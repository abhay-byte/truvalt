package com.ivarna.truvalt.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\b\u0007\u0018\u00002\u00020\u0001B#\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u000e\u0010\f\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u000bH\u0096@\u00a2\u0006\u0002\u0010\u0011J\u000e\u0010\u0012\u001a\u00020\u0010H\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u000e\u0010\u0013\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u0014\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u0016J\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u000bH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u000bH\u0096@\u00a2\u0006\u0002\u0010\u0011J\u000e\u0010\u001a\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ&\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00100\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001eH\u0096@\u00a2\u0006\u0004\b \u0010!J&\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00100\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001eH\u0096@\u00a2\u0006\u0004\b#\u0010!J\b\u0010$\u001a\u0004\u0018\u00010\u000bJ\u0016\u0010%\u001a\u00020\u000b2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/ivarna/truvalt/data/repository/AuthRepositoryImpl;", "Lcom/ivarna/truvalt/domain/repository/AuthRepository;", "context", "Landroid/content/Context;", "cryptoManager", "Lcom/ivarna/truvalt/core/crypto/CryptoManager;", "preferences", "Lcom/ivarna/truvalt/data/preferences/TruvaltPreferences;", "<init>", "(Landroid/content/Context;Lcom/ivarna/truvalt/core/crypto/CryptoManager;Lcom/ivarna/truvalt/data/preferences/TruvaltPreferences;)V", "masterKey", "", "isVaultUnlocked", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "unlockVault", "", "([BLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "lockVault", "isBiometricEnabled", "setBiometricEnabled", "enabled", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getEncryptedVaultKey", "storeVaultKey", "encryptedKey", "hasVault", "createVault", "Lkotlin/Result;", "email", "", "password", "createVault-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "unlockWithPassword", "unlockWithPassword-0E7RQCE", "getMasterKey", "getVaultKey", "app_debug"})
public final class AuthRepositoryImpl implements com.ivarna.truvalt.domain.repository.AuthRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.core.crypto.CryptoManager cryptoManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.ivarna.truvalt.data.preferences.TruvaltPreferences preferences = null;
    @org.jetbrains.annotations.Nullable()
    private byte[] masterKey;
    
    @javax.inject.Inject()
    public AuthRepositoryImpl(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.core.crypto.CryptoManager cryptoManager, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.preferences.TruvaltPreferences preferences) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object isVaultUnlocked(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object unlockVault(@org.jetbrains.annotations.NotNull()
    byte[] masterKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object lockVault(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object isBiometricEnabled(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object setBiometricEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getEncryptedVaultKey(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super byte[]> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object storeVaultKey(@org.jetbrains.annotations.NotNull()
    byte[] encryptedKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object hasVault(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final byte[] getMasterKey() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final byte[] getVaultKey(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
        return null;
    }
}