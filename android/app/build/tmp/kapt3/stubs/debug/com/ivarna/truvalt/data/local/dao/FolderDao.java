package com.ivarna.truvalt.data.local.dao;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\t\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u001c\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\b\u001a\u00020\tH\'J\u0018\u0010\n\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u001c\u0010\u0011\u001a\u00020\u000e2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0016\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0016\u0010\u0016\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\f\u00a8\u0006\u0017"}, d2 = {"Lcom/ivarna/truvalt/data/local/dao/FolderDao;", "", "getAllFolders", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/ivarna/truvalt/data/local/entity/FolderEntity;", "getRootFolders", "getChildFolders", "parentId", "", "getFolderById", "id", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertFolder", "", "folder", "(Lcom/ivarna/truvalt/data/local/entity/FolderEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertFolders", "folders", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateFolder", "deleteFolder", "deleteFolderById", "app_debug"})
@androidx.room.Dao()
public abstract interface FolderDao {
    
    @androidx.room.Query(value = "SELECT * FROM folders ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.data.local.entity.FolderEntity>> getAllFolders();
    
    @androidx.room.Query(value = "SELECT * FROM folders WHERE parentId IS NULL ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.data.local.entity.FolderEntity>> getRootFolders();
    
    @androidx.room.Query(value = "SELECT * FROM folders WHERE parentId = :parentId ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.data.local.entity.FolderEntity>> getChildFolders(@org.jetbrains.annotations.NotNull()
    java.lang.String parentId);
    
    @androidx.room.Query(value = "SELECT * FROM folders WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getFolderById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.ivarna.truvalt.data.local.entity.FolderEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertFolder(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.entity.FolderEntity folder, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertFolders(@org.jetbrains.annotations.NotNull()
    java.util.List<com.ivarna.truvalt.data.local.entity.FolderEntity> folders, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateFolder(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.entity.FolderEntity folder, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteFolder(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.entity.FolderEntity folder, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM folders WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteFolderById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}