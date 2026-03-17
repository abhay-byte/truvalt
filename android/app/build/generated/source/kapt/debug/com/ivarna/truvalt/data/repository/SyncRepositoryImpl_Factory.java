package com.ivarna.truvalt.data.repository;

import android.content.Context;
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
public final class SyncRepositoryImpl_Factory implements Factory<SyncRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<TruvaltPreferences> preferencesProvider;

  public SyncRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<TruvaltPreferences> preferencesProvider) {
    this.contextProvider = contextProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public SyncRepositoryImpl get() {
    return newInstance(contextProvider.get(), preferencesProvider.get());
  }

  public static SyncRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<TruvaltPreferences> preferencesProvider) {
    return new SyncRepositoryImpl_Factory(contextProvider, preferencesProvider);
  }

  public static SyncRepositoryImpl newInstance(Context context, TruvaltPreferences preferences) {
    return new SyncRepositoryImpl(context, preferences);
  }
}
