package com.ivarna.truvalt.di;

import com.ivarna.truvalt.data.local.dao.VaultItemDao;
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
public final class DatabaseModule_ProvideVaultItemDaoFactory implements Factory<VaultItemDao> {
  private final Provider<TruvaltDatabase> databaseProvider;

  public DatabaseModule_ProvideVaultItemDaoFactory(Provider<TruvaltDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public VaultItemDao get() {
    return provideVaultItemDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideVaultItemDaoFactory create(
      Provider<TruvaltDatabase> databaseProvider) {
    return new DatabaseModule_ProvideVaultItemDaoFactory(databaseProvider);
  }

  public static VaultItemDao provideVaultItemDao(TruvaltDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideVaultItemDao(database));
  }
}
