package com.example.mylocalbank.data;

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
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
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
public final class TarjetaDao_Impl implements TarjetaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Tarjeta> __insertionAdapterOfTarjeta;

  private final EntityDeletionOrUpdateAdapter<Tarjeta> __deletionAdapterOfTarjeta;

  private final EntityDeletionOrUpdateAdapter<Tarjeta> __updateAdapterOfTarjeta;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public TarjetaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTarjeta = new EntityInsertionAdapter<Tarjeta>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `tarjetas` (`id`,`alias`,`ultimos4`,`tipo`,`banco`,`color`,`activa`,`fechaCreacion`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tarjeta entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getAlias());
        statement.bindString(3, entity.getUltimos4());
        statement.bindString(4, entity.getTipo());
        statement.bindString(5, entity.getBanco());
        statement.bindString(6, entity.getColor());
        final int _tmp = entity.getActiva() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getFechaCreacion());
      }
    };
    this.__deletionAdapterOfTarjeta = new EntityDeletionOrUpdateAdapter<Tarjeta>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `tarjetas` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tarjeta entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTarjeta = new EntityDeletionOrUpdateAdapter<Tarjeta>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tarjetas` SET `id` = ?,`alias` = ?,`ultimos4` = ?,`tipo` = ?,`banco` = ?,`color` = ?,`activa` = ?,`fechaCreacion` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tarjeta entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getAlias());
        statement.bindString(3, entity.getUltimos4());
        statement.bindString(4, entity.getTipo());
        statement.bindString(5, entity.getBanco());
        statement.bindString(6, entity.getColor());
        final int _tmp = entity.getActiva() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getFechaCreacion());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tarjetas";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Tarjeta tarjeta, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTarjeta.insertAndReturnId(tarjeta);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Tarjeta tarjeta, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTarjeta.handle(tarjeta);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Tarjeta tarjeta, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTarjeta.handle(tarjeta);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
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
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Tarjeta>> getAll() {
    final String _sql = "SELECT * FROM tarjetas ORDER BY fechaCreacion DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tarjetas"}, new Callable<List<Tarjeta>>() {
      @Override
      @NonNull
      public List<Tarjeta> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAlias = CursorUtil.getColumnIndexOrThrow(_cursor, "alias");
          final int _cursorIndexOfUltimos4 = CursorUtil.getColumnIndexOrThrow(_cursor, "ultimos4");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfBanco = CursorUtil.getColumnIndexOrThrow(_cursor, "banco");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final List<Tarjeta> _result = new ArrayList<Tarjeta>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Tarjeta _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpAlias;
            _tmpAlias = _cursor.getString(_cursorIndexOfAlias);
            final String _tmpUltimos4;
            _tmpUltimos4 = _cursor.getString(_cursorIndexOfUltimos4);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final String _tmpBanco;
            _tmpBanco = _cursor.getString(_cursorIndexOfBanco);
            final String _tmpColor;
            _tmpColor = _cursor.getString(_cursorIndexOfColor);
            final boolean _tmpActiva;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            _item = new Tarjeta(_tmpId,_tmpAlias,_tmpUltimos4,_tmpTipo,_tmpBanco,_tmpColor,_tmpActiva,_tmpFechaCreacion);
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
  public Flow<List<Tarjeta>> getActivas() {
    final String _sql = "SELECT * FROM tarjetas WHERE activa = 1 ORDER BY alias ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tarjetas"}, new Callable<List<Tarjeta>>() {
      @Override
      @NonNull
      public List<Tarjeta> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAlias = CursorUtil.getColumnIndexOrThrow(_cursor, "alias");
          final int _cursorIndexOfUltimos4 = CursorUtil.getColumnIndexOrThrow(_cursor, "ultimos4");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfBanco = CursorUtil.getColumnIndexOrThrow(_cursor, "banco");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final List<Tarjeta> _result = new ArrayList<Tarjeta>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Tarjeta _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpAlias;
            _tmpAlias = _cursor.getString(_cursorIndexOfAlias);
            final String _tmpUltimos4;
            _tmpUltimos4 = _cursor.getString(_cursorIndexOfUltimos4);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final String _tmpBanco;
            _tmpBanco = _cursor.getString(_cursorIndexOfBanco);
            final String _tmpColor;
            _tmpColor = _cursor.getString(_cursorIndexOfColor);
            final boolean _tmpActiva;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            _item = new Tarjeta(_tmpId,_tmpAlias,_tmpUltimos4,_tmpTipo,_tmpBanco,_tmpColor,_tmpActiva,_tmpFechaCreacion);
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
  public Object getById(final long id, final Continuation<? super Tarjeta> $completion) {
    final String _sql = "SELECT * FROM tarjetas WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Tarjeta>() {
      @Override
      @Nullable
      public Tarjeta call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAlias = CursorUtil.getColumnIndexOrThrow(_cursor, "alias");
          final int _cursorIndexOfUltimos4 = CursorUtil.getColumnIndexOrThrow(_cursor, "ultimos4");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfBanco = CursorUtil.getColumnIndexOrThrow(_cursor, "banco");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final Tarjeta _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpAlias;
            _tmpAlias = _cursor.getString(_cursorIndexOfAlias);
            final String _tmpUltimos4;
            _tmpUltimos4 = _cursor.getString(_cursorIndexOfUltimos4);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final String _tmpBanco;
            _tmpBanco = _cursor.getString(_cursorIndexOfBanco);
            final String _tmpColor;
            _tmpColor = _cursor.getString(_cursorIndexOfColor);
            final boolean _tmpActiva;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            _result = new Tarjeta(_tmpId,_tmpAlias,_tmpUltimos4,_tmpTipo,_tmpBanco,_tmpColor,_tmpActiva,_tmpFechaCreacion);
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
