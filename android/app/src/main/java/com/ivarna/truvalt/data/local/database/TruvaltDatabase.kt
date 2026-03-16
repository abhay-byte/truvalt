package com.ivarna.truvalt.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ivarna.truvalt.data.local.dao.FolderDao
import com.ivarna.truvalt.data.local.dao.TagDao
import com.ivarna.truvalt.data.local.dao.VaultItemDao
import com.ivarna.truvalt.data.local.entity.FolderEntity
import com.ivarna.truvalt.data.local.entity.SyncMetadataEntity
import com.ivarna.truvalt.data.local.entity.TagEntity
import com.ivarna.truvalt.data.local.entity.VaultItemEntity
import com.ivarna.truvalt.data.local.entity.VaultItemTagEntity

@Database(
    entities = [
        VaultItemEntity::class,
        FolderEntity::class,
        TagEntity::class,
        VaultItemTagEntity::class,
        SyncMetadataEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TruvaltDatabase : RoomDatabase() {
    abstract fun vaultItemDao(): VaultItemDao
    abstract fun folderDao(): FolderDao
    abstract fun tagDao(): TagDao
}
