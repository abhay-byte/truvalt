package com.ivarna.truvalt.di;

import com.ivarna.truvalt.data.local.dao.FolderDao;
import com.ivarna.truvalt.data.local.database.TruvaltDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideFolderDaoFactory implements Factory<FolderDao> {
  private final Provider<TruvaltDatabase> databaseProvider;

  public DatabaseModule_ProvideFolderDaoFactory(Provider<TruvaltDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public FolderDao get() {
    return provideFolderDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideFolderDaoFactory create(
      Provider<TruvaltDatabase> databaseProvider) {
    return new DatabaseModule_ProvideFolderDaoFactory(databaseProvider);
  }

  public static FolderDao provideFolderDao(TruvaltDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFolderDao(database));
  }
}
