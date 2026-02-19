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
public final class RegistroIngresoDao_Impl implements RegistroIngresoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RegistroIngreso> __insertionAdapterOfRegistroIngreso;

  private final EntityDeletionOrUpdateAdapter<RegistroIngreso> __deletionAdapterOfRegistroIngreso;

  private final EntityDeletionOrUpdateAdapter<RegistroIngreso> __updateAdapterOfRegistroIngreso;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public RegistroIngresoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRegistroIngreso = new EntityInsertionAdapter<RegistroIngreso>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `registros_ingresos` (`id`,`monto`,`moneda`,`descripcion`,`fecha`,`categoriaId`,`fuenteId`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegistroIngreso entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getMonto());
        statement.bindString(3, entity.getMoneda());
        statement.bindString(4, entity.getDescripcion());
        statement.bindLong(5, entity.getFecha());
        statement.bindLong(6, entity.getCategoriaId());
        if (entity.getFuenteId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getFuenteId());
        }
      }
    };
    this.__deletionAdapterOfRegistroIngreso = new EntityDeletionOrUpdateAdapter<RegistroIngreso>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `registros_ingresos` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegistroIngreso entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfRegistroIngreso = new EntityDeletionOrUpdateAdapter<RegistroIngreso>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `registros_ingresos` SET `id` = ?,`monto` = ?,`moneda` = ?,`descripcion` = ?,`fecha` = ?,`categoriaId` = ?,`fuenteId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RegistroIngreso entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getMonto());
        statement.bindString(3, entity.getMoneda());
        statement.bindString(4, entity.getDescripcion());
        statement.bindLong(5, entity.getFecha());
        statement.bindLong(6, entity.getCategoriaId());
        if (entity.getFuenteId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getFuenteId());
        }
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM registros_ingresos";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final RegistroIngreso registro,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRegistroIngreso.insertAndReturnId(registro);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final RegistroIngreso registro,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRegistroIngreso.handle(registro);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final RegistroIngreso registro,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRegistroIngreso.handle(registro);
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
  public Flow<List<RegistroIngreso>> getAll() {
    final String _sql = "SELECT * FROM registros_ingresos ORDER BY fecha DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"registros_ingresos"}, new Callable<List<RegistroIngreso>>() {
      @Override
      @NonNull
      public List<RegistroIngreso> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfCategoriaId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoriaId");
          final int _cursorIndexOfFuenteId = CursorUtil.getColumnIndexOrThrow(_cursor, "fuenteId");
          final List<RegistroIngreso> _result = new ArrayList<RegistroIngreso>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegistroIngreso _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final long _tmpFecha;
            _tmpFecha = _cursor.getLong(_cursorIndexOfFecha);
            final long _tmpCategoriaId;
            _tmpCategoriaId = _cursor.getLong(_cursorIndexOfCategoriaId);
            final Long _tmpFuenteId;
            if (_cursor.isNull(_cursorIndexOfFuenteId)) {
              _tmpFuenteId = null;
            } else {
              _tmpFuenteId = _cursor.getLong(_cursorIndexOfFuenteId);
            }
            _item = new RegistroIngreso(_tmpId,_tmpMonto,_tmpMoneda,_tmpDescripcion,_tmpFecha,_tmpCategoriaId,_tmpFuenteId);
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
  public Flow<List<RegistroIngreso>> getByDateRange(final long start, final long end) {
    final String _sql = "SELECT * FROM registros_ingresos WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"registros_ingresos"}, new Callable<List<RegistroIngreso>>() {
      @Override
      @NonNull
      public List<RegistroIngreso> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfCategoriaId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoriaId");
          final int _cursorIndexOfFuenteId = CursorUtil.getColumnIndexOrThrow(_cursor, "fuenteId");
          final List<RegistroIngreso> _result = new ArrayList<RegistroIngreso>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RegistroIngreso _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final long _tmpFecha;
            _tmpFecha = _cursor.getLong(_cursorIndexOfFecha);
            final long _tmpCategoriaId;
            _tmpCategoriaId = _cursor.getLong(_cursorIndexOfCategoriaId);
            final Long _tmpFuenteId;
            if (_cursor.isNull(_cursorIndexOfFuenteId)) {
              _tmpFuenteId = null;
            } else {
              _tmpFuenteId = _cursor.getLong(_cursorIndexOfFuenteId);
            }
            _item = new RegistroIngreso(_tmpId,_tmpMonto,_tmpMoneda,_tmpDescripcion,_tmpFecha,_tmpCategoriaId,_tmpFuenteId);
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
  public Object getPaymentForSource(final long sourceId, final long start, final long end,
      final Continuation<? super RegistroIngreso> $completion) {
    final String _sql = "SELECT * FROM registros_ingresos WHERE fuenteId = ? AND fecha BETWEEN ? AND ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sourceId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, start);
    _argIndex = 3;
    _statement.bindLong(_argIndex, end);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RegistroIngreso>() {
      @Override
      @Nullable
      public RegistroIngreso call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfCategoriaId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoriaId");
          final int _cursorIndexOfFuenteId = CursorUtil.getColumnIndexOrThrow(_cursor, "fuenteId");
          final RegistroIngreso _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final long _tmpFecha;
            _tmpFecha = _cursor.getLong(_cursorIndexOfFecha);
            final long _tmpCategoriaId;
            _tmpCategoriaId = _cursor.getLong(_cursorIndexOfCategoriaId);
            final Long _tmpFuenteId;
            if (_cursor.isNull(_cursorIndexOfFuenteId)) {
              _tmpFuenteId = null;
            } else {
              _tmpFuenteId = _cursor.getLong(_cursorIndexOfFuenteId);
            }
            _result = new RegistroIngreso(_tmpId,_tmpMonto,_tmpMoneda,_tmpDescripcion,_tmpFecha,_tmpCategoriaId,_tmpFuenteId);
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
