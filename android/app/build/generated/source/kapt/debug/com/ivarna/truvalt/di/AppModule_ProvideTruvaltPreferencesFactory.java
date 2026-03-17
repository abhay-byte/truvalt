package com.ivarna.truvalt.di;

import android.content.Context;
import com.ivarna.truvalt.data.preferences.TruvaltPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideTruvaltPreferencesFactory implements Factory<TruvaltPreferences> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideTruvaltPreferencesFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public TruvaltPreferences get() {
    return provideTruvaltPreferences(contextProvider.get());
  }

  public static AppModule_ProvideTruvaltPreferencesFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_ProvideTruvaltPreferencesFactory(contextProvider);
  }

  public static TruvaltPreferences provideTruvaltPreferences(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTruvaltPreferences(context));
  }
}
