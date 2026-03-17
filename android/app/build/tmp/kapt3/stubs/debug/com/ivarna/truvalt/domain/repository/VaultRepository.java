package com.ivarna.truvalt.domain.repository;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000b\bf\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u0014\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u001c\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\b\u001a\u00020\tH&J\u001c\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u000b\u001a\u00020\fH&J\u0014\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u001c\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u000f\u001a\u00020\tH&J\u0018\u0010\u0010\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0011\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0016J\u0016\u0010\u0017\u001a\u00020\u00142\u0006\u0010\u0011\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0011\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0019\u001a\u00020\u00142\u0006\u0010\u0011\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\u0012J\u000e\u0010\u001a\u001a\u00020\u0014H\u00a6@\u00a2\u0006\u0002\u0010\u001bJ\u001e\u0010\u001c\u001a\u00020\u00142\u0006\u0010\u0011\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\u001eH\u00a6@\u00a2\u0006\u0002\u0010\u001fJ\u0014\u0010 \u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020!0\u00040\u0003H&J\u0014\u0010\"\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020!0\u00040\u0003H&J\u0016\u0010#\u001a\u00020\u00142\u0006\u0010$\u001a\u00020!H\u00a6@\u00a2\u0006\u0002\u0010%J\u0016\u0010&\u001a\u00020\u00142\u0006\u0010\u0011\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\u0012J\u0014\u0010\'\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0\u00040\u0003H&J\u0016\u0010)\u001a\u00020\u00142\u0006\u0010*\u001a\u00020(H\u00a6@\u00a2\u0006\u0002\u0010+J\u0016\u0010,\u001a\u00020\u00142\u0006\u0010\u0011\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\u0012J\u001e\u0010-\u001a\u00020\u00142\u0006\u0010.\u001a\u00020\t2\u0006\u0010/\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u00100J\u001e\u00101\u001a\u00020\u00142\u0006\u0010.\u001a\u00020\t2\u0006\u0010/\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u00100J\u001c\u00102\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0\u00040\u00032\u0006\u0010.\u001a\u00020\tH&\u00a8\u00063"}, d2 = {"Lcom/ivarna/truvalt/domain/repository/VaultRepository;", "", "getAllItems", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/ivarna/truvalt/domain/model/VaultItem;", "getFavoriteItems", "getItemsByFolder", "folderId", "", "getItemsByType", "type", "Lcom/ivarna/truvalt/domain/model/VaultItemType;", "getTrashItems", "searchItems", "query", "getItemById", "id", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveItem", "", "item", "(Lcom/ivarna/truvalt/domain/model/VaultItem;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteItem", "softDeleteItem", "restoreItem", "emptyTrash", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toggleFavorite", "favorite", "", "(Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllFolders", "Lcom/ivarna/truvalt/domain/model/Folder;", "getRootFolders", "saveFolder", "folder", "(Lcom/ivarna/truvalt/domain/model/Folder;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteFolder", "getAllTags", "Lcom/ivarna/truvalt/domain/model/Tag;", "saveTag", "tag", "(Lcom/ivarna/truvalt/domain/model/Tag;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteTag", "addTagToItem", "itemId", "tagId", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeTagFromItem", "getTagsForItem", "app_debug"})
public abstract interface VaultRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.VaultItem>> getAllItems();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.VaultItem>> getFavoriteItems();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.VaultItem>> getItemsByFolder(@org.jetbrains.annotations.NotNull()
    java.lang.String folderId);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.VaultItem>> getItemsByType(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.model.VaultItemType type);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.VaultItem>> getTrashItems();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.VaultItem>> searchItems(@org.jetbrains.annotations.NotNull()
    java.lang.String query);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getItemById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.ivarna.truvalt.domain.model.VaultItem> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveItem(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.model.VaultItem item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteItem(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object softDeleteItem(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object restoreItem(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object emptyTrash(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object toggleFavorite(@org.jetbrains.annotations.NotNull()
    java.lang.String id, boolean favorite, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.Folder>> getAllFolders();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.Folder>> getRootFolders();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveFolder(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.model.Folder folder, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteFolder(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.Tag>> getAllTags();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveTag(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.model.Tag tag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteTag(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addTagToItem(@org.jetbrains.annotations.NotNull()
    java.lang.String itemId, @org.jetbrains.annotations.NotNull()
    java.lang.String tagId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeTagFromItem(@org.jetbrains.annotations.NotNull()
    java.lang.String itemId, @org.jetbrains.annotations.NotNull()
    java.lang.String tagId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.Tag>> getTagsForItem(@org.jetbrains.annotations.NotNull()
    java.lang.String itemId);
}