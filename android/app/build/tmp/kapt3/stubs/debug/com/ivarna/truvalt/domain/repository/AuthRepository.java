package com.ivarna.truvalt.domain.repository;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\bf\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a6@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u00a6@\u00a2\u0006\u0002\u0010\tJ\u000e\u0010\n\u001a\u00020\u0006H\u00a6@\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u000b\u001a\u00020\u0003H\u00a6@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\f\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\u0003H\u00a6@\u00a2\u0006\u0002\u0010\u000eJ\u0010\u0010\u000f\u001a\u0004\u0018\u00010\bH\u00a6@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\bH\u00a6@\u00a2\u0006\u0002\u0010\tJ\u000e\u0010\u0012\u001a\u00020\u0003H\u00a6@\u00a2\u0006\u0002\u0010\u0004J&\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00060\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0016H\u00a6@\u00a2\u0006\u0004\b\u0018\u0010\u0019J&\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00060\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0016H\u00a6@\u00a2\u0006\u0004\b\u001b\u0010\u0019\u00a8\u0006\u001c"}, d2 = {"Lcom/ivarna/truvalt/domain/repository/AuthRepository;", "", "isVaultUnlocked", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "unlockVault", "", "masterKey", "", "([BLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "lockVault", "isBiometricEnabled", "setBiometricEnabled", "enabled", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getEncryptedVaultKey", "storeVaultKey", "encryptedKey", "hasVault", "createVault", "Lkotlin/Result;", "email", "", "password", "createVault-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "unlockWithPassword", "unlockWithPassword-0E7RQCE", "app_debug"})
public abstract interface AuthRepository {
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object isVaultUnlocked(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object unlockVault(@org.jetbrains.annotations.NotNull()
    byte[] masterKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object lockVault(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object isBiometricEnabled(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setBiometricEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getEncryptedVaultKey(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super byte[]> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object storeVaultKey(@org.jetbrains.annotations.NotNull()
    byte[] encryptedKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object hasVault(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
}