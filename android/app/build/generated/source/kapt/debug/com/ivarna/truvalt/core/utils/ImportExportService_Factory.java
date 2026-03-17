package com.ivarna.truvalt.core.utils;

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
public final class ImportExportService_Factory implements Factory<ImportExportService> {
  @Override
  public ImportExportService get() {
    return newInstance();
  }

  public static ImportExportService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ImportExportService newInstance() {
    return new ImportExportService();
  }

  private static final class InstanceHolder {
    private static final ImportExportService_Factory INSTANCE = new ImportExportService_Factory();
  }
}
