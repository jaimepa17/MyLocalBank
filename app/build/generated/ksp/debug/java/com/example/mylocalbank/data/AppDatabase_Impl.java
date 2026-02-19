package com.example.mylocalbank.data;

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
  private volatile TarjetaDao _tarjetaDao;

  private volatile FuenteIngresoDao _fuenteIngresoDao;

  private volatile GastoFijoDao _gastoFijoDao;

  private volatile RegistroGastoDao _registroGastoDao;

  private volatile RegistroIngresoDao _registroIngresoDao;

  private volatile CategoriaDao _categoriaDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(13) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `tarjetas` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `alias` TEXT NOT NULL, `ultimos4` TEXT NOT NULL, `tipo` TEXT NOT NULL, `banco` TEXT NOT NULL, `color` TEXT NOT NULL, `activa` INTEGER NOT NULL, `fechaCreacion` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `fuentes_ingreso` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL, `tipo` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `monto` REAL NOT NULL, `moneda` TEXT NOT NULL, `diaIngreso` INTEGER, `fechaPostergada` INTEGER, `notificarCobro` INTEGER NOT NULL, `activa` INTEGER NOT NULL, `fechaCreacion` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `gastos_fijos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL, `monto` REAL NOT NULL, `moneda` TEXT NOT NULL, `tarjetaId` INTEGER, `diaCobro` INTEGER NOT NULL, `activa` INTEGER NOT NULL, `fechaCreacion` INTEGER NOT NULL, `categoriaId` INTEGER NOT NULL, FOREIGN KEY(`tarjetaId`) REFERENCES `tarjetas`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_gastos_fijos_tarjetaId` ON `gastos_fijos` (`tarjetaId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_gastos_fijos_categoriaId` ON `gastos_fijos` (`categoriaId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `registros_gastos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `monto` REAL NOT NULL, `moneda` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `tienda` TEXT NOT NULL, `tarjetaId` INTEGER, `fecha` INTEGER NOT NULL, `categoriaId` INTEGER NOT NULL, FOREIGN KEY(`tarjetaId`) REFERENCES `tarjetas`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_registros_gastos_tarjetaId` ON `registros_gastos` (`tarjetaId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_registros_gastos_categoriaId` ON `registros_gastos` (`categoriaId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `registros_ingresos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `monto` REAL NOT NULL, `moneda` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `fecha` INTEGER NOT NULL, `categoriaId` INTEGER NOT NULL, `fuenteId` INTEGER, FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_registros_ingresos_categoriaId` ON `registros_ingresos` (`categoriaId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `categorias` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL, `icono` TEXT NOT NULL, `esDefault` INTEGER NOT NULL, `activa` INTEGER NOT NULL, `tipo` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4391e88cb6159d8f4ac4d2e85b9fc22f')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `tarjetas`");
        db.execSQL("DROP TABLE IF EXISTS `fuentes_ingreso`");
        db.execSQL("DROP TABLE IF EXISTS `gastos_fijos`");
        db.execSQL("DROP TABLE IF EXISTS `registros_gastos`");
        db.execSQL("DROP TABLE IF EXISTS `registros_ingresos`");
        db.execSQL("DROP TABLE IF EXISTS `categorias`");
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
        final HashMap<String, TableInfo.Column> _columnsTarjetas = new HashMap<String, TableInfo.Column>(8);
        _columnsTarjetas.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTarjetas.put("alias", new TableInfo.Column("alias", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTarjetas.put("ultimos4", new TableInfo.Column("ultimos4", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTarjetas.put("tipo", new TableInfo.Column("tipo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTarjetas.put("banco", new TableInfo.Column("banco", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTarjetas.put("color", new TableInfo.Column("color", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTarjetas.put("activa", new TableInfo.Column("activa", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTarjetas.put("fechaCreacion", new TableInfo.Column("fechaCreacion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTarjetas = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTarjetas = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTarjetas = new TableInfo("tarjetas", _columnsTarjetas, _foreignKeysTarjetas, _indicesTarjetas);
        final TableInfo _existingTarjetas = TableInfo.read(db, "tarjetas");
        if (!_infoTarjetas.equals(_existingTarjetas)) {
          return new RoomOpenHelper.ValidationResult(false, "tarjetas(com.example.mylocalbank.data.Tarjeta).\n"
                  + " Expected:\n" + _infoTarjetas + "\n"
                  + " Found:\n" + _existingTarjetas);
        }
        final HashMap<String, TableInfo.Column> _columnsFuentesIngreso = new HashMap<String, TableInfo.Column>(11);
        _columnsFuentesIngreso.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuentesIngreso.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuentesIngreso.put("tipo", new TableInfo.Column("tipo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuentesIngreso.put("descripcion", new TableInfo.Column("descripcion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuentesIngreso.put("monto", new TableInfo.Column("monto", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuentesIngreso.put("moneda", new TableInfo.Column("moneda", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuentesIngreso.put("diaIngreso", new TableInfo.Column("diaIngreso", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuentesIngreso.put("fechaPostergada", new TableInfo.Column("fechaPostergada", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuentesIngreso.put("notificarCobro", new TableInfo.Column("notificarCobro", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuentesIngreso.put("activa", new TableInfo.Column("activa", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuentesIngreso.put("fechaCreacion", new TableInfo.Column("fechaCreacion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFuentesIngreso = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFuentesIngreso = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFuentesIngreso = new TableInfo("fuentes_ingreso", _columnsFuentesIngreso, _foreignKeysFuentesIngreso, _indicesFuentesIngreso);
        final TableInfo _existingFuentesIngreso = TableInfo.read(db, "fuentes_ingreso");
        if (!_infoFuentesIngreso.equals(_existingFuentesIngreso)) {
          return new RoomOpenHelper.ValidationResult(false, "fuentes_ingreso(com.example.mylocalbank.data.FuenteIngreso).\n"
                  + " Expected:\n" + _infoFuentesIngreso + "\n"
                  + " Found:\n" + _existingFuentesIngreso);
        }
        final HashMap<String, TableInfo.Column> _columnsGastosFijos = new HashMap<String, TableInfo.Column>(9);
        _columnsGastosFijos.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGastosFijos.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGastosFijos.put("monto", new TableInfo.Column("monto", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGastosFijos.put("moneda", new TableInfo.Column("moneda", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGastosFijos.put("tarjetaId", new TableInfo.Column("tarjetaId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGastosFijos.put("diaCobro", new TableInfo.Column("diaCobro", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGastosFijos.put("activa", new TableInfo.Column("activa", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGastosFijos.put("fechaCreacion", new TableInfo.Column("fechaCreacion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGastosFijos.put("categoriaId", new TableInfo.Column("categoriaId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGastosFijos = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysGastosFijos.add(new TableInfo.ForeignKey("tarjetas", "SET NULL", "NO ACTION", Arrays.asList("tarjetaId"), Arrays.asList("id")));
        _foreignKeysGastosFijos.add(new TableInfo.ForeignKey("categorias", "RESTRICT", "NO ACTION", Arrays.asList("categoriaId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesGastosFijos = new HashSet<TableInfo.Index>(2);
        _indicesGastosFijos.add(new TableInfo.Index("index_gastos_fijos_tarjetaId", false, Arrays.asList("tarjetaId"), Arrays.asList("ASC")));
        _indicesGastosFijos.add(new TableInfo.Index("index_gastos_fijos_categoriaId", false, Arrays.asList("categoriaId"), Arrays.asList("ASC")));
        final TableInfo _infoGastosFijos = new TableInfo("gastos_fijos", _columnsGastosFijos, _foreignKeysGastosFijos, _indicesGastosFijos);
        final TableInfo _existingGastosFijos = TableInfo.read(db, "gastos_fijos");
        if (!_infoGastosFijos.equals(_existingGastosFijos)) {
          return new RoomOpenHelper.ValidationResult(false, "gastos_fijos(com.example.mylocalbank.data.GastoFijo).\n"
                  + " Expected:\n" + _infoGastosFijos + "\n"
                  + " Found:\n" + _existingGastosFijos);
        }
        final HashMap<String, TableInfo.Column> _columnsRegistrosGastos = new HashMap<String, TableInfo.Column>(8);
        _columnsRegistrosGastos.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosGastos.put("monto", new TableInfo.Column("monto", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosGastos.put("moneda", new TableInfo.Column("moneda", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosGastos.put("descripcion", new TableInfo.Column("descripcion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosGastos.put("tienda", new TableInfo.Column("tienda", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosGastos.put("tarjetaId", new TableInfo.Column("tarjetaId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosGastos.put("fecha", new TableInfo.Column("fecha", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosGastos.put("categoriaId", new TableInfo.Column("categoriaId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRegistrosGastos = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysRegistrosGastos.add(new TableInfo.ForeignKey("tarjetas", "SET NULL", "NO ACTION", Arrays.asList("tarjetaId"), Arrays.asList("id")));
        _foreignKeysRegistrosGastos.add(new TableInfo.ForeignKey("categorias", "RESTRICT", "NO ACTION", Arrays.asList("categoriaId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRegistrosGastos = new HashSet<TableInfo.Index>(2);
        _indicesRegistrosGastos.add(new TableInfo.Index("index_registros_gastos_tarjetaId", false, Arrays.asList("tarjetaId"), Arrays.asList("ASC")));
        _indicesRegistrosGastos.add(new TableInfo.Index("index_registros_gastos_categoriaId", false, Arrays.asList("categoriaId"), Arrays.asList("ASC")));
        final TableInfo _infoRegistrosGastos = new TableInfo("registros_gastos", _columnsRegistrosGastos, _foreignKeysRegistrosGastos, _indicesRegistrosGastos);
        final TableInfo _existingRegistrosGastos = TableInfo.read(db, "registros_gastos");
        if (!_infoRegistrosGastos.equals(_existingRegistrosGastos)) {
          return new RoomOpenHelper.ValidationResult(false, "registros_gastos(com.example.mylocalbank.data.RegistroGasto).\n"
                  + " Expected:\n" + _infoRegistrosGastos + "\n"
                  + " Found:\n" + _existingRegistrosGastos);
        }
        final HashMap<String, TableInfo.Column> _columnsRegistrosIngresos = new HashMap<String, TableInfo.Column>(7);
        _columnsRegistrosIngresos.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosIngresos.put("monto", new TableInfo.Column("monto", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosIngresos.put("moneda", new TableInfo.Column("moneda", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosIngresos.put("descripcion", new TableInfo.Column("descripcion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosIngresos.put("fecha", new TableInfo.Column("fecha", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosIngresos.put("categoriaId", new TableInfo.Column("categoriaId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegistrosIngresos.put("fuenteId", new TableInfo.Column("fuenteId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRegistrosIngresos = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRegistrosIngresos.add(new TableInfo.ForeignKey("categorias", "RESTRICT", "NO ACTION", Arrays.asList("categoriaId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRegistrosIngresos = new HashSet<TableInfo.Index>(1);
        _indicesRegistrosIngresos.add(new TableInfo.Index("index_registros_ingresos_categoriaId", false, Arrays.asList("categoriaId"), Arrays.asList("ASC")));
        final TableInfo _infoRegistrosIngresos = new TableInfo("registros_ingresos", _columnsRegistrosIngresos, _foreignKeysRegistrosIngresos, _indicesRegistrosIngresos);
        final TableInfo _existingRegistrosIngresos = TableInfo.read(db, "registros_ingresos");
        if (!_infoRegistrosIngresos.equals(_existingRegistrosIngresos)) {
          return new RoomOpenHelper.ValidationResult(false, "registros_ingresos(com.example.mylocalbank.data.RegistroIngreso).\n"
                  + " Expected:\n" + _infoRegistrosIngresos + "\n"
                  + " Found:\n" + _existingRegistrosIngresos);
        }
        final HashMap<String, TableInfo.Column> _columnsCategorias = new HashMap<String, TableInfo.Column>(6);
        _columnsCategorias.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategorias.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategorias.put("icono", new TableInfo.Column("icono", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategorias.put("esDefault", new TableInfo.Column("esDefault", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategorias.put("activa", new TableInfo.Column("activa", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategorias.put("tipo", new TableInfo.Column("tipo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategorias = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCategorias = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCategorias = new TableInfo("categorias", _columnsCategorias, _foreignKeysCategorias, _indicesCategorias);
        final TableInfo _existingCategorias = TableInfo.read(db, "categorias");
        if (!_infoCategorias.equals(_existingCategorias)) {
          return new RoomOpenHelper.ValidationResult(false, "categorias(com.example.mylocalbank.data.Categoria).\n"
                  + " Expected:\n" + _infoCategorias + "\n"
                  + " Found:\n" + _existingCategorias);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4391e88cb6159d8f4ac4d2e85b9fc22f", "31d67d76d25a972891775e88bcc1ac60");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "tarjetas","fuentes_ingreso","gastos_fijos","registros_gastos","registros_ingresos","categorias");
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
      _db.execSQL("DELETE FROM `tarjetas`");
      _db.execSQL("DELETE FROM `fuentes_ingreso`");
      _db.execSQL("DELETE FROM `gastos_fijos`");
      _db.execSQL("DELETE FROM `registros_gastos`");
      _db.execSQL("DELETE FROM `registros_ingresos`");
      _db.execSQL("DELETE FROM `categorias`");
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
    _typeConvertersMap.put(TarjetaDao.class, TarjetaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FuenteIngresoDao.class, FuenteIngresoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GastoFijoDao.class, GastoFijoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RegistroGastoDao.class, RegistroGastoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RegistroIngresoDao.class, RegistroIngresoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CategoriaDao.class, CategoriaDao_Impl.getRequiredConverters());
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
  public TarjetaDao tarjetaDao() {
    if (_tarjetaDao != null) {
      return _tarjetaDao;
    } else {
      synchronized(this) {
        if(_tarjetaDao == null) {
          _tarjetaDao = new TarjetaDao_Impl(this);
        }
        return _tarjetaDao;
      }
    }
  }

  @Override
  public FuenteIngresoDao fuenteIngresoDao() {
    if (_fuenteIngresoDao != null) {
      return _fuenteIngresoDao;
    } else {
      synchronized(this) {
        if(_fuenteIngresoDao == null) {
          _fuenteIngresoDao = new FuenteIngresoDao_Impl(this);
        }
        return _fuenteIngresoDao;
      }
    }
  }

  @Override
  public GastoFijoDao gastoFijoDao() {
    if (_gastoFijoDao != null) {
      return _gastoFijoDao;
    } else {
      synchronized(this) {
        if(_gastoFijoDao == null) {
          _gastoFijoDao = new GastoFijoDao_Impl(this);
        }
        return _gastoFijoDao;
      }
    }
  }

  @Override
  public RegistroGastoDao registroGastoDao() {
    if (_registroGastoDao != null) {
      return _registroGastoDao;
    } else {
      synchronized(this) {
        if(_registroGastoDao == null) {
          _registroGastoDao = new RegistroGastoDao_Impl(this);
        }
        return _registroGastoDao;
      }
    }
  }

  @Override
  public RegistroIngresoDao registroIngresoDao() {
    if (_registroIngresoDao != null) {
      return _registroIngresoDao;
    } else {
      synchronized(this) {
        if(_registroIngresoDao == null) {
          _registroIngresoDao = new RegistroIngresoDao_Impl(this);
        }
        return _registroIngresoDao;
      }
    }
  }

  @Override
  public CategoriaDao categoriaDao() {
    if (_categoriaDao != null) {
      return _categoriaDao;
    } else {
      synchronized(this) {
        if(_categoriaDao == null) {
          _categoriaDao = new CategoriaDao_Impl(this);
        }
        return _categoriaDao;
      }
    }
  }
}
