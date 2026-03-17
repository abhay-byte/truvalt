package com.ivarna.truvalt.data.local.database;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&\u00a8\u0006\n"}, d2 = {"Lcom/ivarna/truvalt/data/local/database/TruvaltDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "vaultItemDao", "Lcom/ivarna/truvalt/data/local/dao/VaultItemDao;", "folderDao", "Lcom/ivarna/truvalt/data/local/dao/FolderDao;", "tagDao", "Lcom/ivarna/truvalt/data/local/dao/TagDao;", "app_debug"})
@androidx.room.Database(entities = {com.ivarna.truvalt.data.local.entity.VaultItemEntity.class, com.ivarna.truvalt.data.local.entity.FolderEntity.class, com.ivarna.truvalt.data.local.entity.TagEntity.class, com.ivarna.truvalt.data.local.entity.VaultItemTagEntity.class, com.ivarna.truvalt.data.local.entity.SyncMetadataEntity.class}, version = 1, exportSchema = false)
public abstract class TruvaltDatabase extends androidx.room.RoomDatabase {
    
    public TruvaltDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.ivarna.truvalt.data.local.dao.VaultItemDao vaultItemDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.ivarna.truvalt.data.local.dao.FolderDao folderDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.ivarna.truvalt.data.local.dao.TagDao tagDao();
}