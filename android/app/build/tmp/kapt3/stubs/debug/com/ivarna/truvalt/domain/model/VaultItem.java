package com.ivarna.truvalt.domain.model;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\b\n\u0002\b\u000e\b\u0086\b\u0018\u00002\u00020\u0001Bi\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\r\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\r\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0013\u0010&\u001a\u00020\u000b2\b\u0010\'\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010(\u001a\u00020)H\u0016J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\u0005H\u00c6\u0003J\t\u0010,\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010-\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010.\u001a\u00020\tH\u00c6\u0003J\t\u0010/\u001a\u00020\u000bH\u00c6\u0003J\t\u00100\u001a\u00020\rH\u00c6\u0003J\t\u00101\u001a\u00020\rH\u00c6\u0003J\u0010\u00102\u001a\u0004\u0018\u00010\rH\u00c6\u0003\u00a2\u0006\u0002\u0010\"J\t\u00103\u001a\u00020\u0011H\u00c6\u0003Jv\u00104\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\r2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\r2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u00c6\u0001\u00a2\u0006\u0002\u00105J\t\u00106\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0015R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0015R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\u000e\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001fR\u0015\u0010\u000f\u001a\u0004\u0018\u00010\r\u00a2\u0006\n\n\u0002\u0010#\u001a\u0004\b!\u0010\"R\u0011\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%\u00a8\u00067"}, d2 = {"Lcom/ivarna/truvalt/domain/model/VaultItem;", "", "id", "", "type", "Lcom/ivarna/truvalt/domain/model/VaultItemType;", "name", "folderId", "encryptedData", "", "favorite", "", "createdAt", "", "updatedAt", "deletedAt", "syncStatus", "Lcom/ivarna/truvalt/domain/model/SyncStatus;", "<init>", "(Ljava/lang/String;Lcom/ivarna/truvalt/domain/model/VaultItemType;Ljava/lang/String;Ljava/lang/String;[BZJJLjava/lang/Long;Lcom/ivarna/truvalt/domain/model/SyncStatus;)V", "getId", "()Ljava/lang/String;", "getType", "()Lcom/ivarna/truvalt/domain/model/VaultItemType;", "getName", "getFolderId", "getEncryptedData", "()[B", "getFavorite", "()Z", "getCreatedAt", "()J", "getUpdatedAt", "getDeletedAt", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getSyncStatus", "()Lcom/ivarna/truvalt/domain/model/SyncStatus;", "equals", "other", "hashCode", "", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "copy", "(Ljava/lang/String;Lcom/ivarna/truvalt/domain/model/VaultItemType;Ljava/lang/String;Ljava/lang/String;[BZJJLjava/lang/Long;Lcom/ivarna/truvalt/domain/model/SyncStatus;)Lcom/ivarna/truvalt/domain/model/VaultItem;", "toString", "app_debug"})
public final class VaultItem {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.domain.model.VaultItemType type = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String folderId = null;
    @org.jetbrains.annotations.NotNull()
    private final byte[] encryptedData = null;
    private final boolean favorite = false;
    private final long createdAt = 0L;
    private final long updatedAt = 0L;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long deletedAt = null;
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.domain.model.SyncStatus syncStatus = null;
    
    public VaultItem(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.model.VaultItemType type, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String folderId, @org.jetbrains.annotations.NotNull()
    byte[] encryptedData, boolean favorite, long createdAt, long updatedAt, @org.jetbrains.annotations.Nullable()
    java.lang.Long deletedAt, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.model.SyncStatus syncStatus) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.ivarna.truvalt.domain.model.VaultItemType getType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getFolderId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final byte[] getEncryptedData() {
        return null;
    }
    
    public final boolean getFavorite() {
        return false;
    }
    
    public final long getCreatedAt() {
        return 0L;
    }
    
    public final long getUpdatedAt() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getDeletedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.ivarna.truvalt.domain.model.SyncStatus getSyncStatus() {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.ivarna.truvalt.domain.model.SyncStatus component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.ivarna.truvalt.domain.model.VaultItemType component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final byte[] component5() {
        return null;
    }
    
    public final boolean component6() {
        return false;
    }
    
    public final long component7() {
        return 0L;
    }
    
    public final long component8() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.ivarna.truvalt.domain.model.VaultItem copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.model.VaultItemType type, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String folderId, @org.jetbrains.annotations.NotNull()
    byte[] encryptedData, boolean favorite, long createdAt, long updatedAt, @org.jetbrains.annotations.Nullable()
    java.lang.Long deletedAt, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.domain.model.SyncStatus syncStatus) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}