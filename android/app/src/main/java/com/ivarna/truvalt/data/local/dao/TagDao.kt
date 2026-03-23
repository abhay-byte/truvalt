package com.ivarna.truvalt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivarna.truvalt.data.local.entity.TagEntity
import com.ivarna.truvalt.data.local.entity.VaultItemTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags ORDER BY name ASC")
    suspend fun getAllTagsNow(): List<TagEntity>

    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun getTagById(id: String): TagEntity?

    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun getTagByName(name: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Query("DELETE FROM tags WHERE id = :id")
    suspend fun deleteTagById(id: String)

    @Query("SELECT tags.* FROM tags INNER JOIN vault_item_tags ON tags.id = vault_item_tags.tagId WHERE vault_item_tags.itemId = :itemId")
    fun getTagsForItem(itemId: String): Flow<List<TagEntity>>

    @Query("SELECT vault_item_tags.itemId FROM vault_item_tags WHERE vault_item_tags.tagId = :tagId")
    fun getItemsForTag(tagId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTagToItem(itemTag: VaultItemTagEntity)

    @Delete
    suspend fun removeTagFromItem(itemTag: VaultItemTagEntity)

    @Query("DELETE FROM vault_item_tags WHERE itemId = :itemId")
    suspend fun removeAllTagsFromItem(itemId: String)
}
