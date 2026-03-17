package com.ivarna.truvalt.presentation.ui.auth;

import com.ivarna.truvalt.domain.repository.SyncRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ServerSetupViewModel_Factory implements Factory<ServerSetupViewModel> {
  private final Provider<SyncRepository> syncRepositoryProvider;

  public ServerSetupViewModel_Factory(Provider<SyncRepository> syncRepositoryProvider) {
    this.syncRepositoryProvider = syncRepositoryProvider;
  }

  @Override
  public ServerSetupViewModel get() {
    return newInstance(syncRepositoryProvider.get());
  }

  public static ServerSetupViewModel_Factory create(
      Provider<SyncRepository> syncRepositoryProvider) {
    return new ServerSetupViewModel_Factory(syncRepositoryProvider);
  }

  public static ServerSetupViewModel newInstance(SyncRepository syncRepository) {
    return new ServerSetupViewModel(syncRepository);
  }
}
