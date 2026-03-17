package com.ivarna.truvalt.data.preferences;

import android.content.Context;
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
public final class TruvaltPreferences_Factory implements Factory<TruvaltPreferences> {
  private final Provider<Context> contextProvider;

  public TruvaltPreferences_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public TruvaltPreferences get() {
    return newInstance(contextProvider.get());
  }

  public static TruvaltPreferences_Factory create(Provider<Context> contextProvider) {
    return new TruvaltPreferences_Factory(contextProvider);
  }

  public static TruvaltPreferences newInstance(Context context) {
    return new TruvaltPreferences(context);
  }
}
