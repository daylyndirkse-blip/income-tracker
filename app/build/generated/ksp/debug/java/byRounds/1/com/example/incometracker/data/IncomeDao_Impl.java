package com.example.incometracker.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IncomeDao_Impl implements IncomeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IncomeSourceEntity> __insertionAdapterOfIncomeSourceEntity;

  private final EntityInsertionAdapter<IncomeRuleEntity> __insertionAdapterOfIncomeRuleEntity;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<IncomeEntryEntity> __insertionAdapterOfIncomeEntryEntity;

  private final EntityDeletionOrUpdateAdapter<IncomeRuleEntity> __updateAdapterOfIncomeRuleEntity;

  private final EntityDeletionOrUpdateAdapter<IncomeEntryEntity> __updateAdapterOfIncomeEntryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteEntryById;

  public IncomeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIncomeSourceEntity = new EntityInsertionAdapter<IncomeSourceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `income_sources` (`id`,`name`) VALUES (nullif(?, 0),?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeSourceEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
      }
    };
    this.__insertionAdapterOfIncomeRuleEntity = new EntityInsertionAdapter<IncomeRuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `income_rules` (`id`,`sourceId`,`amountCents`,`recurrence`,`anchorDate`,`note`,`active`,`lastGeneratedDate`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeRuleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSourceId());
        statement.bindLong(3, entity.getAmountCents());
        final String _tmp = __converters.recurrenceToString(entity.getRecurrence());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        final Long _tmp_1 = __converters.localDateToEpochDay(entity.getAnchorDate());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_1);
        }
        if (entity.getNote() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getNote());
        }
        final int _tmp_2 = entity.getActive() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
        final Long _tmp_3 = __converters.localDateToEpochDay(entity.getLastGeneratedDate());
        if (_tmp_3 == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, _tmp_3);
        }
      }
    };
    this.__insertionAdapterOfIncomeEntryEntity = new EntityInsertionAdapter<IncomeEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `income_entries` (`id`,`sourceId`,`amountCents`,`date`,`note`,`ruleId`,`createdAtEpochMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSourceId());
        statement.bindLong(3, entity.getAmountCents());
        final Long _tmp = __converters.localDateToEpochDay(entity.getDate());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp);
        }
        if (entity.getNote() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getNote());
        }
        if (entity.getRuleId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getRuleId());
        }
        statement.bindLong(7, entity.getCreatedAtEpochMillis());
      }
    };
    this.__updateAdapterOfIncomeRuleEntity = new EntityDeletionOrUpdateAdapter<IncomeRuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `income_rules` SET `id` = ?,`sourceId` = ?,`amountCents` = ?,`recurrence` = ?,`anchorDate` = ?,`note` = ?,`active` = ?,`lastGeneratedDate` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeRuleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSourceId());
        statement.bindLong(3, entity.getAmountCents());
        final String _tmp = __converters.recurrenceToString(entity.getRecurrence());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        final Long _tmp_1 = __converters.localDateToEpochDay(entity.getAnchorDate());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_1);
        }
        if (entity.getNote() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getNote());
        }
        final int _tmp_2 = entity.getActive() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
        final Long _tmp_3 = __converters.localDateToEpochDay(entity.getLastGeneratedDate());
        if (_tmp_3 == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, _tmp_3);
        }
        statement.bindLong(9, entity.getId());
      }
    };
    this.__updateAdapterOfIncomeEntryEntity = new EntityDeletionOrUpdateAdapter<IncomeEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `income_entries` SET `id` = ?,`sourceId` = ?,`amountCents` = ?,`date` = ?,`note` = ?,`ruleId` = ?,`createdAtEpochMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSourceId());
        statement.bindLong(3, entity.getAmountCents());
        final Long _tmp = __converters.localDateToEpochDay(entity.getDate());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp);
        }
        if (entity.getNote() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getNote());
        }
        if (entity.getRuleId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getRuleId());
        }
        statement.bindLong(7, entity.getCreatedAtEpochMillis());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteEntryById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM income_entries WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertSource(final IncomeSourceEntity source,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfIncomeSourceEntity.insertAndReturnId(source);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertRule(final IncomeRuleEntity rule,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfIncomeRuleEntity.insertAndReturnId(rule);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertEntry(final IncomeEntryEntity entry,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfIncomeEntryEntity.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertEntries(final List<IncomeEntryEntity> entries,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfIncomeEntryEntity.insertAndReturnIdsList(entries);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRule(final IncomeRuleEntity rule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfIncomeRuleEntity.handle(rule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateEntry(final IncomeEntryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfIncomeEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteEntryById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteEntryById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteEntryById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<IncomeSourceEntity>> observeSources() {
    final String _sql = "SELECT * FROM income_sources ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income_sources"}, new Callable<List<IncomeSourceEntity>>() {
      @Override
      @NonNull
      public List<IncomeSourceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final List<IncomeSourceEntity> _result = new ArrayList<IncomeSourceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncomeSourceEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            _item = new IncomeSourceEntity(_tmpId,_tmpName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getSourceByName(final String name,
      final Continuation<? super IncomeSourceEntity> $completion) {
    final String _sql = "SELECT * FROM income_sources WHERE name = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IncomeSourceEntity>() {
      @Override
      @Nullable
      public IncomeSourceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final IncomeSourceEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            _result = new IncomeSourceEntity(_tmpId,_tmpName);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getActiveRules(final Continuation<? super List<IncomeRuleEntity>> $completion) {
    final String _sql = "SELECT * FROM income_rules WHERE active = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IncomeRuleEntity>>() {
      @Override
      @NonNull
      public List<IncomeRuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSourceId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceId");
          final int _cursorIndexOfAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "amountCents");
          final int _cursorIndexOfRecurrence = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence");
          final int _cursorIndexOfAnchorDate = CursorUtil.getColumnIndexOrThrow(_cursor, "anchorDate");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final int _cursorIndexOfLastGeneratedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastGeneratedDate");
          final List<IncomeRuleEntity> _result = new ArrayList<IncomeRuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncomeRuleEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSourceId;
            _tmpSourceId = _cursor.getLong(_cursorIndexOfSourceId);
            final long _tmpAmountCents;
            _tmpAmountCents = _cursor.getLong(_cursorIndexOfAmountCents);
            final Recurrence _tmpRecurrence;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfRecurrence)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfRecurrence);
            }
            final Recurrence _tmp_1 = __converters.stringToRecurrence(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.example.incometracker.data.Recurrence', but it was NULL.");
            } else {
              _tmpRecurrence = _tmp_1;
            }
            final LocalDate _tmpAnchorDate;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfAnchorDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfAnchorDate);
            }
            final LocalDate _tmp_3 = __converters.epochDayToLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpAnchorDate = _tmp_3;
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final boolean _tmpActive;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp_4 != 0;
            final LocalDate _tmpLastGeneratedDate;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastGeneratedDate)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastGeneratedDate);
            }
            _tmpLastGeneratedDate = __converters.epochDayToLocalDate(_tmp_5);
            _item = new IncomeRuleEntity(_tmpId,_tmpSourceId,_tmpAmountCents,_tmpRecurrence,_tmpAnchorDate,_tmpNote,_tmpActive,_tmpLastGeneratedDate);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<Long> observeTotalBetween(final LocalDate start, final LocalDate end) {
    final String _sql = "SELECT COALESCE(SUM(amountCents), 0) FROM income_entries WHERE date BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final Long _tmp = __converters.localDateToEpochDay(start);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp);
    }
    _argIndex = 2;
    final Long _tmp_1 = __converters.localDateToEpochDay(end);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp_1);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income_entries"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp_2;
            _tmp_2 = _cursor.getLong(0);
            _result = _tmp_2;
          } else {
            _result = 0L;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DailyTotal>> observeDailyTotalsBetween(final LocalDate start,
      final LocalDate end) {
    final String _sql = "\n"
            + "        SELECT date AS date, COALESCE(SUM(amountCents), 0) AS totalCents\n"
            + "        FROM income_entries\n"
            + "        WHERE date BETWEEN ? AND ?\n"
            + "        GROUP BY date\n"
            + "        ORDER BY date ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final Long _tmp = __converters.localDateToEpochDay(start);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp);
    }
    _argIndex = 2;
    final Long _tmp_1 = __converters.localDateToEpochDay(end);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp_1);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income_entries"}, new Callable<List<DailyTotal>>() {
      @Override
      @NonNull
      public List<DailyTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = 0;
          final int _cursorIndexOfTotalCents = 1;
          final List<DailyTotal> _result = new ArrayList<DailyTotal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyTotal _item;
            final LocalDate _tmpDate;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfDate);
            }
            final LocalDate _tmp_3 = __converters.epochDayToLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_3;
            }
            final long _tmpTotalCents;
            _tmpTotalCents = _cursor.getLong(_cursorIndexOfTotalCents);
            _item = new DailyTotal(_tmpDate,_tmpTotalCents);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<IncomeEntryWithSource>> observeEntriesWithSourceForDate(final LocalDate date) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            e.id AS id,\n"
            + "            s.name AS sourceName,\n"
            + "            e.amountCents AS amountCents,\n"
            + "            e.date AS date,\n"
            + "            e.note AS note,\n"
            + "            e.createdAtEpochMillis AS createdAtEpochMillis\n"
            + "        FROM income_entries e\n"
            + "        JOIN income_sources s ON s.id = e.sourceId\n"
            + "        WHERE e.date = ?\n"
            + "        ORDER BY e.createdAtEpochMillis DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final Long _tmp = __converters.localDateToEpochDay(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income_entries",
        "income_sources"}, new Callable<List<IncomeEntryWithSource>>() {
      @Override
      @NonNull
      public List<IncomeEntryWithSource> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfSourceName = 1;
          final int _cursorIndexOfAmountCents = 2;
          final int _cursorIndexOfDate = 3;
          final int _cursorIndexOfNote = 4;
          final int _cursorIndexOfCreatedAtEpochMillis = 5;
          final List<IncomeEntryWithSource> _result = new ArrayList<IncomeEntryWithSource>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncomeEntryWithSource _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSourceName;
            _tmpSourceName = _cursor.getString(_cursorIndexOfSourceName);
            final long _tmpAmountCents;
            _tmpAmountCents = _cursor.getLong(_cursorIndexOfAmountCents);
            final LocalDate _tmpDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfDate);
            }
            final LocalDate _tmp_2 = __converters.epochDayToLocalDate(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_2;
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            _item = new IncomeEntryWithSource(_tmpId,_tmpSourceName,_tmpAmountCents,_tmpDate,_tmpNote,_tmpCreatedAtEpochMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<SourceTotal>> observeTotalsBySourceBetween(final LocalDate start,
      final LocalDate end) {
    final String _sql = "\n"
            + "        SELECT s.name AS sourceName, COALESCE(SUM(e.amountCents), 0) AS totalCents\n"
            + "        FROM income_entries e\n"
            + "        JOIN income_sources s ON s.id = e.sourceId\n"
            + "        WHERE e.date BETWEEN ? AND ?\n"
            + "        GROUP BY e.sourceId\n"
            + "        ORDER BY totalCents DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final Long _tmp = __converters.localDateToEpochDay(start);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp);
    }
    _argIndex = 2;
    final Long _tmp_1 = __converters.localDateToEpochDay(end);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp_1);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income_entries",
        "income_sources"}, new Callable<List<SourceTotal>>() {
      @Override
      @NonNull
      public List<SourceTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSourceName = 0;
          final int _cursorIndexOfTotalCents = 1;
          final List<SourceTotal> _result = new ArrayList<SourceTotal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SourceTotal _item;
            final String _tmpSourceName;
            _tmpSourceName = _cursor.getString(_cursorIndexOfSourceName);
            final long _tmpTotalCents;
            _tmpTotalCents = _cursor.getLong(_cursorIndexOfTotalCents);
            _item = new SourceTotal(_tmpSourceName,_tmpTotalCents);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getEntryById(final long id,
      final Continuation<? super IncomeEntryEntity> $completion) {
    final String _sql = "SELECT * FROM income_entries WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IncomeEntryEntity>() {
      @Override
      @Nullable
      public IncomeEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSourceId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceId");
          final int _cursorIndexOfAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "amountCents");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfRuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "ruleId");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final IncomeEntryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSourceId;
            _tmpSourceId = _cursor.getLong(_cursorIndexOfSourceId);
            final long _tmpAmountCents;
            _tmpAmountCents = _cursor.getLong(_cursorIndexOfAmountCents);
            final LocalDate _tmpDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfDate);
            }
            final LocalDate _tmp_1 = __converters.epochDayToLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_1;
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final Long _tmpRuleId;
            if (_cursor.isNull(_cursorIndexOfRuleId)) {
              _tmpRuleId = null;
            } else {
              _tmpRuleId = _cursor.getLong(_cursorIndexOfRuleId);
            }
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            _result = new IncomeEntryEntity(_tmpId,_tmpSourceId,_tmpAmountCents,_tmpDate,_tmpNote,_tmpRuleId,_tmpCreatedAtEpochMillis);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
