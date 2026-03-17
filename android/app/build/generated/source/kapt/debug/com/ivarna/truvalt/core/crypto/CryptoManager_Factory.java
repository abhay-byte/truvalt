package com.ivarna.truvalt.core.crypto;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class CryptoManager_Factory implements Factory<CryptoManager> {
  @Override
  public CryptoManager get() {
    return newInstance();
  }

  public static CryptoManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CryptoManager newInstance() {
    return new CryptoManager();
  }

  private static final class InstanceHolder {
    private static final CryptoManager_Factory INSTANCE = new CryptoManager_Factory();
  }
}
