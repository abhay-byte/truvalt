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
public final class VaultItemEditViewModel_Factory implements Factory<VaultItemEditViewModel> {
  private final Provider<VaultRepository> vaultRepositoryProvider;

  public VaultItemEditViewModel_Factory(Provider<VaultRepository> vaultRepositoryProvider) {
    this.vaultRepositoryProvider = vaultRepositoryProvider;
  }

  @Override
  public VaultItemEditViewModel get() {
    return newInstance(vaultRepositoryProvider.get());
  }

  public static VaultItemEditViewModel_Factory create(
      Provider<VaultRepository> vaultRepositoryProvider) {
    return new VaultItemEditViewModel_Factory(vaultRepositoryProvider);
  }

  public static VaultItemEditViewModel newInstance(VaultRepository vaultRepository) {
    return new VaultItemEditViewModel(vaultRepository);
  }
}
