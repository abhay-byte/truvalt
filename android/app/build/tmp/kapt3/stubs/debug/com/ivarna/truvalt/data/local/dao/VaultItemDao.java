package com.ivarna.truvalt.data.local.dao;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u001c\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\b\u001a\u00020\tH\'J\u001c\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u000b\u001a\u00020\tH\'J\u0018\u0010\f\u001a\u0004\u0018\u00010\u00052\u0006\u0010\r\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0010\u001a\u00020\tH\'J\u0014\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u001c\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0013\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0015\u001a\u00020\u0016H\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u001bJ\u001c\u0010\u001c\u001a\u00020\u00192\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u001eJ\u0016\u0010\u001f\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u001bJ\u0016\u0010 \u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u001bJ\u0016\u0010!\u001a\u00020\u00192\u0006\u0010\r\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u001e\u0010\"\u001a\u00020\u00192\u0006\u0010\r\u001a\u00020\t2\u0006\u0010#\u001a\u00020\u0016H\u00a7@\u00a2\u0006\u0002\u0010$J\u0016\u0010%\u001a\u00020\u00192\u0006\u0010\r\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u001e\u0010&\u001a\u00020\u00192\u0006\u0010\r\u001a\u00020\t2\u0006\u0010\'\u001a\u00020(H\u00a7@\u00a2\u0006\u0002\u0010)J\u001e\u0010*\u001a\u00020\u00192\u0006\u0010\r\u001a\u00020\t2\u0006\u0010\u0013\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010+J\u0016\u0010,\u001a\u00020\u00192\u0006\u0010-\u001a\u00020\u0016H\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u000e\u0010.\u001a\u00020/H\u00a7@\u00a2\u0006\u0002\u00100\u00a8\u00061"}, d2 = {"Lcom/ivarna/truvalt/data/local/dao/VaultItemDao;", "", "getAllItems", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/ivarna/truvalt/data/local/entity/VaultItemEntity;", "getFavoriteItems", "getItemsByFolder", "folderId", "", "getItemsByType", "type", "getItemById", "id", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchItems", "query", "getTrashItems", "getItemsBySyncStatus", "status", "getItemsUpdatedSince", "since", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertItem", "", "item", "(Lcom/ivarna/truvalt/data/local/entity/VaultItemEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertItems", "items", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateItem", "deleteItem", "deleteItemById", "softDeleteItem", "deletedAt", "(Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "restoreItem", "updateFavorite", "favorite", "", "(Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateSyncStatus", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "emptyTrash", "before", "getItemCount", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface VaultItemDao {
    
    @androidx.room.Query(value = "SELECT * FROM vault_items WHERE deletedAt IS NULL ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.data.local.entity.VaultItemEntity>> getAllItems();
    
    @androidx.room.Query(value = "SELECT * FROM vault_items WHERE deletedAt IS NULL AND favorite = 1 ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.data.local.entity.VaultItemEntity>> getFavoriteItems();
    
    @androidx.room.Query(value = "SELECT * FROM vault_items WHERE folderId = :folderId AND deletedAt IS NULL ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.data.local.entity.VaultItemEntity>> getItemsByFolder(@org.jetbrains.annotations.NotNull()
    java.lang.String folderId);
    
    @androidx.room.Query(value = "SELECT * FROM vault_items WHERE deletedAt IS NULL AND type = :type ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.data.local.entity.VaultItemEntity>> getItemsByType(@org.jetbrains.annotations.NotNull()
    java.lang.String type);
    
    @androidx.room.Query(value = "SELECT * FROM vault_items WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getItemById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.ivarna.truvalt.data.local.entity.VaultItemEntity> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM vault_items WHERE deletedAt IS NULL AND name LIKE \'%\' || :query || \'%\'")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.data.local.entity.VaultItemEntity>> searchItems(@org.jetbrains.annotations.NotNull()
    java.lang.String query);
    
    @androidx.room.Query(value = "SELECT * FROM vault_items WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.data.local.entity.VaultItemEntity>> getTrashItems();
    
    @androidx.room.Query(value = "SELECT * FROM vault_items WHERE syncStatus = :status")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getItemsBySyncStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String status, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.ivarna.truvalt.data.local.entity.VaultItemEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM vault_items WHERE updatedAt > :since")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getItemsUpdatedSince(long since, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.ivarna.truvalt.data.local.entity.VaultItemEntity>> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertItem(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.entity.VaultItemEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertItems(@org.jetbrains.annotations.NotNull()
    java.util.List<com.ivarna.truvalt.data.local.entity.VaultItemEntity> items, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateItem(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.entity.VaultItemEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteItem(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.entity.VaultItemEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM vault_items WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteItemById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE vault_items SET deletedAt = :deletedAt, syncStatus = \'PENDING_UPLOAD\' WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object softDeleteItem(@org.jetbrains.annotations.NotNull()
    java.lang.String id, long deletedAt, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE vault_items SET deletedAt = NULL, syncStatus = \'PENDING_UPLOAD\' WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object restoreItem(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE vault_items SET favorite = :favorite, syncStatus = \'PENDING_UPLOAD\' WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateFavorite(@org.jetbrains.annotations.NotNull()
    java.lang.String id, boolean favorite, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE vault_items SET syncStatus = :status WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateSyncStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String status, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM vault_items WHERE deletedAt IS NOT NULL AND deletedAt < :before")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object emptyTrash(long before, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM vault_items WHERE deletedAt IS NULL")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getItemCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}