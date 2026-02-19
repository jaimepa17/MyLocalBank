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
import java.lang.Integer;
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
public final class FuenteIngresoDao_Impl implements FuenteIngresoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FuenteIngreso> __insertionAdapterOfFuenteIngreso;

  private final EntityDeletionOrUpdateAdapter<FuenteIngreso> __deletionAdapterOfFuenteIngreso;

  private final EntityDeletionOrUpdateAdapter<FuenteIngreso> __updateAdapterOfFuenteIngreso;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public FuenteIngresoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFuenteIngreso = new EntityInsertionAdapter<FuenteIngreso>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `fuentes_ingreso` (`id`,`nombre`,`tipo`,`descripcion`,`monto`,`moneda`,`diaIngreso`,`fechaPostergada`,`notificarCobro`,`activa`,`fechaCreacion`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FuenteIngreso entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNombre());
        statement.bindString(3, entity.getTipo());
        statement.bindString(4, entity.getDescripcion());
        statement.bindDouble(5, entity.getMonto());
        statement.bindString(6, entity.getMoneda());
        if (entity.getDiaIngreso() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getDiaIngreso());
        }
        if (entity.getFechaPostergada() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getFechaPostergada());
        }
        final int _tmp = entity.getNotificarCobro() ? 1 : 0;
        statement.bindLong(9, _tmp);
        final int _tmp_1 = entity.getActiva() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        statement.bindLong(11, entity.getFechaCreacion());
      }
    };
    this.__deletionAdapterOfFuenteIngreso = new EntityDeletionOrUpdateAdapter<FuenteIngreso>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `fuentes_ingreso` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FuenteIngreso entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFuenteIngreso = new EntityDeletionOrUpdateAdapter<FuenteIngreso>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `fuentes_ingreso` SET `id` = ?,`nombre` = ?,`tipo` = ?,`descripcion` = ?,`monto` = ?,`moneda` = ?,`diaIngreso` = ?,`fechaPostergada` = ?,`notificarCobro` = ?,`activa` = ?,`fechaCreacion` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FuenteIngreso entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNombre());
        statement.bindString(3, entity.getTipo());
        statement.bindString(4, entity.getDescripcion());
        statement.bindDouble(5, entity.getMonto());
        statement.bindString(6, entity.getMoneda());
        if (entity.getDiaIngreso() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getDiaIngreso());
        }
        if (entity.getFechaPostergada() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getFechaPostergada());
        }
        final int _tmp = entity.getNotificarCobro() ? 1 : 0;
        statement.bindLong(9, _tmp);
        final int _tmp_1 = entity.getActiva() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        statement.bindLong(11, entity.getFechaCreacion());
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM fuentes_ingreso";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final FuenteIngreso fuenteIngreso,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFuenteIngreso.insertAndReturnId(fuenteIngreso);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final FuenteIngreso fuenteIngreso,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFuenteIngreso.handle(fuenteIngreso);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final FuenteIngreso fuenteIngreso,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFuenteIngreso.handle(fuenteIngreso);
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
  public Flow<List<FuenteIngreso>> getAll() {
    final String _sql = "SELECT * FROM fuentes_ingreso ORDER BY fechaCreacion DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"fuentes_ingreso"}, new Callable<List<FuenteIngreso>>() {
      @Override
      @NonNull
      public List<FuenteIngreso> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfDiaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "diaIngreso");
          final int _cursorIndexOfFechaPostergada = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaPostergada");
          final int _cursorIndexOfNotificarCobro = CursorUtil.getColumnIndexOrThrow(_cursor, "notificarCobro");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final List<FuenteIngreso> _result = new ArrayList<FuenteIngreso>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FuenteIngreso _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final Integer _tmpDiaIngreso;
            if (_cursor.isNull(_cursorIndexOfDiaIngreso)) {
              _tmpDiaIngreso = null;
            } else {
              _tmpDiaIngreso = _cursor.getInt(_cursorIndexOfDiaIngreso);
            }
            final Long _tmpFechaPostergada;
            if (_cursor.isNull(_cursorIndexOfFechaPostergada)) {
              _tmpFechaPostergada = null;
            } else {
              _tmpFechaPostergada = _cursor.getLong(_cursorIndexOfFechaPostergada);
            }
            final boolean _tmpNotificarCobro;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfNotificarCobro);
            _tmpNotificarCobro = _tmp != 0;
            final boolean _tmpActiva;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp_1 != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            _item = new FuenteIngreso(_tmpId,_tmpNombre,_tmpTipo,_tmpDescripcion,_tmpMonto,_tmpMoneda,_tmpDiaIngreso,_tmpFechaPostergada,_tmpNotificarCobro,_tmpActiva,_tmpFechaCreacion);
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
  public Flow<List<FuenteIngreso>> getActivas() {
    final String _sql = "SELECT * FROM fuentes_ingreso WHERE activa = 1 ORDER BY nombre ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"fuentes_ingreso"}, new Callable<List<FuenteIngreso>>() {
      @Override
      @NonNull
      public List<FuenteIngreso> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfDiaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "diaIngreso");
          final int _cursorIndexOfFechaPostergada = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaPostergada");
          final int _cursorIndexOfNotificarCobro = CursorUtil.getColumnIndexOrThrow(_cursor, "notificarCobro");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final List<FuenteIngreso> _result = new ArrayList<FuenteIngreso>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FuenteIngreso _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final Integer _tmpDiaIngreso;
            if (_cursor.isNull(_cursorIndexOfDiaIngreso)) {
              _tmpDiaIngreso = null;
            } else {
              _tmpDiaIngreso = _cursor.getInt(_cursorIndexOfDiaIngreso);
            }
            final Long _tmpFechaPostergada;
            if (_cursor.isNull(_cursorIndexOfFechaPostergada)) {
              _tmpFechaPostergada = null;
            } else {
              _tmpFechaPostergada = _cursor.getLong(_cursorIndexOfFechaPostergada);
            }
            final boolean _tmpNotificarCobro;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfNotificarCobro);
            _tmpNotificarCobro = _tmp != 0;
            final boolean _tmpActiva;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp_1 != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            _item = new FuenteIngreso(_tmpId,_tmpNombre,_tmpTipo,_tmpDescripcion,_tmpMonto,_tmpMoneda,_tmpDiaIngreso,_tmpFechaPostergada,_tmpNotificarCobro,_tmpActiva,_tmpFechaCreacion);
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
  public Flow<List<FuenteIngreso>> getTrabajos() {
    final String _sql = "SELECT * FROM fuentes_ingreso WHERE tipo = 'TRABAJO' AND activa = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"fuentes_ingreso"}, new Callable<List<FuenteIngreso>>() {
      @Override
      @NonNull
      public List<FuenteIngreso> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfDiaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "diaIngreso");
          final int _cursorIndexOfFechaPostergada = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaPostergada");
          final int _cursorIndexOfNotificarCobro = CursorUtil.getColumnIndexOrThrow(_cursor, "notificarCobro");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final List<FuenteIngreso> _result = new ArrayList<FuenteIngreso>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FuenteIngreso _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final Integer _tmpDiaIngreso;
            if (_cursor.isNull(_cursorIndexOfDiaIngreso)) {
              _tmpDiaIngreso = null;
            } else {
              _tmpDiaIngreso = _cursor.getInt(_cursorIndexOfDiaIngreso);
            }
            final Long _tmpFechaPostergada;
            if (_cursor.isNull(_cursorIndexOfFechaPostergada)) {
              _tmpFechaPostergada = null;
            } else {
              _tmpFechaPostergada = _cursor.getLong(_cursorIndexOfFechaPostergada);
            }
            final boolean _tmpNotificarCobro;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfNotificarCobro);
            _tmpNotificarCobro = _tmp != 0;
            final boolean _tmpActiva;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp_1 != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            _item = new FuenteIngreso(_tmpId,_tmpNombre,_tmpTipo,_tmpDescripcion,_tmpMonto,_tmpMoneda,_tmpDiaIngreso,_tmpFechaPostergada,_tmpNotificarCobro,_tmpActiva,_tmpFechaCreacion);
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
  public Object getById(final long id, final Continuation<? super FuenteIngreso> $completion) {
    final String _sql = "SELECT * FROM fuentes_ingreso WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FuenteIngreso>() {
      @Override
      @Nullable
      public FuenteIngreso call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfMonto = CursorUtil.getColumnIndexOrThrow(_cursor, "monto");
          final int _cursorIndexOfMoneda = CursorUtil.getColumnIndexOrThrow(_cursor, "moneda");
          final int _cursorIndexOfDiaIngreso = CursorUtil.getColumnIndexOrThrow(_cursor, "diaIngreso");
          final int _cursorIndexOfFechaPostergada = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaPostergada");
          final int _cursorIndexOfNotificarCobro = CursorUtil.getColumnIndexOrThrow(_cursor, "notificarCobro");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacion");
          final FuenteIngreso _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final double _tmpMonto;
            _tmpMonto = _cursor.getDouble(_cursorIndexOfMonto);
            final String _tmpMoneda;
            _tmpMoneda = _cursor.getString(_cursorIndexOfMoneda);
            final Integer _tmpDiaIngreso;
            if (_cursor.isNull(_cursorIndexOfDiaIngreso)) {
              _tmpDiaIngreso = null;
            } else {
              _tmpDiaIngreso = _cursor.getInt(_cursorIndexOfDiaIngreso);
            }
            final Long _tmpFechaPostergada;
            if (_cursor.isNull(_cursorIndexOfFechaPostergada)) {
              _tmpFechaPostergada = null;
            } else {
              _tmpFechaPostergada = _cursor.getLong(_cursorIndexOfFechaPostergada);
            }
            final boolean _tmpNotificarCobro;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfNotificarCobro);
            _tmpNotificarCobro = _tmp != 0;
            final boolean _tmpActiva;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp_1 != 0;
            final long _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getLong(_cursorIndexOfFechaCreacion);
            _result = new FuenteIngreso(_tmpId,_tmpNombre,_tmpTipo,_tmpDescripcion,_tmpMonto,_tmpMoneda,_tmpDiaIngreso,_tmpFechaPostergada,_tmpNotificarCobro,_tmpActiva,_tmpFechaCreacion);
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
