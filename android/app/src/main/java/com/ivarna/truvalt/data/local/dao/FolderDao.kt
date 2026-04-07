package com.ivarna.truvalt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ivarna.truvalt.data.local.entity.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    @Query("SELECT * FROM folders WHERE deletedAt IS NULL ORDER BY name ASC")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE deletedAt IS NULL ORDER BY name ASC")
    suspend fun getAllFoldersNow(): List<FolderEntity>

    @Query("SELECT * FROM folders WHERE parentId IS NULL AND deletedAt IS NULL ORDER BY name ASC")
    fun getRootFolders(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE parentId = :parentId AND deletedAt IS NULL ORDER BY name ASC")
    fun getChildFolders(parentId: String): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE id = :id")
    suspend fun getFolderById(id: String): FolderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolders(folders: List<FolderEntity>)

    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Delete
    suspend fun deleteFolder(folder: FolderEntity)

    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteFolderById(id: String)

    @Query("SELECT * FROM folders WHERE syncStatus = :status")
    suspend fun getFoldersBySyncStatus(status: String): List<FolderEntity>

    @Query("UPDATE folders SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String)

    @Query("UPDATE folders SET deletedAt = :deletedAt, syncStatus = 'PENDING_UPLOAD' WHERE id = :id")
    suspend fun softDeleteFolder(id: String, deletedAt: Long)
}
