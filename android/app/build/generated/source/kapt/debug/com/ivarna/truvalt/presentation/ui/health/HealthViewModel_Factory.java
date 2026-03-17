package com.ivarna.truvalt.presentation.ui.health;

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
public final class HealthViewModel_Factory implements Factory<HealthViewModel> {
  private final Provider<VaultRepository> vaultRepositoryProvider;

  public HealthViewModel_Factory(Provider<VaultRepository> vaultRepositoryProvider) {
    this.vaultRepositoryProvider = vaultRepositoryProvider;
  }

  @Override
  public HealthViewModel get() {
    return newInstance(vaultRepositoryProvider.get());
  }

  public static HealthViewModel_Factory create(Provider<VaultRepository> vaultRepositoryProvider) {
    return new HealthViewModel_Factory(vaultRepositoryProvider);
  }

  public static HealthViewModel newInstance(VaultRepository vaultRepository) {
    return new HealthViewModel(vaultRepository);
  }
}
