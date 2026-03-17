package com.ivarna.truvalt.presentation.ui.vault;

import com.ivarna.truvalt.domain.repository.VaultRepository;
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
public final class VaultViewModel_Factory implements Factory<VaultViewModel> {
  private final Provider<VaultRepository> vaultRepositoryProvider;

  public VaultViewModel_Factory(Provider<VaultRepository> vaultRepositoryProvider) {
    this.vaultRepositoryProvider = vaultRepositoryProvider;
  }

  @Override
  public VaultViewModel get() {
    return newInstance(vaultRepositoryProvider.get());
  }

  public static VaultViewModel_Factory create(Provider<VaultRepository> vaultRepositoryProvider) {
    return new VaultViewModel_Factory(vaultRepositoryProvider);
  }

  public static VaultViewModel newInstance(VaultRepository vaultRepository) {
    return new VaultViewModel(vaultRepository);
  }
}
