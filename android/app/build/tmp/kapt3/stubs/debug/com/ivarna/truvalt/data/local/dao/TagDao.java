package com.ivarna.truvalt.data.local.dao;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0018\u0010\n\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000b\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u001c\u0010\u0010\u001a\u00020\r2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0013\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0014\u001a\u00020\r2\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u001c\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0016\u001a\u00020\bH\'J\u001c\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00040\u00032\u0006\u0010\u0018\u001a\u00020\bH\'J\u0016\u0010\u0019\u001a\u00020\r2\u0006\u0010\u001a\u001a\u00020\u001bH\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u0016\u0010\u001d\u001a\u00020\r2\u0006\u0010\u001a\u001a\u00020\u001bH\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u0016\u0010\u001e\u001a\u00020\r2\u0006\u0010\u0016\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\t\u00a8\u0006\u001f"}, d2 = {"Lcom/ivarna/truvalt/data/local/dao/TagDao;", "", "getAllTags", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/ivarna/truvalt/data/local/entity/TagEntity;", "getTagById", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTagByName", "name", "insertTag", "", "tag", "(Lcom/ivarna/truvalt/data/local/entity/TagEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertTags", "tags", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteTag", "deleteTagById", "getTagsForItem", "itemId", "getItemsForTag", "tagId", "addTagToItem", "itemTag", "Lcom/ivarna/truvalt/data/local/entity/VaultItemTagEntity;", "(Lcom/ivarna/truvalt/data/local/entity/VaultItemTagEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeTagFromItem", "removeAllTagsFromItem", "app_debug"})
@androidx.room.Dao()
public abstract interface TagDao {
    
    @androidx.room.Query(value = "SELECT * FROM tags ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.data.local.entity.TagEntity>> getAllTags();
    
    @androidx.room.Query(value = "SELECT * FROM tags WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTagById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.ivarna.truvalt.data.local.entity.TagEntity> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM tags WHERE name = :name")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTagByName(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.ivarna.truvalt.data.local.entity.TagEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertTag(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.entity.TagEntity tag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertTags(@org.jetbrains.annotations.NotNull()
    java.util.List<com.ivarna.truvalt.data.local.entity.TagEntity> tags, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteTag(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.entity.TagEntity tag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM tags WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteTagById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT tags.* FROM tags INNER JOIN vault_item_tags ON tags.id = vault_item_tags.tagId WHERE vault_item_tags.itemId = :itemId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.data.local.entity.TagEntity>> getTagsForItem(@org.jetbrains.annotations.NotNull()
    java.lang.String itemId);
    
    @androidx.room.Query(value = "SELECT vault_item_tags.itemId FROM vault_item_tags WHERE vault_item_tags.tagId = :tagId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<java.lang.String>> getItemsForTag(@org.jetbrains.annotations.NotNull()
    java.lang.String tagId);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addTagToItem(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.entity.VaultItemTagEntity itemTag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeTagFromItem(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.entity.VaultItemTagEntity itemTag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM vault_item_tags WHERE itemId = :itemId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeAllTagsFromItem(@org.jetbrains.annotations.NotNull()
    java.lang.String itemId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}