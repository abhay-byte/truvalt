package com.ivarna.truvalt.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.ivarna.truvalt.data.local.dao.FolderDao;
import com.ivarna.truvalt.data.local.dao.FolderDao_Impl;
import com.ivarna.truvalt.data.local.dao.TagDao;
import com.ivarna.truvalt.data.local.dao.TagDao_Impl;
import com.ivarna.truvalt.data.local.dao.VaultItemDao;
import com.ivarna.truvalt.data.local.dao.VaultItemDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TruvaltDatabase_Impl extends TruvaltDatabase {
  private volatile VaultItemDao _vaultItemDao;

  private volatile FolderDao _folderDao;

  private volatile TagDao _tagDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `vault_items` (`id` TEXT NOT NULL, `type` TEXT NOT NULL, `name` TEXT NOT NULL, `folderId` TEXT, `encryptedData` BLOB NOT NULL, `favorite` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `deletedAt` INTEGER, `syncStatus` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `folders` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `icon` TEXT, `parentId` TEXT, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `vault_item_tags` (`itemId` TEXT NOT NULL, `tagId` TEXT NOT NULL, PRIMARY KEY(`itemId`, `tagId`), FOREIGN KEY(`itemId`) REFERENCES `vault_items`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`tagId`) REFERENCES `tags`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vault_item_tags_itemId` ON `vault_item_tags` (`itemId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vault_item_tags_tagId` ON `vault_item_tags` (`tagId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_metadata` (`key` TEXT NOT NULL, `value` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`key`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '06735ab850df2dbc712870b55a3d0317')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `vault_items`");
        db.execSQL("DROP TABLE IF EXISTS `folders`");
        db.execSQL("DROP TABLE IF EXISTS `tags`");
        db.execSQL("DROP TABLE IF EXISTS `vault_item_tags`");
        db.execSQL("DROP TABLE IF EXISTS `sync_metadata`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsVaultItems = new HashMap<String, TableInfo.Column>(10);
        _columnsVaultItems.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("folderId", new TableInfo.Column("folderId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("encryptedData", new TableInfo.Column("encryptedData", "BLOB", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("favorite", new TableInfo.Column("favorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("deletedAt", new TableInfo.Column("deletedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVaultItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVaultItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVaultItems = new TableInfo("vault_items", _columnsVaultItems, _foreignKeysVaultItems, _indicesVaultItems);
        final TableInfo _existingVaultItems = TableInfo.read(db, "vault_items");
        if (!_infoVaultItems.equals(_existingVaultItems)) {
          return new RoomOpenHelper.ValidationResult(false, "vault_items(com.ivarna.truvalt.data.local.entity.VaultItemEntity).\n"
                  + " Expected:\n" + _infoVaultItems + "\n"
                  + " Found:\n" + _existingVaultItems);
        }
        final HashMap<String, TableInfo.Column> _columnsFolders = new HashMap<String, TableInfo.Column>(5);
        _columnsFolders.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFolders.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFolders.put("icon", new TableInfo.Column("icon", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFolders.put("parentId", new TableInfo.Column("parentId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFolders.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFolders = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFolders = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFolders = new TableInfo("folders", _columnsFolders, _foreignKeysFolders, _indicesFolders);
        final TableInfo _existingFolders = TableInfo.read(db, "folders");
        if (!_infoFolders.equals(_existingFolders)) {
          return new RoomOpenHelper.ValidationResult(false, "folders(com.ivarna.truvalt.data.local.entity.FolderEntity).\n"
                  + " Expected:\n" + _infoFolders + "\n"
                  + " Found:\n" + _existingFolders);
        }
        final HashMap<String, TableInfo.Column> _columnsTags = new HashMap<String, TableInfo.Column>(2);
        _columnsTags.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTags = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTags = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTags = new TableInfo("tags", _columnsTags, _foreignKeysTags, _indicesTags);
        final TableInfo _existingTags = TableInfo.read(db, "tags");
        if (!_infoTags.equals(_existingTags)) {
          return new RoomOpenHelper.ValidationResult(false, "tags(com.ivarna.truvalt.data.local.entity.TagEntity).\n"
                  + " Expected:\n" + _infoTags + "\n"
                  + " Found:\n" + _existingTags);
        }
        final HashMap<String, TableInfo.Column> _columnsVaultItemTags = new HashMap<String, TableInfo.Column>(2);
        _columnsVaultItemTags.put("itemId", new TableInfo.Column("itemId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItemTags.put("tagId", new TableInfo.Column("tagId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVaultItemTags = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysVaultItemTags.add(new TableInfo.ForeignKey("vault_items", "CASCADE", "NO ACTION", Arrays.asList("itemId"), Arrays.asList("id")));
        _foreignKeysVaultItemTags.add(new TableInfo.ForeignKey("tags", "CASCADE", "NO ACTION", Arrays.asList("tagId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesVaultItemTags = new HashSet<TableInfo.Index>(2);
        _indicesVaultItemTags.add(new TableInfo.Index("index_vault_item_tags_itemId", false, Arrays.asList("itemId"), Arrays.asList("ASC")));
        _indicesVaultItemTags.add(new TableInfo.Index("index_vault_item_tags_tagId", false, Arrays.asList("tagId"), Arrays.asList("ASC")));
        final TableInfo _infoVaultItemTags = new TableInfo("vault_item_tags", _columnsVaultItemTags, _foreignKeysVaultItemTags, _indicesVaultItemTags);
        final TableInfo _existingVaultItemTags = TableInfo.read(db, "vault_item_tags");
        if (!_infoVaultItemTags.equals(_existingVaultItemTags)) {
          return new RoomOpenHelper.ValidationResult(false, "vault_item_tags(com.ivarna.truvalt.data.local.entity.VaultItemTagEntity).\n"
                  + " Expected:\n" + _infoVaultItemTags + "\n"
                  + " Found:\n" + _existingVaultItemTags);
        }
        final HashMap<String, TableInfo.Column> _columnsSyncMetadata = new HashMap<String, TableInfo.Column>(3);
        _columnsSyncMetadata.put("key", new TableInfo.Column("key", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncMetadata.put("value", new TableInfo.Column("value", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncMetadata.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSyncMetadata = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSyncMetadata = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSyncMetadata = new TableInfo("sync_metadata", _columnsSyncMetadata, _foreignKeysSyncMetadata, _indicesSyncMetadata);
        final TableInfo _existingSyncMetadata = TableInfo.read(db, "sync_metadata");
        if (!_infoSyncMetadata.equals(_existingSyncMetadata)) {
          return new RoomOpenHelper.ValidationResult(false, "sync_metadata(com.ivarna.truvalt.data.local.entity.SyncMetadataEntity).\n"
                  + " Expected:\n" + _infoSyncMetadata + "\n"
                  + " Found:\n" + _existingSyncMetadata);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "06735ab850df2dbc712870b55a3d0317", "c713f2f734b21c857cabaa6f7a14c580");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "vault_items","folders","tags","vault_item_tags","sync_metadata");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `vault_items`");
      _db.execSQL("DELETE FROM `folders`");
      _db.execSQL("DELETE FROM `tags`");
      _db.execSQL("DELETE FROM `vault_item_tags`");
      _db.execSQL("DELETE FROM `sync_metadata`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(VaultItemDao.class, VaultItemDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FolderDao.class, FolderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TagDao.class, TagDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public VaultItemDao vaultItemDao() {
    if (_vaultItemDao != null) {
      return _vaultItemDao;
    } else {
      synchronized(this) {
        if(_vaultItemDao == null) {
          _vaultItemDao = new VaultItemDao_Impl(this);
        }
        return _vaultItemDao;
      }
    }
  }

  @Override
  public FolderDao folderDao() {
    if (_folderDao != null) {
      return _folderDao;
    } else {
      synchronized(this) {
        if(_folderDao == null) {
          _folderDao = new FolderDao_Impl(this);
        }
        return _folderDao;
      }
    }
  }

  @Override
  public TagDao tagDao() {
    if (_tagDao != null) {
      return _tagDao;
    } else {
      synchronized(this) {
        if(_tagDao == null) {
          _tagDao = new TagDao_Impl(this);
        }
        return _tagDao;
      }
    }
  }
}
