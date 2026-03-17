package com.ivarna.truvalt.presentation.ui.auth;

import com.ivarna.truvalt.data.preferences.TruvaltPreferences;
import com.ivarna.truvalt.domain.repository.AuthRepository;
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<SyncRepository> syncRepositoryProvider;

  private final Provider<TruvaltPreferences> preferencesProvider;

  public AuthViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider,
      Provider<TruvaltPreferences> preferencesProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.syncRepositoryProvider = syncRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(authRepositoryProvider.get(), syncRepositoryProvider.get(), preferencesProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider,
      Provider<TruvaltPreferences> preferencesProvider) {
    return new AuthViewModel_Factory(authRepositoryProvider, syncRepositoryProvider, preferencesProvider);
  }

  public static AuthViewModel newInstance(AuthRepository authRepository,
      SyncRepository syncRepository, TruvaltPreferences preferences) {
    return new AuthViewModel(authRepository, syncRepository, preferences);
  }
}
