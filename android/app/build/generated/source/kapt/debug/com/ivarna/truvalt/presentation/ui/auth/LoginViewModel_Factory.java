package com.ivarna.truvalt.presentation.ui.auth;

import com.ivarna.truvalt.domain.repository.AuthRepository;
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<VaultRepository> vaultRepositoryProvider;

  public LoginViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<VaultRepository> vaultRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.vaultRepositoryProvider = vaultRepositoryProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(authRepositoryProvider.get(), vaultRepositoryProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<VaultRepository> vaultRepositoryProvider) {
    return new LoginViewModel_Factory(authRepositoryProvider, vaultRepositoryProvider);
  }

  public static LoginViewModel newInstance(AuthRepository authRepository,
      VaultRepository vaultRepository) {
    return new LoginViewModel(authRepository, vaultRepository);
  }
}
