package com.ivarna.truvalt.di;

@dagger.Module()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\nH\'J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0006\u001a\u00020\rH\'\u00a8\u0006\u000e"}, d2 = {"Lcom/ivarna/truvalt/di/RepositoryModule;", "", "<init>", "()V", "bindVaultRepository", "Lcom/ivarna/truvalt/domain/repository/VaultRepository;", "impl", "Lcom/ivarna/truvalt/data/repository/VaultRepositoryImpl;", "bindAuthRepository", "Lcom/ivarna/truvalt/domain/repository/AuthRepository;", "Lcom/ivarna/truvalt/data/repository/AuthRepositoryImpl;", "bindSyncRepository", "Lcom/ivarna/truvalt/domain/repository/SyncRepository;", "Lcom/ivarna/truvalt/data/repository/SyncRepositoryImpl;", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public abstract class RepositoryModule {
    
    public RepositoryModule() {
        super();
    }
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.ivarna.truvalt.domain.repository.VaultRepository bindVaultRepository(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.repository.VaultRepositoryImpl impl);
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.ivarna.truvalt.domain.repository.AuthRepository bindAuthRepository(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.repository.AuthRepositoryImpl impl);
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.ivarna.truvalt.domain.repository.SyncRepository bindSyncRepository(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.data.repository.SyncRepositoryImpl impl);
}