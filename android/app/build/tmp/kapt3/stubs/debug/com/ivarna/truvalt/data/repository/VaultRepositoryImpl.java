package com.ivarna.truvalt.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B1\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0004\b\f\u0010\rJ\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000fJ\u0006\u0010\u0013\u001a\u00020\u0011J\u0014\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00160\u0015H\u0016J\u0014\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00160\u0015H\u0016J\u001c\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00160\u00152\u0006\u0010\u001a\u001a\u00020\u001bH\u0016J\u001c\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00160\u00152\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J\u0014\u0010\u001f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00160\u0015H\u0016J\u001c\u0010 \u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00160\u00152\u0006\u0010!\u001a\u00020\u001bH\u0016J\u0018\u0010\"\u001a\u0004\u0018\u00010\u00172\u0006\u0010#\u001a\u00020\u001bH\u0096@\u00a2\u0006\u0002\u0010$J\u0016\u0010%\u001a\u00020\u00112\u0006\u0010&\u001a\u00020\u0017H\u0096@\u00a2\u0006\u0002\u0010\'J\u0016\u0010(\u001a\u00020\u00112\u0006\u0010#\u001a\u00020\u001bH\u0096@\u00a2\u0006\u0002\u0010$J\u0016\u0010)\u001a\u00020\u00112\u0006\u0010#\u001a\u00020\u001bH\u0096@\u00a2\u0006\u0002\u0010$J\u0016\u0010*\u001a\u00020\u00112\u0006\u0010#\u001a\u00020\u001bH\u0096@\u00a2\u0006\u0002\u0010$J\u000e\u0010+\u001a\u00020\u0011H\u0096@\u00a2\u0006\u0002\u0010,J\u001e\u0010-\u001a\u00020\u00112\u0006\u0010#\u001a\u00020\u001b2\u0006\u0010.\u001a\u00020/H\u0096@\u00a2\u0006\u0002\u00100J\u0014\u00101\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002020\u00160\u0015H\u0016J\u0014\u00103\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002020\u00160\u0015H\u0016J\u0016\u00104\u001a\u00020\u00112\u0006\u00105\u001a\u000202H\u0096@\u00a2\u0006\u0002\u00106J\u0016\u00107\u001a\u00020\u00112\u0006\u0010#\u001a\u00020\u001bH\u0096@\u00a2\u0006\u0002\u0010$J\u0014\u00108\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002090\u00160\u0015H\u0016J\u0016\u0010:\u001a\u00020\u00112\u0006\u0010;\u001a\u000209H\u0096@\u00a2\u0006\u0002\u0010<J\u0016\u0010=\u001a\u00020\u00112\u0006\u0010#\u001a\u00020\u001bH\u0096@\u00a2\u0006\u0002\u0010$J\u001e\u0010>\u001a\u00020\u00112\u0006\u0010?\u001a\u00020\u001b2\u0006\u0010@\u001a\u00020\u001bH\u0096@\u00a2\u0006\u0002\u0010AJ\u001e\u0010B\u001a\u00020\u00112\u0006\u0010?\u001a\u00020\u001b2\u0006\u0010@\u001a\u00020\u001bH\u0096@\u00a2\u0006\u0002\u0010AJ\u001c\u0010C\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002090\u00160\u00152\u0006\u0010?\u001a\u00020\u001bH\u0016J\u000e\u0010D\u001a\u0004\u0018\u00010\u0017*\u00020EH\u0002J\f\u0010F\u001a\u00020E*\u00020\u0017H\u0002J\f\u0010D\u001a\u000202*\u00020GH\u0002J\f\u0010F\u001a\u00020G*\u000202H\u0002J\f\u0010D\u001a\u000209*\u00020HH\u0002J\f\u0010F\u001a\u00020H*\u000209H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006I"}, d2 = {"Lcom/ivarna/truvalt/data/repository/VaultRepositoryImpl;", "Lcom/ivarna/truvalt/domain/repository/VaultRepository;", "vaultItemDao", "Lcom/ivarna/truvalt/data/local/dao/VaultItemDao;", "folderDao", "Lcom/ivarna/truvalt/data/local/dao/FolderDao;", "tagDao", "Lcom/ivarna/truvalt/data/local/dao/TagDao;", "cryptoManager", "Lcom/ivarna/truvalt/core/crypto/CryptoManager;", "preferences", "Lcom/ivarna/truvalt/data/preferences/TruvaltPreferences;", "<init>", "(Lcom/ivarna/truvalt/data/local/dao/VaultItemDao;Lcom/ivarna/truvalt/data/local/dao/FolderDao;Lcom/ivarna/truvalt/data/local/dao/TagDao;Lcom/ivarna/truvalt/core/crypto/CryptoManager;Lcom/ivarna/truvalt/data/preferences/TruvaltPreferences;)V", "vaultKey", "", "setVaultKey", "", "key", "clearVaultKey", "getAllItems", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/ivarna/truvalt/domain/model/VaultItem;", "getFavoriteItems", "getItemsByFolder", "folderId", "", "getItemsByType", "type", "Lcom/ivarna/truvalt/domain/model/VaultItemType;", "getTrashItems", "searchItems", "query", "getItemById", "id", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveItem", "item", "(Lcom/ivarna/truvalt/domain/model/VaultItem;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteItem", "softDeleteItem", "restoreItem", "emptyTrash", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toggleFavorite", "favorite", "", "(Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllFolders", "Lcom/ivarna/truvalt/domain/model/Folder;", "getRootFolders", "saveFolder", "folder", "(Lcom/ivarna/truvalt/domain/model/Folder;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteFolder", "getAllTags", "Lcom/ivarna/truvalt/domain/model/Tag;", "saveTag", "tag", "(Lcom/ivarna/truvalt/domain/model/Tag;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteTag", "addTagToItem", "itemId", "tagId", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeTagFromItem", "getTagsForItem", "toDomain", "Lcom/ivarna/truvalt/data/local/entity/VaultItemEntity;", "toEntity", "Lcom/ivarna/truvalt/data/local/entity/FolderEntity;", "Lcom/ivarna/truvalt/data/local/entity/TagEntity;", "app_debug"})
public final class VaultRepositoryImpl implements com.ivarna.truvalt.domain.repository.VaultRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.data.local.dao.VaultItemDao vaultItemDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.data.local.dao.FolderDao folderDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.data.local.dao.TagDao tagDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.core.crypto.CryptoManager cryptoManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.data.preferences.TruvaltPreferences preferences = null;
    @org.jetbrains.annotations.Nullable()
    private byte[] vaultKey;
    
    @javax.inject.Inject()
    public VaultRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.dao.VaultItemDao vaultItemDao, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.dao.FolderDao folderDao, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.local.dao.TagDao tagDao, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.core.crypto.CryptoManager cryptoManager, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.preferences.TruvaltPreferences preferences) {
        super();
    }
    
    public final void setVaultKey(@org.jetbrains.annotations.NotNull()
    byte[] key) {
    }
    
    public final void clearVaultKey() {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.VaultItem>> getAllItems() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.VaultItem>> getFavoriteItems() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.VaultItem>> getItemsByFolder(@org.jetbrains.annotations.NotNull()
    java.lang.String folderId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.VaultItem>> getItemsByType(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.model.VaultItemType type) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.VaultItem>> getTrashItems() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.VaultItem>> searchItems(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getItemById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.ivarna.truvalt.domain.model.VaultItem> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object saveItem(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.model.VaultItem item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteItem(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object softDeleteItem(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object restoreItem(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object emptyTrash(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object toggleFavorite(@org.jetbrains.annotations.NotNull()
    java.lang.String id, boolean favorite, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.Folder>> getAllFolders() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.Folder>> getRootFolders() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object saveFolder(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.model.Folder folder, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteFolder(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.Tag>> getAllTags() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object saveTag(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.model.Tag tag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteTag(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object addTagToItem(@org.jetbrains.annotations.NotNull()
    java.lang.String itemId, @org.jetbrains.annotations.NotNull()
    java.lang.String tagId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object removeTagFromItem(@org.jetbrains.annotations.NotNull()
    java.lang.String itemId, @org.jetbrains.annotations.NotNull()
    java.lang.String tagId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.ivarna.truvalt.domain.model.Tag>> getTagsForItem(@org.jetbrains.annotations.NotNull()
    java.lang.String itemId) {
        return null;
    }
    
    private final com.ivarna.truvalt.domain.model.VaultItem toDomain(com.ivarna.truvalt.data.local.entity.VaultItemEntity $this$toDomain) {
        return null;
    }
    
    private final com.ivarna.truvalt.data.local.entity.VaultItemEntity toEntity(com.ivarna.truvalt.domain.model.VaultItem $this$toEntity) {
        return null;
    }
    
    private final com.ivarna.truvalt.domain.model.Folder toDomain(com.ivarna.truvalt.data.local.entity.FolderEntity $this$toDomain) {
        return null;
    }
    
    private final com.ivarna.truvalt.data.local.entity.FolderEntity toEntity(com.ivarna.truvalt.domain.model.Folder $this$toEntity) {
        return null;
    }
    
    private final com.ivarna.truvalt.domain.model.Tag toDomain(com.ivarna.truvalt.data.local.entity.TagEntity $this$toDomain) {
        return null;
    }
    
    private final com.ivarna.truvalt.data.local.entity.TagEntity toEntity(com.ivarna.truvalt.domain.model.Tag $this$toEntity) {
        return null;
    }
}