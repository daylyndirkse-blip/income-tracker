package com.example.incometracker.data;

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
public final class AppDatabase_Impl extends AppDatabase {
  private volatile IncomeDao _incomeDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `income_sources` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_income_sources_name` ON `income_sources` (`name`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `income_rules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sourceId` INTEGER NOT NULL, `amountCents` INTEGER NOT NULL, `recurrence` TEXT NOT NULL, `anchorDate` INTEGER NOT NULL, `note` TEXT, `active` INTEGER NOT NULL, `lastGeneratedDate` INTEGER, FOREIGN KEY(`sourceId`) REFERENCES `income_sources`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_income_rules_sourceId` ON `income_rules` (`sourceId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `income_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sourceId` INTEGER NOT NULL, `amountCents` INTEGER NOT NULL, `date` INTEGER NOT NULL, `note` TEXT, `ruleId` INTEGER, `createdAtEpochMillis` INTEGER NOT NULL, FOREIGN KEY(`sourceId`) REFERENCES `income_sources`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`ruleId`) REFERENCES `income_rules`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_income_entries_date` ON `income_entries` (`date`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_income_entries_sourceId` ON `income_entries` (`sourceId`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_income_entries_ruleId_date` ON `income_entries` (`ruleId`, `date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8165e11e561edb439df425884153275e')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `income_sources`");
        db.execSQL("DROP TABLE IF EXISTS `income_rules`");
        db.execSQL("DROP TABLE IF EXISTS `income_entries`");
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
        final HashMap<String, TableInfo.Column> _columnsIncomeSources = new HashMap<String, TableInfo.Column>(2);
        _columnsIncomeSources.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeSources.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncomeSources = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIncomeSources = new HashSet<TableInfo.Index>(1);
        _indicesIncomeSources.add(new TableInfo.Index("index_income_sources_name", true, Arrays.asList("name"), Arrays.asList("ASC")));
        final TableInfo _infoIncomeSources = new TableInfo("income_sources", _columnsIncomeSources, _foreignKeysIncomeSources, _indicesIncomeSources);
        final TableInfo _existingIncomeSources = TableInfo.read(db, "income_sources");
        if (!_infoIncomeSources.equals(_existingIncomeSources)) {
          return new RoomOpenHelper.ValidationResult(false, "income_sources(com.example.incometracker.data.IncomeSourceEntity).\n"
                  + " Expected:\n" + _infoIncomeSources + "\n"
                  + " Found:\n" + _existingIncomeSources);
        }
        final HashMap<String, TableInfo.Column> _columnsIncomeRules = new HashMap<String, TableInfo.Column>(8);
        _columnsIncomeRules.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeRules.put("sourceId", new TableInfo.Column("sourceId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeRules.put("amountCents", new TableInfo.Column("amountCents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeRules.put("recurrence", new TableInfo.Column("recurrence", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeRules.put("anchorDate", new TableInfo.Column("anchorDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeRules.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeRules.put("active", new TableInfo.Column("active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeRules.put("lastGeneratedDate", new TableInfo.Column("lastGeneratedDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncomeRules = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysIncomeRules.add(new TableInfo.ForeignKey("income_sources", "RESTRICT", "NO ACTION", Arrays.asList("sourceId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesIncomeRules = new HashSet<TableInfo.Index>(1);
        _indicesIncomeRules.add(new TableInfo.Index("index_income_rules_sourceId", false, Arrays.asList("sourceId"), Arrays.asList("ASC")));
        final TableInfo _infoIncomeRules = new TableInfo("income_rules", _columnsIncomeRules, _foreignKeysIncomeRules, _indicesIncomeRules);
        final TableInfo _existingIncomeRules = TableInfo.read(db, "income_rules");
        if (!_infoIncomeRules.equals(_existingIncomeRules)) {
          return new RoomOpenHelper.ValidationResult(false, "income_rules(com.example.incometracker.data.IncomeRuleEntity).\n"
                  + " Expected:\n" + _infoIncomeRules + "\n"
                  + " Found:\n" + _existingIncomeRules);
        }
        final HashMap<String, TableInfo.Column> _columnsIncomeEntries = new HashMap<String, TableInfo.Column>(7);
        _columnsIncomeEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeEntries.put("sourceId", new TableInfo.Column("sourceId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeEntries.put("amountCents", new TableInfo.Column("amountCents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeEntries.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeEntries.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeEntries.put("ruleId", new TableInfo.Column("ruleId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeEntries.put("createdAtEpochMillis", new TableInfo.Column("createdAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncomeEntries = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysIncomeEntries.add(new TableInfo.ForeignKey("income_sources", "RESTRICT", "NO ACTION", Arrays.asList("sourceId"), Arrays.asList("id")));
        _foreignKeysIncomeEntries.add(new TableInfo.ForeignKey("income_rules", "SET NULL", "NO ACTION", Arrays.asList("ruleId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesIncomeEntries = new HashSet<TableInfo.Index>(3);
        _indicesIncomeEntries.add(new TableInfo.Index("index_income_entries_date", false, Arrays.asList("date"), Arrays.asList("ASC")));
        _indicesIncomeEntries.add(new TableInfo.Index("index_income_entries_sourceId", false, Arrays.asList("sourceId"), Arrays.asList("ASC")));
        _indicesIncomeEntries.add(new TableInfo.Index("index_income_entries_ruleId_date", true, Arrays.asList("ruleId", "date"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoIncomeEntries = new TableInfo("income_entries", _columnsIncomeEntries, _foreignKeysIncomeEntries, _indicesIncomeEntries);
        final TableInfo _existingIncomeEntries = TableInfo.read(db, "income_entries");
        if (!_infoIncomeEntries.equals(_existingIncomeEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "income_entries(com.example.incometracker.data.IncomeEntryEntity).\n"
                  + " Expected:\n" + _infoIncomeEntries + "\n"
                  + " Found:\n" + _existingIncomeEntries);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "8165e11e561edb439df425884153275e", "af775bb61200f9982aae2ec0d1aff1be");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "income_sources","income_rules","income_entries");
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
      _db.execSQL("DELETE FROM `income_rules`");
      _db.execSQL("DELETE FROM `income_entries`");
      _db.execSQL("DELETE FROM `income_sources`");
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
    _typeConvertersMap.put(IncomeDao.class, IncomeDao_Impl.getRequiredConverters());
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
  public IncomeDao incomeDao() {
    if (_incomeDao != null) {
      return _incomeDao;
    } else {
      synchronized(this) {
        if(_incomeDao == null) {
          _incomeDao = new IncomeDao_Impl(this);
        }
        return _incomeDao;
      }
    }
  }
}
