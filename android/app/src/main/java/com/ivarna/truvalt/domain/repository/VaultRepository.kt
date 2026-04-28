package com.ivarna.truvalt.domain.repository

import com.ivarna.truvalt.domain.model.Folder
import com.ivarna.truvalt.domain.model.Tag
import com.ivarna.truvalt.domain.model.VaultItem
import com.ivarna.truvalt.domain.model.VaultItemType
import kotlinx.coroutines.flow.Flow

interface VaultRepository {
    fun getAllItems(): Flow<List<VaultItem>>
    fun getFavoriteItems(): Flow<List<VaultItem>>
    fun getItemsByFolder(folderId: String): Flow<List<VaultItem>>
    fun getItemsByType(type: String): Flow<List<VaultItem>>
    fun getTrashItems(): Flow<List<VaultItem>>
    fun searchItems(query: String): Flow<List<VaultItem>>
    
    suspend fun getItemById(id: String): VaultItem?
    suspend fun saveItem(item: VaultItem)
    suspend fun deleteItem(id: String)
    suspend fun softDeleteItem(id: String)
    suspend fun restoreItem(id: String)
    suspend fun emptyTrash()
    suspend fun toggleFavorite(id: String, favorite: Boolean)
    
    fun getAllFolders(): Flow<List<Folder>>
    fun getRootFolders(): Flow<List<Folder>>
    suspend fun saveFolder(folder: Folder)
    suspend fun deleteFolder(id: String)
    
    fun getAllTags(): Flow<List<Tag>>
    suspend fun saveTag(tag: Tag)
    suspend fun deleteTag(id: String)
    suspend fun addTagToItem(itemId: String, tagId: String)
    suspend fun removeTagFromItem(itemId: String, tagId: String)
    fun getTagsForItem(itemId: String): Flow<List<Tag>>
}

interface AuthRepository {
    suspend fun isVaultUnlocked(): Boolean
    suspend fun unlockVault(masterKey: ByteArray)
    suspend fun lockVault()
    suspend fun isBiometricEnabled(): Boolean
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun getEncryptedVaultKey(): ByteArray?
    suspend fun storeVaultKey(encryptedKey: ByteArray)
    suspend fun hasVault(): Boolean
    suspend fun createVault(email: String, password: String): Result<Unit>
    suspend fun unlockWithPassword(email: String, password: String): Result<Unit>
}

interface SyncRepository {
    suspend fun sync(): Result<Unit>
    suspend fun getLastSyncTime(): Long
    suspend fun setLastSyncTime(time: Long)
    suspend fun isOnline(): Boolean
    suspend fun isLocalOnly(): Boolean
    suspend fun setLocalOnly(localOnly: Boolean)
}
