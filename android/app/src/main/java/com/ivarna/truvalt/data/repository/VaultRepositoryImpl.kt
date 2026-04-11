package com.ivarna.truvalt.data.repository

import android.util.Log
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.data.local.dao.FolderDao
import com.ivarna.truvalt.data.local.dao.TagDao
import com.ivarna.truvalt.data.local.dao.VaultItemDao
import com.ivarna.truvalt.data.local.entity.FolderEntity
import com.ivarna.truvalt.data.local.entity.TagEntity
import com.ivarna.truvalt.data.local.entity.VaultItemEntity
import com.ivarna.truvalt.data.local.entity.VaultItemTagEntity
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.domain.model.Folder
import com.ivarna.truvalt.domain.model.SyncStatus
import com.ivarna.truvalt.domain.model.Tag
import com.ivarna.truvalt.domain.model.VaultItem
import com.ivarna.truvalt.domain.model.VaultItemType
import com.ivarna.truvalt.domain.repository.SyncRepository
import com.ivarna.truvalt.domain.repository.VaultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultRepositoryImpl @Inject constructor(
    private val vaultItemDao: VaultItemDao,
    private val folderDao: FolderDao,
    private val tagDao: TagDao,
    private val cryptoManager: CryptoManager,
    private val preferences: TruvaltPreferences,
    private val vaultKeyManager: VaultKeyManager,
    private val syncRepository: SyncRepository,
) : VaultRepository {

    private var vaultKey: ByteArray? = null
    private val vaultKeyVersion = MutableStateFlow(0)

    fun setVaultKey(key: ByteArray) {
        Log.d("VaultRepository", "setVaultKey called, key size: ${key.size}")
        vaultKey = key
        vaultKeyVersion.value += 1
    }

    fun clearVaultKey() {
        Log.d("VaultRepository", "clearVaultKey called")
        vaultKey = null
        vaultKeyVersion.value += 1
    }
    
    private fun getVaultKey(): ByteArray {
        // Try memory cache first
        if (vaultKey != null) {
            Log.d("VaultRepository", "getVaultKey: Using cached key (${vaultKey!!.size} bytes)")
            return vaultKey!!
        }
        
        // Try to get from VaultKeyManager
        val keyFromManager = vaultKeyManager.getInMemoryKey()
        if (keyFromManager != null) {
            Log.d("VaultRepository", "getVaultKey: Retrieved from VaultKeyManager (${keyFromManager.size} bytes)")
            vaultKey = keyFromManager
            return keyFromManager
        }
        
        Log.e("VaultRepository", "getVaultKey: No key available - vault not unlocked!")
        throw IllegalStateException("Vault not unlocked")
    }

    override fun getAllItems(): Flow<List<VaultItem>> {
        return observeDecryptedItems(vaultItemDao.getAllItems())
    }

    override fun getFavoriteItems(): Flow<List<VaultItem>> {
        return observeDecryptedItems(vaultItemDao.getFavoriteItems())
    }

    override fun getItemsByFolder(folderId: String): Flow<List<VaultItem>> {
        return observeDecryptedItems(vaultItemDao.getItemsByFolder(folderId))
    }

    override fun getItemsByType(type: String): Flow<List<VaultItem>> {
        return observeDecryptedItems(vaultItemDao.getItemsByType(type))
    }

    override fun getTrashItems(): Flow<List<VaultItem>> {
        return observeDecryptedItems(vaultItemDao.getTrashItems())
    }

    override fun searchItems(query: String): Flow<List<VaultItem>> {
        return observeDecryptedItems(vaultItemDao.searchItems(query))
    }

    override suspend fun getItemById(id: String): VaultItem? {
        return vaultItemDao.getItemById(id)?.toDomain()
    }

    override suspend fun saveItem(item: VaultItem) {
        val updatedItem = item.copy(
            updatedAt = System.currentTimeMillis(),
            syncStatus = SyncStatus.PENDING_UPLOAD
        )
        val entity = updatedItem.toEntity()
        vaultItemDao.insertItem(entity)
        syncPendingChanges("saveItem")
    }

    override suspend fun deleteItem(id: String) {
        vaultItemDao.deleteItemById(id)
        syncPendingChanges("deleteItem")
    }

    override suspend fun softDeleteItem(id: String) {
        vaultItemDao.softDeleteItem(id, System.currentTimeMillis())
        syncPendingChanges("softDeleteItem")
    }

    override suspend fun restoreItem(id: String) {
        vaultItemDao.restoreItem(id)
        syncPendingChanges("restoreItem")
    }

    override suspend fun emptyTrash() {
        vaultItemDao.emptyTrash(System.currentTimeMillis())
        syncPendingChanges("emptyTrash")
    }

    override suspend fun toggleFavorite(id: String, favorite: Boolean) {
        vaultItemDao.updateFavorite(id, favorite)
        syncPendingChanges("toggleFavorite")
    }

    override fun getAllFolders(): Flow<List<Folder>> {
        return folderDao.getAllFolders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRootFolders(): Flow<List<Folder>> {
        return folderDao.getRootFolders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveFolder(folder: Folder) {
        val updatedFolder = folder.copy(
            updatedAt = System.currentTimeMillis(),
            syncStatus = SyncStatus.PENDING_UPLOAD
        )
        folderDao.insertFolder(updatedFolder.toEntity())
        syncPendingChanges("saveFolder")
    }

    override suspend fun deleteFolder(id: String) {
        folderDao.softDeleteFolder(id, System.currentTimeMillis())
        syncPendingChanges("deleteFolder")
    }

    override fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveTag(tag: Tag) {
        val updatedTag = tag.copy(
            updatedAt = System.currentTimeMillis(),
            syncStatus = SyncStatus.PENDING_UPLOAD
        )
        tagDao.insertTag(updatedTag.toEntity())
        syncPendingChanges("saveTag")
    }

    override suspend fun deleteTag(id: String) {
        tagDao.softDeleteTag(id, System.currentTimeMillis())
        syncPendingChanges("deleteTag")
    }

    override suspend fun addTagToItem(itemId: String, tagId: String) {
        tagDao.addTagToItem(VaultItemTagEntity(itemId, tagId))
        syncPendingChanges("addTagToItem")
    }

    override suspend fun removeTagFromItem(itemId: String, tagId: String) {
        tagDao.removeTagFromItem(VaultItemTagEntity(itemId, tagId))
        syncPendingChanges("removeTagFromItem")
    }

    override fun getTagsForItem(itemId: String): Flow<List<Tag>> {
        return tagDao.getTagsForItem(itemId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun observeDecryptedItems(source: Flow<List<VaultItemEntity>>): Flow<List<VaultItem>> {
        return source.combine(vaultKeyVersion) { entities, _ ->
            entities.mapNotNull { it.toDomain() }
        }
    }

    private suspend fun syncPendingChanges(reason: String) {
        val result = runCatching { syncRepository.sync() }
            .getOrElse { error ->
                Log.w("VaultRepository", "Auto-sync crashed after $reason", error)
                return
            }

        result.exceptionOrNull()?.let { error ->
            Log.w("VaultRepository", "Auto-sync skipped after $reason: ${error.message}")
        }
    }

    private fun VaultItemEntity.toDomain(): VaultItem? {
        return try {
            val key = getVaultKey()
            val decryptedData = cryptoManager.decryptVaultItem(
                com.ivarna.truvalt.core.crypto.EncryptedBlob(
                    iv = encryptedData.take(12).toByteArray(),
                    ciphertext = encryptedData.drop(12).toByteArray()
                ),
                key
            )
            VaultItem(
                id = id,
                type = type,
                name = name,
                folderId = folderId,
                encryptedData = decryptedData,
                favorite = favorite,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt,
                syncStatus = SyncStatus.valueOf(syncStatus)
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun VaultItem.toEntity(): VaultItemEntity {
        val key = getVaultKey()
        val blob = cryptoManager.encryptVaultItem(encryptedData, key)
        return VaultItemEntity(
            id = id,
            type = type,
            name = name,
            folderId = folderId,
            encryptedData = blob.iv + blob.ciphertext,
            favorite = favorite,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
            syncStatus = syncStatus.name
        )
    }

    private fun FolderEntity.toDomain(): Folder = Folder(
        id = id,
        name = name,
        icon = icon,
        parentId = parentId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        syncStatus = SyncStatus.valueOf(syncStatus)
    )

    private fun Folder.toEntity(): FolderEntity = FolderEntity(
        id = id,
        name = name,
        icon = icon,
        parentId = parentId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        syncStatus = syncStatus.name
    )

    private fun TagEntity.toDomain(): Tag = Tag(
        id = id,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        syncStatus = SyncStatus.valueOf(syncStatus)
    )

    private fun Tag.toEntity(): TagEntity = TagEntity(
        id = id,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        syncStatus = syncStatus.name
    )
}
