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
public final class GastoFijoDao_Impl implements GastoFijoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GastoFijo> __insertionAdapterOfGastoFijo;

  private final EntityDeletionOrUpdateAdapter<GastoFijo> __deletionAdapterOfGastoFijo;

  private final EntityDeletionOrUpdateAdapter<GastoFijo> __updateAdapterOfGastoFijo;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public GastoFijoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGastoFijo = new EntityInsertionAdapter<GastoFijo>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `gastos_fijos` (`id`,`nombre`,`monto`,`moneda`,`tarjetaId`,`diaCobro`,`activa`,`fechaCreacion`,`categoriaId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GastoFijo entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNombre());
        statement.bindDouble(3, entity.getMonto());
        statement.bindString(4, entity.getMoneda());
        if (entity.getTarjetaId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getTarjetaId());
        }
        statement.bindLong(6, entity.getDiaCobro());
        final int _tmp = entity.getActiva() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getFechaCreacion());
        statement.bindLong(9, entity.getCategoriaId());
      }
    };
    this.__deletionAdapterOfGastoFijo = new EntityDeletionOrUpdateAdapter<GastoFijo>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `gastos_fijos` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GastoFijo entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfGastoFijo = new EntityDeletionOrUpdateAdapter<GastoFijo>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `gastos_fijos` SET `id` = ?,`nombre` = ?,`monto` = ?,`moneda` = ?,`tarjetaId` = ?,`diaCobro` = ?,`activa` = ?,`fechaCreacion` = ?,`categoriaId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GastoFijo entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNombre());
        statement.bindDouble(3, entity.getMonto());
        statement.bindString(4, entity.getMoneda());
        if (entity.getTarjetaId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getTarjetaId());
        }
        statement.bindLong(6, entity.getDiaCobro());
        final int _tmp = entity.getActiva() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getFechaCreacion());
        statement.bindLong(9, entity.getCategoriaId());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM gastos_fijos";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final GastoFijo gastoFijo, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfGastoFijo.insertAndReturnId(gastoFijo);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final GastoFijo gastoFijo, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfGastoFijo.handle(gastoFijo);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final GastoFijo gastoFijo, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfGastoFijo.handle(gastoFijo);
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
  public Flow<List<GastoFijo>> getAll() {
    final String _sql = "SELECT * FROM gastos_fijos ORDER BY fechaCreacion DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"gastos_fijos"}, new Callable<List<GastoFijo>>() {
      @Override
      @NonNull
      public List<GastoFijo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfTarjetaId = CursorUtil.getColumnIndexOrThrow(_cursor, "tarjetaId");
          final int _cursorIndexOfDiaCobro = CursorUtil.getColumnIndexOrThrow(_cursor, "diaCobro");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfCategoriaId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoriaId");
          final List<GastoFijo> _result = new ArrayList<GastoFijo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GastoFijo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final Long _tmpTarjetaId;
            if (_cursor.isNull(_cursorIndexOfTarjetaId)) {
              _tmpTarjetaId = null;
            } else {
              _tmpTarjetaId = _cursor.getLong(_cursorIndexOfTarjetaId);
            }
            final int _tmpDiaCobro;
            _tmpDiaCobro = _cursor.getInt(_cursorIndexOfDiaCobro);
            final boolean _tmpActiva;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            final long _tmpCategoriaId;
            _tmpCategoriaId = _cursor.getLong(_cursorIndexOfCategoriaId);
            _item = new GastoFijo(_tmpId,_tmpNombre,_tmpMonto,_tmpMoneda,_tmpTarjetaId,_tmpDiaCobro,_tmpActiva,_tmpFechaCreacion,_tmpCategoriaId);
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
  public Flow<List<GastoFijo>> getActivos() {
    final String _sql = "SELECT * FROM gastos_fijos WHERE activa = 1 ORDER BY diaCobro ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"gastos_fijos"}, new Callable<List<GastoFijo>>() {
      @Override
      @NonNull
      public List<GastoFijo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfTarjetaId = CursorUtil.getColumnIndexOrThrow(_cursor, "tarjetaId");
          final int _cursorIndexOfDiaCobro = CursorUtil.getColumnIndexOrThrow(_cursor, "diaCobro");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfCategoriaId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoriaId");
          final List<GastoFijo> _result = new ArrayList<GastoFijo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GastoFijo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final Long _tmpTarjetaId;
            if (_cursor.isNull(_cursorIndexOfTarjetaId)) {
              _tmpTarjetaId = null;
            } else {
              _tmpTarjetaId = _cursor.getLong(_cursorIndexOfTarjetaId);
            }
            final int _tmpDiaCobro;
            _tmpDiaCobro = _cursor.getInt(_cursorIndexOfDiaCobro);
            final boolean _tmpActiva;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            final long _tmpCategoriaId;
            _tmpCategoriaId = _cursor.getLong(_cursorIndexOfCategoriaId);
            _item = new GastoFijo(_tmpId,_tmpNombre,_tmpMonto,_tmpMoneda,_tmpTarjetaId,_tmpDiaCobro,_tmpActiva,_tmpFechaCreacion,_tmpCategoriaId);
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
  public Flow<List<GastoFijo>> getActivosCobradosHasta(final int maxDay) {
    final String _sql = "SELECT * FROM gastos_fijos WHERE activa = 1 AND diaCobro <= ? ORDER BY diaCobro ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, maxDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"gastos_fijos"}, new Callable<List<GastoFijo>>() {
      @Override
      @NonNull
      public List<GastoFijo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfTarjetaId = CursorUtil.getColumnIndexOrThrow(_cursor, "tarjetaId");
          final int _cursorIndexOfDiaCobro = CursorUtil.getColumnIndexOrThrow(_cursor, "diaCobro");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfCategoriaId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoriaId");
          final List<GastoFijo> _result = new ArrayList<GastoFijo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GastoFijo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final Long _tmpTarjetaId;
            if (_cursor.isNull(_cursorIndexOfTarjetaId)) {
              _tmpTarjetaId = null;
            } else {
              _tmpTarjetaId = _cursor.getLong(_cursorIndexOfTarjetaId);
            }
            final int _tmpDiaCobro;
            _tmpDiaCobro = _cursor.getInt(_cursorIndexOfDiaCobro);
            final boolean _tmpActiva;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            final long _tmpCategoriaId;
            _tmpCategoriaId = _cursor.getLong(_cursorIndexOfCategoriaId);
            _item = new GastoFijo(_tmpId,_tmpNombre,_tmpMonto,_tmpMoneda,_tmpTarjetaId,_tmpDiaCobro,_tmpActiva,_tmpFechaCreacion,_tmpCategoriaId);
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
  public Object getById(final long id, final Continuation<? super GastoFijo> $completion) {
    final String _sql = "SELECT * FROM gastos_fijos WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GastoFijo>() {
      @Override
      @Nullable
      public GastoFijo call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfTarjetaId = CursorUtil.getColumnIndexOrThrow(_cursor, "tarjetaId");
          final int _cursorIndexOfDiaCobro = CursorUtil.getColumnIndexOrThrow(_cursor, "diaCobro");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final int _cursorIndexOfCategoriaId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoriaId");
          final GastoFijo _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final Long _tmpTarjetaId;
            if (_cursor.isNull(_cursorIndexOfTarjetaId)) {
              _tmpTarjetaId = null;
            } else {
              _tmpTarjetaId = _cursor.getLong(_cursorIndexOfTarjetaId);
            }
            final int _tmpDiaCobro;
            _tmpDiaCobro = _cursor.getInt(_cursorIndexOfDiaCobro);
            final boolean _tmpActiva;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            final long _tmpCategoriaId;
            _tmpCategoriaId = _cursor.getLong(_cursorIndexOfCategoriaId);
            _result = new GastoFijo(_tmpId,_tmpNombre,_tmpMonto,_tmpMoneda,_tmpTarjetaId,_tmpDiaCobro,_tmpActiva,_tmpFechaCreacion,_tmpCategoriaId);
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
