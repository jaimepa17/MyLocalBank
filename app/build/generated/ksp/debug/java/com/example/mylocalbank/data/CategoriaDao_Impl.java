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
public final class CategoriaDao_Impl implements CategoriaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Categoria> __insertionAdapterOfCategoria;

  private final EntityDeletionOrUpdateAdapter<Categoria> __deletionAdapterOfCategoria;

  private final EntityDeletionOrUpdateAdapter<Categoria> __updateAdapterOfCategoria;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public CategoriaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCategoria = new EntityInsertionAdapter<Categoria>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `categorias` (`id`,`nombre`,`icono`,`esDefault`,`activa`,`tipo`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Categoria entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNombre());
        statement.bindString(3, entity.getIcono());
        final int _tmp = entity.getEsDefault() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.getActiva() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        statement.bindString(6, entity.getTipo());
      }
    };
    this.__deletionAdapterOfCategoria = new EntityDeletionOrUpdateAdapter<Categoria>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `categorias` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Categoria entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfCategoria = new EntityDeletionOrUpdateAdapter<Categoria>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `categorias` SET `id` = ?,`nombre` = ?,`icono` = ?,`esDefault` = ?,`activa` = ?,`tipo` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Categoria entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNombre());
        statement.bindString(3, entity.getIcono());
        final int _tmp = entity.getEsDefault() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.getActiva() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        statement.bindString(6, entity.getTipo());
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM categorias";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Categoria categoria, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCategoria.insert(categoria);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Categoria categoria, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCategoria.handle(categoria);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Categoria categoria, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCategoria.handle(categoria);
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
  public Flow<List<Categoria>> getAll() {
    final String _sql = "SELECT * FROM categorias WHERE activa = 1 ORDER BY esDefault DESC, nombre ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"categorias"}, new Callable<List<Categoria>>() {
      @Override
      @NonNull
      public List<Categoria> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfIcono = CursorUtil.getColumnIndexOrThrow(_cursor, "icono");
          final int _cursorIndexOfEsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "esDefault");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final List<Categoria> _result = new ArrayList<Categoria>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Categoria _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpIcono;
            _tmpIcono = _cursor.getString(_cursorIndexOfIcono);
            final boolean _tmpEsDefault;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEsDefault);
            _tmpEsDefault = _tmp != 0;
            final boolean _tmpActiva;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp_1 != 0;
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            _item = new Categoria(_tmpId,_tmpNombre,_tmpIcono,_tmpEsDefault,_tmpActiva,_tmpTipo);
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
  public List<Categoria> getAllSync() {
    final String _sql = "SELECT * FROM categorias";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
      final int _cursorIndexOfIcono = CursorUtil.getColumnIndexOrThrow(_cursor, "icono");
      final int _cursorIndexOfEsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "esDefault");
      final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
      final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
      final List<Categoria> _result = new ArrayList<Categoria>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Categoria _item;
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        final String _tmpNombre;
        _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
        final String _tmpIcono;
        _tmpIcono = _cursor.getString(_cursorIndexOfIcono);
        final boolean _tmpEsDefault;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfEsDefault);
        _tmpEsDefault = _tmp != 0;
        final boolean _tmpActiva;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfActiva);
        _tmpActiva = _tmp_1 != 0;
        final String _tmpTipo;
        _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
        _item = new Categoria(_tmpId,_tmpNombre,_tmpIcono,_tmpEsDefault,_tmpActiva,_tmpTipo);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Object getById(final long id, final Continuation<? super Categoria> $completion) {
    final String _sql = "SELECT * FROM categorias WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Categoria>() {
      @Override
      @Nullable
      public Categoria call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfIcono = CursorUtil.getColumnIndexOrThrow(_cursor, "icono");
          final int _cursorIndexOfEsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "esDefault");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final Categoria _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpIcono;
            _tmpIcono = _cursor.getString(_cursorIndexOfIcono);
            final boolean _tmpEsDefault;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEsDefault);
            _tmpEsDefault = _tmp != 0;
            final boolean _tmpActiva;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp_1 != 0;
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            _result = new Categoria(_tmpId,_tmpNombre,_tmpIcono,_tmpEsDefault,_tmpActiva,_tmpTipo);
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
  public Object getCountByName(final String nombre,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM categorias WHERE nombre = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, nombre);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Flow<List<Categoria>> getAllForBackup() {
    final String _sql = "SELECT * FROM categorias";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"categorias"}, new Callable<List<Categoria>>() {
      @Override
      @NonNull
      public List<Categoria> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfIcono = CursorUtil.getColumnIndexOrThrow(_cursor, "icono");
          final int _cursorIndexOfEsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "esDefault");
          final int _cursorIndexOfActiva = CursorUtil.getColumnIndexOrThrow(_cursor, "activa");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final List<Categoria> _result = new ArrayList<Categoria>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Categoria _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpIcono;
            _tmpIcono = _cursor.getString(_cursorIndexOfIcono);
            final boolean _tmpEsDefault;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEsDefault);
            _tmpEsDefault = _tmp != 0;
            final boolean _tmpActiva;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfActiva);
            _tmpActiva = _tmp_1 != 0;
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            _item = new Categoria(_tmpId,_tmpNombre,_tmpIcono,_tmpEsDefault,_tmpActiva,_tmpTipo);
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
