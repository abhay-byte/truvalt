package com.ivarna.truvalt.data.repository;

import android.content.Context;
import com.ivarna.truvalt.core.crypto.CryptoManager;
import com.ivarna.truvalt.data.preferences.TruvaltPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<CryptoManager> cryptoManagerProvider;

  private final Provider<TruvaltPreferences> preferencesProvider;

  public AuthRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<CryptoManager> cryptoManagerProvider,
      Provider<TruvaltPreferences> preferencesProvider) {
    this.contextProvider = contextProvider;
    this.cryptoManagerProvider = cryptoManagerProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(contextProvider.get(), cryptoManagerProvider.get(), preferencesProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<CryptoManager> cryptoManagerProvider,
      Provider<TruvaltPreferences> preferencesProvider) {
    return new AuthRepositoryImpl_Factory(contextProvider, cryptoManagerProvider, preferencesProvider);
  }

  public static AuthRepositoryImpl newInstance(Context context, CryptoManager cryptoManager,
      TruvaltPreferences preferences) {
    return new AuthRepositoryImpl(context, cryptoManager, preferences);
  }
}
