package com.ivarna.truvalt.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Base64
import com.ivarna.truvalt.data.local.dao.FolderDao
import com.ivarna.truvalt.data.local.dao.TagDao
import com.ivarna.truvalt.data.local.dao.VaultItemDao
import com.ivarna.truvalt.data.local.entity.FolderEntity
import com.ivarna.truvalt.data.local.entity.TagEntity
import com.ivarna.truvalt.data.local.entity.VaultItemEntity
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.remote.api.BackendApiFactory
import com.ivarna.truvalt.data.remote.api.TruvaltApiService
import com.ivarna.truvalt.data.remote.dto.BackendFolderRequest
import com.ivarna.truvalt.data.remote.dto.BackendSyncRequest
import com.ivarna.truvalt.data.remote.dto.BackendTagRequest
import com.ivarna.truvalt.data.remote.dto.BackendVaultItemDto
import com.ivarna.truvalt.data.remote.dto.BackendVaultItemPayload
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
    private val backendApiFactory: BackendApiFactory
) : SyncRepository {

    override suspend fun sync(): Result<Unit> {
        return if (isOnline() && !isLocalOnly()) {
            try {
                val serverUrl = preferences.getServerUrlSync()
                    ?: return Result.failure(IllegalStateException("Server URL not configured"))
                val idToken = preferences.getBackendIdTokenSync()
                    ?: return Result.failure(IllegalStateException("No backend session found. Please log in again."))
                val api = backendApiFactory.create(serverUrl)
                val bearer = "Bearer $idToken"
                val lastSyncTimeMs = preferences.lastSyncTime.first()

                withContext(Dispatchers.IO) {
                    pushFolders(api, bearer)
                    pushTags(api, bearer)
                    pushVaultItems(api, bearer)
                    pullRemoteState(api, bearer, lastSyncTimeMs)
                }

                preferences.setLastSyncTime(System.currentTimeMillis())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(IllegalStateException("Offline or local-only mode"))
        }
    }

    override suspend fun getLastSyncTime(): Long {
        return preferences.lastSyncTime.first()
    }

    override suspend fun setLastSyncTime(time: Long) {
        preferences.setLastSyncTime(time)
    }

    override suspend fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override suspend fun isLocalOnly(): Boolean {
        return preferences.isLocalOnlySync()
    }

    override suspend fun setLocalOnly(localOnly: Boolean) {
        preferences.setLocalOnly(localOnly)
    }

    override suspend fun getServerUrl(): String? {
        return preferences.getServerUrlSync()
    }

    override suspend fun setServerUrl(url: String) {
        preferences.setServerUrl(backendApiFactory.normalizeBaseUrl(url).trimEnd('/'))
    }

    private suspend fun pushFolders(api: TruvaltApiService, bearer: String) {
        val remoteFolders = api.getFolders(bearer).associateBy { it.id }
        val localFolders = folderDao.getAllFoldersNow()

        localFolders.forEach { folder ->
            val request = BackendFolderRequest(
                id = folder.id,
                name = folder.name,
                icon = folder.icon,
                parent_id = folder.parentId
            )

            if (remoteFolders.containsKey(folder.id)) {
                api.updateFolder(bearer, folder.id, request)
            } else {
                api.createFolder(bearer, request)
            }
        }

        val refreshedFolders = api.getFolders(bearer).map { remote ->
            FolderEntity(
                id = remote.id,
                name = remote.name,
                icon = remote.icon,
                parentId = remote.parent_id,
                updatedAt = fromApiSeconds(remote.updated_at)
            )
        }
        folderDao.insertFolders(refreshedFolders)
    }

    private suspend fun pushTags(api: TruvaltApiService, bearer: String) {
        val remoteTags = api.getTags(bearer).associateBy { it.id }
        val localTags = tagDao.getAllTagsNow()

        localTags.forEach { tag ->
            if (!remoteTags.containsKey(tag.id)) {
                api.createTag(
                    bearer,
                    BackendTagRequest(
                        id = tag.id,
                        name = tag.name
                    )
                )
            }
        }

        val refreshedTags = api.getTags(bearer).map { remote ->
            TagEntity(
                id = remote.id,
                name = remote.name
            )
        }
        tagDao.insertTags(refreshedTags)
    }

    private suspend fun pushVaultItems(api: TruvaltApiService, bearer: String) {
        val pendingUpload = vaultItemDao.getItemsBySyncStatus("PENDING_UPLOAD")
        val pendingDelete = vaultItemDao.getItemsBySyncStatus("PENDING_DELETE")
        val pendingItems = (pendingUpload + pendingDelete).distinctBy { it.id }

        if (pendingItems.isEmpty()) {
            return
        }

        val response = api.syncVaultItems(
            bearer,
            BackendSyncRequest(
                items = pendingItems.map { it.toBackendPayload() }
            )
        )

        val merged = (response.synced + response.conflicts).distinctBy { it.id }
        if (merged.isNotEmpty()) {
            vaultItemDao.insertItems(merged.map { it.toEntity() })
            merged.forEach { vaultItemDao.updateSyncStatus(it.id, "SYNCED") }
        }
    }

    private suspend fun pullRemoteState(api: TruvaltApiService, bearer: String, lastSyncTimeMs: Long) {
        val updatedAfterSeconds = lastSyncTimeMs.takeIf { it > 0L }?.let(::toApiSeconds)
        val activeItems = api.getVaultItems(bearer, updatedAfterSeconds)
        val trashItems = api.getTrashItems(bearer)
        val allRemoteItems = (activeItems + trashItems).distinctBy { it.id }

        if (allRemoteItems.isNotEmpty()) {
            vaultItemDao.insertItems(allRemoteItems.map { it.toEntity() })
            allRemoteItems.forEach { vaultItemDao.updateSyncStatus(it.id, "SYNCED") }
        }
    }

    private fun VaultItemEntity.toBackendPayload(): BackendVaultItemPayload {
        return BackendVaultItemPayload(
            id = id,
            type = type,
            name = name,
            encrypted_data = Base64.encodeToString(encryptedData, Base64.NO_WRAP),
            updated_at = toApiSeconds(updatedAt),
            created_at = toApiSeconds(createdAt),
            folder_id = folderId,
            favorite = favorite,
            deleted_at = deletedAt?.let(::toApiSeconds)
        )
    }

    private fun BackendVaultItemDto.toEntity(): VaultItemEntity {
        return VaultItemEntity(
            id = id,
            type = type,
            name = name,
            folderId = folder_id,
            encryptedData = Base64.decode(encrypted_data, Base64.DEFAULT),
            favorite = favorite,
            createdAt = fromApiSeconds(created_at),
            updatedAt = fromApiSeconds(updated_at),
            deletedAt = deleted_at?.let(::fromApiSeconds),
            syncStatus = "SYNCED"
        )
    }

    private fun toApiSeconds(valueMs: Long): Long = valueMs / 1000L

    private fun fromApiSeconds(valueSeconds: Long): Long = valueSeconds * 1000L
}
