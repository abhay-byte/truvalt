package com.ivarna.truvalt.domain.repository;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a6@\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\bH\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\bH\u00a6@\u00a2\u0006\u0002\u0010\u000bJ\u000e\u0010\f\u001a\u00020\rH\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u000e\u001a\u00020\rH\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\rH\u00a6@\u00a2\u0006\u0002\u0010\u0011J\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u0013H\u00a6@\u00a2\u0006\u0002\u0010\u0016\u00a8\u0006\u0017"}, d2 = {"Lcom/ivarna/truvalt/domain/repository/SyncRepository;", "", "sync", "Lkotlin/Result;", "", "sync-IoAF18A", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLastSyncTime", "", "setLastSyncTime", "time", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "isOnline", "", "isLocalOnly", "setLocalOnly", "localOnly", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getServerUrl", "", "setServerUrl", "url", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface SyncRepository {
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLastSyncTime(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setLastSyncTime(long time, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object isOnline(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object isLocalOnly(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setLocalOnly(boolean localOnly, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getServerUrl(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setServerUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String url, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}