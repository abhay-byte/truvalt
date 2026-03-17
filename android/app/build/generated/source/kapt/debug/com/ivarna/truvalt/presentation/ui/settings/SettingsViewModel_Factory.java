package com.ivarna.truvalt.presentation.ui.settings;

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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<TruvaltPreferences> preferencesProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<SyncRepository> syncRepositoryProvider;

  public SettingsViewModel_Factory(Provider<TruvaltPreferences> preferencesProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider) {
    this.preferencesProvider = preferencesProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.syncRepositoryProvider = syncRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(preferencesProvider.get(), authRepositoryProvider.get(), syncRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<TruvaltPreferences> preferencesProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider) {
    return new SettingsViewModel_Factory(preferencesProvider, authRepositoryProvider, syncRepositoryProvider);
  }

  public static SettingsViewModel newInstance(TruvaltPreferences preferences,
      AuthRepository authRepository, SyncRepository syncRepository) {
    return new SettingsViewModel(preferences, authRepository, syncRepository);
  }
}
