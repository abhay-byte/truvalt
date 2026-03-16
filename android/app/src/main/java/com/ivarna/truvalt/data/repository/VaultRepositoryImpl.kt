package com.ivarna.truvalt.data.repository

import com.ivarna.truvalt.core.crypto.CryptoManager
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
import com.ivarna.truvalt.domain.repository.VaultRepository
import kotlinx.coroutines.flow.Flow
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
    private val preferences: TruvaltPreferences
) : VaultRepository {

    private var vaultKey: ByteArray? = null

    fun setVaultKey(key: ByteArray) {
        vaultKey = key
    }

    fun clearVaultKey() {
        vaultKey = null
    }

    override fun getAllItems(): Flow<List<VaultItem>> {
        return vaultItemDao.getAllItems().map { entities ->
            entities.mapNotNull { it.toDomain() }
        }
    }

    override fun getFavoriteItems(): Flow<List<VaultItem>> {
        return vaultItemDao.getFavoriteItems().map { entities ->
            entities.mapNotNull { it.toDomain() }
        }
    }

    override fun getItemsByFolder(folderId: String): Flow<List<VaultItem>> {
        return vaultItemDao.getItemsByFolder(folderId).map { entities ->
            entities.mapNotNull { it.toDomain() }
        }
    }

    override fun getItemsByType(type: VaultItemType): Flow<List<VaultItem>> {
        return vaultItemDao.getItemsByType(type.name).map { entities ->
            entities.mapNotNull { it.toDomain() }
        }
    }

    override fun getTrashItems(): Flow<List<VaultItem>> {
        return vaultItemDao.getTrashItems().map { entities ->
            entities.mapNotNull { it.toDomain() }
        }
    }

    override fun searchItems(query: String): Flow<List<VaultItem>> {
        return vaultItemDao.searchItems(query).map { entities ->
            entities.mapNotNull { it.toDomain() }
        }
    }

    override suspend fun getItemById(id: String): VaultItem? {
        return vaultItemDao.getItemById(id)?.toDomain()
    }

    override suspend fun saveItem(item: VaultItem) {
        val entity = item.toEntity()
        vaultItemDao.insertItem(entity)
    }

    override suspend fun deleteItem(id: String) {
        vaultItemDao.deleteItemById(id)
    }

    override suspend fun softDeleteItem(id: String) {
        vaultItemDao.softDeleteItem(id, System.currentTimeMillis())
    }

    override suspend fun restoreItem(id: String) {
        vaultItemDao.restoreItem(id)
    }

    override suspend fun emptyTrash() {
        vaultItemDao.emptyTrash(System.currentTimeMillis())
    }

    override suspend fun toggleFavorite(id: String, favorite: Boolean) {
        vaultItemDao.updateFavorite(id, favorite)
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
        folderDao.insertFolder(folder.toEntity())
    }

    override suspend fun deleteFolder(id: String) {
        folderDao.deleteFolderById(id)
    }

    override fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveTag(tag: Tag) {
        tagDao.insertTag(tag.toEntity())
    }

    override suspend fun deleteTag(id: String) {
        tagDao.deleteTagById(id)
    }

    override suspend fun addTagToItem(itemId: String, tagId: String) {
        tagDao.addTagToItem(VaultItemTagEntity(itemId, tagId))
    }

    override suspend fun removeTagFromItem(itemId: String, tagId: String) {
        tagDao.removeTagFromItem(VaultItemTagEntity(itemId, tagId))
    }

    override fun getTagsForItem(itemId: String): Flow<List<Tag>> {
        return tagDao.getTagsForItem(itemId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun VaultItemEntity.toDomain(): VaultItem? {
        val key = vaultKey ?: return null
        return try {
            val decryptedData = cryptoManager.decryptVaultItem(
                com.ivarna.truvalt.core.crypto.EncryptedBlob(
                    iv = encryptedData.take(12).toByteArray(),
                    ciphertext = encryptedData.drop(12).toByteArray()
                ),
                key
            )
            VaultItem(
                id = id,
                type = VaultItemType.valueOf(type),
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
        val key = vaultKey ?: throw IllegalStateException("Vault not unlocked")
        val blob = cryptoManager.encryptVaultItem(encryptedData, key)
        return VaultItemEntity(
            id = id,
            type = type.name,
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
        updatedAt = updatedAt
    )

    private fun Folder.toEntity(): FolderEntity = FolderEntity(
        id = id,
        name = name,
        icon = icon,
        parentId = parentId,
        updatedAt = updatedAt
    )

    private fun TagEntity.toDomain(): Tag = Tag(
        id = id,
        name = name
    )

    private fun Tag.toEntity(): TagEntity = TagEntity(
        id = id,
        name = name
    )
}
