package com.example.mylocalbank.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
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
public final class RegistroGastoDao_Impl implements RegistroGastoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RegistroGasto> __insertionAdapterOfRegistroGasto;

  private final EntityDeletionOrUpdateAdapter<RegistroGasto> __deletionAdapterOfRegistroGasto;

  private final EntityDeletionOrUpdateAdapter<RegistroGasto> __updateAdapterOfRegistroGasto;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public RegistroGastoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRegistroGasto = new EntityInsertionAdapter<RegistroGasto>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `registros_gastos` (`id`,`monto`,`moneda`,`descripcion`,`tienda`,`tarjetaId`,`fecha`,`categoriaId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegistroGasto entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getMonto());
        statement.bindString(3, entity.getMoneda());
        statement.bindString(4, entity.getDescripcion());
        statement.bindString(5, entity.getTienda());
        if (entity.getTarjetaId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getTarjetaId());
        }
        statement.bindLong(7, entity.getFecha());
        statement.bindLong(8, entity.getCategoriaId());
      }
    };
    this.__deletionAdapterOfRegistroGasto = new EntityDeletionOrUpdateAdapter<RegistroGasto>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `registros_gastos` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegistroGasto entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfRegistroGasto = new EntityDeletionOrUpdateAdapter<RegistroGasto>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `registros_gastos` SET `id` = ?,`monto` = ?,`moneda` = ?,`descripcion` = ?,`tienda` = ?,`tarjetaId` = ?,`fecha` = ?,`categoriaId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegistroGasto entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getMonto());
        statement.bindString(3, entity.getMoneda());
        statement.bindString(4, entity.getDescripcion());
        statement.bindString(5, entity.getTienda());
        if (entity.getTarjetaId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getTarjetaId());
        }
        statement.bindLong(7, entity.getFecha());
        statement.bindLong(8, entity.getCategoriaId());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM registros_gastos";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final RegistroGasto registro, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRegistroGasto.insertAndReturnId(registro);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final RegistroGasto registro, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRegistroGasto.handle(registro);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final RegistroGasto registro, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRegistroGasto.handle(registro);
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
  public Flow<List<RegistroGasto>> getAll() {
    final String _sql = "SELECT * FROM registros_gastos ORDER BY fecha DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"registros_gastos"}, new Callable<List<RegistroGasto>>() {
      @Override
      @NonNull
      public List<RegistroGasto> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfTienda = CursorUtil.getColumnIndexOrThrow(_cursor, "tienda");
          final int _cursorIndexOfTarjetaId = CursorUtil.getColumnIndexOrThrow(_cursor, "tarjetaId");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfCategoriaId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoriaId");
          final List<RegistroGasto> _result = new ArrayList<RegistroGasto>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegistroGasto _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final String _tmpTienda;
            _tmpTienda = _cursor.getString(_cursorIndexOfTienda);
            final Long _tmpTarjetaId;
            if (_cursor.isNull(_cursorIndexOfTarjetaId)) {
              _tmpTarjetaId = null;
            } else {
              _tmpTarjetaId = _cursor.getLong(_cursorIndexOfTarjetaId);
            }
            final long _tmpFecha;
            _tmpFecha = _cursor.getLong(_cursorIndexOfFecha);
            final long _tmpCategoriaId;
            _tmpCategoriaId = _cursor.getLong(_cursorIndexOfCategoriaId);
            _item = new RegistroGasto(_tmpId,_tmpMonto,_tmpMoneda,_tmpDescripcion,_tmpTienda,_tmpTarjetaId,_tmpFecha,_tmpCategoriaId);
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
  public Flow<List<RegistroGasto>> getByDateRange(final long start, final long end) {
    final String _sql = "SELECT * FROM registros_gastos WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"registros_gastos"}, new Callable<List<RegistroGasto>>() {
      @Override
      @NonNull
      public List<RegistroGasto> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfTienda = CursorUtil.getColumnIndexOrThrow(_cursor, "tienda");
          final int _cursorIndexOfTarjetaId = CursorUtil.getColumnIndexOrThrow(_cursor, "tarjetaId");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfCategoriaId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoriaId");
          final List<RegistroGasto> _result = new ArrayList<RegistroGasto>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegistroGasto _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final String _tmpTienda;
            _tmpTienda = _cursor.getString(_cursorIndexOfTienda);
            final Long _tmpTarjetaId;
            if (_cursor.isNull(_cursorIndexOfTarjetaId)) {
              _tmpTarjetaId = null;
            } else {
              _tmpTarjetaId = _cursor.getLong(_cursorIndexOfTarjetaId);
            }
            final long _tmpFecha;
            _tmpFecha = _cursor.getLong(_cursorIndexOfFecha);
            final long _tmpCategoriaId;
            _tmpCategoriaId = _cursor.getLong(_cursorIndexOfCategoriaId);
            _item = new RegistroGasto(_tmpId,_tmpMonto,_tmpMoneda,_tmpDescripcion,_tmpTienda,_tmpTarjetaId,_tmpFecha,_tmpCategoriaId);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
