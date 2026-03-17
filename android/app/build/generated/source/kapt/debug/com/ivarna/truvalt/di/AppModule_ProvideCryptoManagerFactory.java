package com.ivarna.truvalt.di;

import com.ivarna.truvalt.core.crypto.CryptoManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideCryptoManagerFactory implements Factory<CryptoManager> {
  @Override
  public CryptoManager get() {
    return provideCryptoManager();
  }

  public static AppModule_ProvideCryptoManagerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CryptoManager provideCryptoManager() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCryptoManager());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideCryptoManagerFactory INSTANCE = new AppModule_ProvideCryptoManagerFactory();
  }
}
