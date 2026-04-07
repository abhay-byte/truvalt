package com.ivarna.truvalt.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Base64
import com.google.firebase.auth.FirebaseAuth
import com.ivarna.truvalt.data.local.dao.FolderDao
import com.ivarna.truvalt.data.local.dao.TagDao
import com.ivarna.truvalt.data.local.dao.VaultItemDao
import com.ivarna.truvalt.data.local.entity.FolderEntity
import com.ivarna.truvalt.data.local.entity.TagEntity
import com.ivarna.truvalt.data.local.entity.VaultItemEntity
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.remote.FirestoreVaultRepository
import com.ivarna.truvalt.domain.repository.SyncRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: TruvaltPreferences,
    private val vaultItemDao: VaultItemDao,
    private val folderDao: FolderDao,
    private val tagDao: TagDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestoreRepository: FirestoreVaultRepository,
) : SyncRepository {

    override suspend fun sync(): Result<Unit> {
        if (isLocalOnly()) return Result.failure(IllegalStateException("Local-only mode"))
        if (!isOnline()) return Result.failure(IllegalStateException("No internet connection"))

        val uid = firebaseAuth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Not signed in to Firebase"))

        return try {
            withContext(Dispatchers.IO) {
                // 1. Push local changes
                pushFolders(uid)
                pushTags(uid)
                pushVaultItems(uid)

                // 2. Pull remote changes
                pullRemoteState(uid)
            }
            preferences.setLastSyncTime(System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SyncRepository", "Sync failed", e)
            Result.failure(e)
        }
    }

    override suspend fun getLastSyncTime(): Long = preferences.lastSyncTime.first()

    override suspend fun setLastSyncTime(time: Long) = preferences.setLastSyncTime(time)

    override suspend fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override suspend fun isLocalOnly(): Boolean = preferences.isLocalOnlySync()

    override suspend fun setLocalOnly(localOnly: Boolean) = preferences.setLocalOnly(localOnly)

    override suspend fun getServerUrl(): String? = preferences.getServerUrlSync()

    override suspend fun setServerUrl(url: String) = preferences.setServerUrl(url)

    // ── Push local folders → Firestore ────────────────────────────────────────

    private suspend fun pushFolders(uid: String) {
        val pending = folderDao.getFoldersBySyncStatus("PENDING_UPLOAD")
        if (pending.isEmpty()) return

        val payload = pending.map { folder ->
            mapOf(
                "id" to folder.id,
                "name" to folder.name,
                "icon" to folder.icon,
                "parent_id" to folder.parentId,
                "created_at" to toSeconds(folder.createdAt),
                "updated_at" to toSeconds(folder.updatedAt),
                "deleted_at" to folder.deletedAt?.let(::toSeconds),
            )
        }

        // Batch sync folders (similar to vault items)
        for (data in payload) {
            val folderId = data["id"] as String
            val incoming = (data["updated_at"] as Long)
            val existing = firestoreRepository.getFolder(uid, folderId)

            if (existing != null) {
                val serverUpdatedAt = ((existing["updated_at"] as? Number)?.toLong() ?: 0L)
                if (serverUpdatedAt > incoming) {
                    // Conflict: Server is newer. Will be updated during pull phase.
                    continue
                }
            }
            firestoreRepository.saveFolder(uid, data)
            folderDao.updateSyncStatus(folderId, "SYNCED")
        }
    }

    // ── Push local tags → Firestore ───────────────────────────────────────────

    private suspend fun pushTags(uid: String) {
        val pending = tagDao.getTagsBySyncStatus("PENDING_UPLOAD")
        if (pending.isEmpty()) return

        val payload = pending.map { tag ->
            mapOf(
                "id" to tag.id,
                "name" to tag.name,
                "created_at" to toSeconds(tag.createdAt),
                "updated_at" to toSeconds(tag.updatedAt),
                "deleted_at" to tag.deletedAt?.let(::toSeconds),
            )
        }

        for (data in payload) {
            val tagId = data["id"] as String
            val incoming = (data["updated_at"] as Long)
            val existing = firestoreRepository.getTags(uid).find { it["id"] == tagId } // No getTagById for tags in repo yet, using list

            if (existing != null) {
                val serverUpdatedAt = ((existing["updated_at"] as? Number)?.toLong() ?: 0L)
                if (serverUpdatedAt > incoming) {
                    continue
                }
            }
            firestoreRepository.saveTag(uid, data)
            tagDao.updateSyncStatus(tagId, "SYNCED")
        }
    }

    // ── Push pending vault items → Firestore (batch sync) ────────────────────

    private suspend fun pushVaultItems(uid: String) {
        val pendingUpload = vaultItemDao.getItemsBySyncStatus("PENDING_UPLOAD")
        val pendingDelete = vaultItemDao.getItemsBySyncStatus("PENDING_DELETE")
        val pendingItems = (pendingUpload + pendingDelete).distinctBy { it.id }

        if (pendingItems.isEmpty()) return

        val payload = pendingItems.map { item ->
            mapOf(
                "id" to item.id,
                "type" to item.type,
                "name" to item.name,
                "encrypted_data" to Base64.encodeToString(item.encryptedData, Base64.NO_WRAP),
                "folder_id" to item.folderId,
                "favorite" to item.favorite,
                "created_at" to toSeconds(item.createdAt),
                "updated_at" to toSeconds(item.updatedAt),
                "deleted_at" to item.deletedAt?.let(::toSeconds),
            )
        }

        val (synced, conflicts) = firestoreRepository.syncVaultItems(uid, payload)
        val merged = (synced + conflicts).distinctBy { it["id"] }
        if (merged.isNotEmpty()) {
            vaultItemDao.insertItems(merged.map { it.toEntity() })
            merged.forEach { vaultItemDao.updateSyncStatus(it["id"] as String, "SYNCED") }
        }
    }

    // ── Pull full remote state → Room ─────────────────────────────────────────

    private suspend fun pullRemoteState(uid: String) {
        val lastSyncMs = preferences.lastSyncTime.first()
        val updatedAfterSeconds = if (lastSyncMs > 0L) toSeconds(lastSyncMs) else null

        // 1. Sync Vault Items
        val activeItems = firestoreRepository.getVaultItems(uid, updatedAfterSeconds)
        val trashItems = firestoreRepository.getTrashedItems(uid)
        val allRemoteItems = (activeItems + trashItems).distinctBy { it["id"] }

        if (allRemoteItems.isNotEmpty()) {
            vaultItemDao.insertItems(allRemoteItems.map { it.toEntity() })
            allRemoteItems.forEach { vaultItemDao.updateSyncStatus(it["id"] as String, "SYNCED") }
        }

        // 2. Sync Folders
        val activeFolders = firestoreRepository.getFolders(uid, updatedAfterSeconds)
        val trashFolders = firestoreRepository.getTrashedFolders(uid)
        val allRemoteFolders = (activeFolders + trashFolders).distinctBy { it["id"] }

        if (allRemoteFolders.isNotEmpty()) {
            folderDao.insertFolders(allRemoteFolders.map { it.toFolderEntity() })
            allRemoteFolders.forEach { folderDao.updateSyncStatus(it["id"] as String, "SYNCED") }
        }

        // 3. Sync Tags
        val activeTags = firestoreRepository.getTags(uid, updatedAfterSeconds)
        val trashTags = firestoreRepository.getTrashedTags(uid)
        val allRemoteTags = (activeTags + trashTags).distinctBy { it["id"] }

        if (allRemoteTags.isNotEmpty()) {
            tagDao.insertTags(allRemoteTags.map { it.toTagEntity() })
            allRemoteTags.forEach { tagDao.updateSyncStatus(it["id"] as String, "SYNCED") }
        }
    }

    // ── Converters ────────────────────────────────────────────────────────────

    private fun toSeconds(ms: Long): Long = ms / 1000L

    private fun fromSeconds(seconds: Long): Long = seconds * 1000L

    private fun Map<String, Any?>.toTagEntity(): TagEntity {
        return TagEntity(
            id = this["id"] as String,
            name = this["name"] as? String ?: "",
            createdAt = fromSeconds((this["created_at"] as? Number)?.toLong() ?: 0L),
            updatedAt = fromSeconds((this["updated_at"] as? Number)?.toLong() ?: 0L),
            deletedAt = (this["deleted_at"] as? Number)?.toLong()?.let(::fromSeconds),
            syncStatus = "SYNCED",
        )
    }

    private fun Map<String, Any?>.toFolderEntity(): FolderEntity {
        return FolderEntity(
            id = this["id"] as String,
            name = this["name"] as? String ?: "",
            icon = this["icon"] as? String,
            parentId = this["parent_id"] as? String,
            createdAt = fromSeconds((this["created_at"] as? Number)?.toLong() ?: 0L),
            updatedAt = fromSeconds((this["updated_at"] as? Number)?.toLong() ?: 0L),
            deletedAt = (this["deleted_at"] as? Number)?.toLong()?.let(::fromSeconds),
            syncStatus = "SYNCED",
        )
    }

    private fun Map<String, Any?>.toEntity(): VaultItemEntity {
        val encryptedDataStr = this["encrypted_data"] as? String ?: ""
        return VaultItemEntity(
            id = this["id"] as String,
            type = this["type"] as? String ?: "",
            name = this["name"] as? String ?: "",
            folderId = this["folder_id"] as? String,
            encryptedData = if (encryptedDataStr.isNotEmpty())
                Base64.decode(encryptedDataStr, Base64.DEFAULT)
            else
                ByteArray(0),
            favorite = this["favorite"] as? Boolean ?: false,
            createdAt = fromSeconds((this["created_at"] as? Number)?.toLong() ?: 0L),
            updatedAt = fromSeconds((this["updated_at"] as? Number)?.toLong() ?: 0L),
            deletedAt = (this["deleted_at"] as? Number)?.toLong()?.let(::fromSeconds),
            syncStatus = "SYNCED",
        )
    }
}
