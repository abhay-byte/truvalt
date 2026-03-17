package com.ivarna.truvalt.data.repository;

import com.ivarna.truvalt.core.crypto.CryptoManager;
import com.ivarna.truvalt.data.local.dao.FolderDao;
import com.ivarna.truvalt.data.local.dao.TagDao;
import com.ivarna.truvalt.data.local.dao.VaultItemDao;
import com.ivarna.truvalt.data.preferences.TruvaltPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class VaultRepositoryImpl_Factory implements Factory<VaultRepositoryImpl> {
  private final Provider<VaultItemDao> vaultItemDaoProvider;

  private final Provider<FolderDao> folderDaoProvider;

  private final Provider<TagDao> tagDaoProvider;

  private final Provider<CryptoManager> cryptoManagerProvider;

  private final Provider<TruvaltPreferences> preferencesProvider;

  public VaultRepositoryImpl_Factory(Provider<VaultItemDao> vaultItemDaoProvider,
      Provider<FolderDao> folderDaoProvider, Provider<TagDao> tagDaoProvider,
      Provider<CryptoManager> cryptoManagerProvider,
      Provider<TruvaltPreferences> preferencesProvider) {
    this.vaultItemDaoProvider = vaultItemDaoProvider;
    this.folderDaoProvider = folderDaoProvider;
    this.tagDaoProvider = tagDaoProvider;
    this.cryptoManagerProvider = cryptoManagerProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public VaultRepositoryImpl get() {
    return newInstance(vaultItemDaoProvider.get(), folderDaoProvider.get(), tagDaoProvider.get(), cryptoManagerProvider.get(), preferencesProvider.get());
  }

  public static VaultRepositoryImpl_Factory create(Provider<VaultItemDao> vaultItemDaoProvider,
      Provider<FolderDao> folderDaoProvider, Provider<TagDao> tagDaoProvider,
      Provider<CryptoManager> cryptoManagerProvider,
      Provider<TruvaltPreferences> preferencesProvider) {
    return new VaultRepositoryImpl_Factory(vaultItemDaoProvider, folderDaoProvider, tagDaoProvider, cryptoManagerProvider, preferencesProvider);
  }

  public static VaultRepositoryImpl newInstance(VaultItemDao vaultItemDao, FolderDao folderDao,
      TagDao tagDao, CryptoManager cryptoManager, TruvaltPreferences preferences) {
    return new VaultRepositoryImpl(vaultItemDao, folderDao, tagDao, cryptoManager, preferences);
  }
}
