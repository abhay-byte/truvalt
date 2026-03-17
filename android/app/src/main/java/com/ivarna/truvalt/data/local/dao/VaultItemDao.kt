package com.ivarna.truvalt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ivarna.truvalt.data.local.entity.VaultItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultItemDao {

    @Query("SELECT * FROM vault_items WHERE deletedAt IS NULL ORDER BY name ASC")
    fun getAllItems(): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items WHERE deletedAt IS NULL AND favorite = 1 ORDER BY name ASC")
    fun getFavoriteItems(): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items WHERE folderId = :folderId AND deletedAt IS NULL ORDER BY name ASC")
    fun getItemsByFolder(folderId: String): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items WHERE deletedAt IS NULL AND type = :type ORDER BY name ASC")
    fun getItemsByType(type: String): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items WHERE id = :id")
    suspend fun getItemById(id: String): VaultItemEntity?

    @Query("SELECT * FROM vault_items WHERE deletedAt IS NULL AND name LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getTrashItems(): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items WHERE syncStatus = :status")
    suspend fun getItemsBySyncStatus(status: String): List<VaultItemEntity>

    @Query("SELECT * FROM vault_items WHERE updatedAt > :since")
    suspend fun getItemsUpdatedSince(since: Long): List<VaultItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: VaultItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<VaultItemEntity>)

    @Update
    suspend fun updateItem(item: VaultItemEntity)

    @Delete
    suspend fun deleteItem(item: VaultItemEntity)

    @Query("DELETE FROM vault_items WHERE id = :id")
    suspend fun deleteItemById(id: String)

    @Query("UPDATE vault_items SET deletedAt = :deletedAt, syncStatus = 'PENDING_UPLOAD' WHERE id = :id")
    suspend fun softDeleteItem(id: String, deletedAt: Long)

    @Query("UPDATE vault_items SET deletedAt = NULL, syncStatus = 'PENDING_UPLOAD' WHERE id = :id")
    suspend fun restoreItem(id: String)

    @Query("UPDATE vault_items SET favorite = :favorite, syncStatus = 'PENDING_UPLOAD' WHERE id = :id")
    suspend fun updateFavorite(id: String, favorite: Boolean)

    @Query("UPDATE vault_items SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String)

    @Query("DELETE FROM vault_items WHERE deletedAt IS NOT NULL AND deletedAt < :before")
    suspend fun emptyTrash(before: Long)

    @Query("SELECT COUNT(*) FROM vault_items WHERE deletedAt IS NULL")
    suspend fun getItemCount(): Int
}
