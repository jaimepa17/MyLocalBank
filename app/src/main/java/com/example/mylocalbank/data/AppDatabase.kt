package com.example.mylocalbank.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
        entities =
                [
                        Tarjeta::class,
                        FuenteIngreso::class,
                        GastoFijo::class,
                        RegistroGasto::class,
                        RegistroIngreso::class,
                        // ...
                        Categoria::class,
                        SaldoInicial::class],
        version = 17,
        exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

        abstract fun tarjetaDao(): TarjetaDao
        abstract fun fuenteIngresoDao(): FuenteIngresoDao
        abstract fun gastoFijoDao(): GastoFijoDao
        abstract fun registroGastoDao(): RegistroGastoDao
        abstract fun registroIngresoDao(): RegistroIngresoDao
        abstract fun categoriaDao(): CategoriaDao
        abstract fun saldoInicialDao(): SaldoInicialDao

        companion object {
                val MIGRATION_7_8 =
                        object : androidx.room.migration.Migration(7, 8) {
                                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                                        // ... (existing code, compacted for brevity in replacement
                                        // if needed, but
                                        // better to keep target strict)
                                        // 1. Create Categorias table
                                        db.execSQL(
                                                "CREATE TABLE IF NOT EXISTS `categorias` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL, `icono` TEXT NOT NULL, `esDefault` INTEGER NOT NULL, `activa` INTEGER NOT NULL)"
                                        )

                                        // 2. Insert Default 'Otros'
                                        db.execSQL(
                                                "INSERT INTO categorias (id, nombre, icono, esDefault, activa) VALUES (1, 'Otros', '📦', 1, 1)"
                                        )

                                        // 3. Migrate RegistroGasto (Add FK)
                                        // Rename old table
                                        db.execSQL(
                                                "ALTER TABLE `registros_gastos` RENAME TO `registros_gastos_old`"
                                        )
                                        // Create new table
                                        db.execSQL(
                                                "CREATE TABLE IF NOT EXISTS `registros_gastos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `monto` REAL NOT NULL, `moneda` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `tienda` TEXT NOT NULL, `tarjetaId` INTEGER, `fecha` INTEGER NOT NULL, `categoriaId` INTEGER NOT NULL DEFAULT 1, FOREIGN KEY(`tarjetaId`) REFERENCES `tarjetas`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL, FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT)"
                                        )
                                        // Copy data
                                        db.execSQL(
                                                "INSERT INTO `registros_gastos` (id, monto, moneda, descripcion, tienda, tarjetaId, fecha, categoriaId) SELECT id, monto, moneda, descripcion, tienda, tarjetaId, fecha, 1 FROM `registros_gastos_old`"
                                        )
                                        // Drop old
                                        db.execSQL("DROP TABLE `registros_gastos_old`")

                                        // 4. Migrate GastoFijo (Add FK)
                                        db.execSQL(
                                                "ALTER TABLE `gastos_fijos` RENAME TO `gastos_fijos_old`"
                                        )
                                        // Note: GastoFijo has active (boolean) -> INTEGER
                                        db.execSQL(
                                                "CREATE TABLE IF NOT EXISTS `gastos_fijos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL, `monto` REAL NOT NULL, `moneda` TEXT NOT NULL, `tarjetaId` INTEGER, `diaCobro` INTEGER NOT NULL, `activa` INTEGER NOT NULL, `fechaCreacion` INTEGER NOT NULL, `categoriaId` INTEGER NOT NULL DEFAULT 1, FOREIGN KEY(`tarjetaId`) REFERENCES `tarjetas`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL, FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT)"
                                        )
                                        db.execSQL(
                                                "INSERT INTO `gastos_fijos` (id, nombre, monto, moneda, tarjetaId, diaCobro, activa, fechaCreacion, categoriaId) SELECT id, nombre, monto, moneda, tarjetaId, diaCobro, activa, fechaCreacion, 1 FROM `gastos_fijos_old`"
                                        )
                                        db.execSQL("DROP TABLE `gastos_fijos_old`")
                                }
                        }

                val MIGRATION_8_9 =
                        object : androidx.room.migration.Migration(8, 9) {
                                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                                        // 1. Add 'tipo' column to 'categorias'
                                        db.execSQL(
                                                "ALTER TABLE `categorias` ADD COLUMN `tipo` TEXT NOT NULL DEFAULT 'AMBOS'"
                                        )

                                        // 2. Migrate RegistroIngreso (Add FK)
                                        db.execSQL(
                                                "ALTER TABLE `registros_ingresos` RENAME TO `registros_ingresos_old`"
                                        )
                                        db.execSQL(
                                                "CREATE TABLE IF NOT EXISTS `registros_ingresos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `monto` REAL NOT NULL, `moneda` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `fecha` INTEGER NOT NULL, `categoriaId` INTEGER NOT NULL DEFAULT 1, FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT)"
                                        )
                                        db.execSQL(
                                                "INSERT INTO `registros_ingresos` (id, monto, moneda, descripcion, fecha, categoriaId) SELECT id, monto, moneda, descripcion, fecha, 1 FROM `registros_ingresos_old`"
                                        )
                                        db.execSQL("DROP TABLE `registros_ingresos_old`")
                                }
                        }

                val MIGRATION_9_10 =
                        object : androidx.room.migration.Migration(9, 10) {
                                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                                        db.execSQL(
                                                "INSERT INTO categorias (nombre, icono, esDefault, activa, tipo) VALUES ('Gastos Rápidos', '⚡', 0, 1, 'GASTO')"
                                        )
                                }
                        }

                val MIGRATION_10_11 =
                        object : androidx.room.migration.Migration(10, 11) {
                                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                                        db.execSQL(
                                                "INSERT INTO categorias (nombre, icono, esDefault, activa, tipo) VALUES ('Ingresos Rápidos', '⚡', 0, 1, 'INGRESO')"
                                        )
                                }
                        }

                val MIGRATION_11_12 =
                        object : androidx.room.migration.Migration(11, 12) {
                                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                                        // 1. Add fechaPostergada to fuentes_ingreso
                                        db.execSQL(
                                                "ALTER TABLE `fuentes_ingreso` ADD COLUMN `fechaPostergada` INTEGER DEFAULT NULL"
                                        )

                                        // 2. Add fuenteId to registros_ingresos
                                        // SQLite doesn't support adding FK columns easily with
                                        // ALTER TABLE in older
                                        // versions reliably for all constraints,
                                        // but simpler ADD COLUMN works for nullable fields without
                                        // strict
                                        // constraints immediately or if we accept no strict FK
                                        // enforcement on this
                                        // column for legacy data
                                        // To be safe and consistent with previous migrations
                                        // (re-creating table for
                                        // FKs):

                                        db.execSQL(
                                                "ALTER TABLE `registros_ingresos` RENAME TO `registros_ingresos_old`"
                                        )
                                        db.execSQL(
                                                "CREATE TABLE IF NOT EXISTS `registros_ingresos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `monto` REAL NOT NULL, `moneda` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `fecha` INTEGER NOT NULL, `categoriaId` INTEGER NOT NULL DEFAULT 1, `fuenteId` INTEGER DEFAULT NULL, FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT, FOREIGN KEY(`fuenteId`) REFERENCES `fuentes_ingreso`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL)"
                                        )
                                        db.execSQL(
                                                "INSERT INTO `registros_ingresos` (id, monto, moneda, descripcion, fecha, categoriaId, fuenteId) SELECT id, monto, moneda, descripcion, fecha, categoriaId, NULL FROM `registros_ingresos_old`"
                                        )
                                        db.execSQL("DROP TABLE `registros_ingresos_old`")
                                }
                        }

                val MIGRATION_12_13 =
                        object : androidx.room.migration.Migration(12, 13) {
                                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                                        db.execSQL(
                                                "ALTER TABLE `fuentes_ingreso` ADD COLUMN `notificarCobro` INTEGER NOT NULL DEFAULT 1"
                                        )
                                }
                        }

                val MIGRATION_13_14 =
                        object : androidx.room.migration.Migration(13, 14) {
                                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                                        db.execSQL(
                                                "CREATE TABLE IF NOT EXISTS `saldo_inicial` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `monto` REAL NOT NULL, `mesApertura` INTEGER NOT NULL, `anioApertura` INTEGER NOT NULL)"
                                        )
                                }
                        }

                val MIGRATION_14_15 =
                        object : androidx.room.migration.Migration(14, 15) {
                                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                                        // Insertar la categoría especial "Apertura"
                                        db.execSQL(
                                                "INSERT INTO categorias (nombre, icono, esDefault, activa, tipo) VALUES ('Apertura', '💵', 0, 0, 'AMBOS')"
                                        )
                                }
                        }

                val MIGRATION_15_16 =
                        object : androidx.room.migration.Migration(15, 16) {
                                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                                        db.execSQL(
                                                "ALTER TABLE `registros_gastos` RENAME TO `registros_gastos_old`"
                                        )
                                        db.execSQL(
                                                "CREATE TABLE IF NOT EXISTS `registros_gastos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `monto` REAL NOT NULL, `moneda` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `tienda` TEXT NOT NULL, `tarjetaId` INTEGER, `fecha` INTEGER NOT NULL, `categoriaId` INTEGER NOT NULL DEFAULT 1, `gastoFijoId` INTEGER DEFAULT NULL, FOREIGN KEY(`tarjetaId`) REFERENCES `tarjetas`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL, FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT, FOREIGN KEY(`gastoFijoId`) REFERENCES `gastos_fijos`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL)"
                                        )
                                        db.execSQL(
                                                "CREATE INDEX IF NOT EXISTS `index_registros_gastos_tarjetaId` ON `registros_gastos` (`tarjetaId`)"
                                        )
                                        db.execSQL(
                                                "CREATE INDEX IF NOT EXISTS `index_registros_gastos_categoriaId` ON `registros_gastos` (`categoriaId`)"
                                        )
                                        db.execSQL(
                                                "CREATE INDEX IF NOT EXISTS `index_registros_gastos_gastoFijoId` ON `registros_gastos` (`gastoFijoId`)"
                                        )
                                        db.execSQL(
                                                "INSERT INTO `registros_gastos` (id, monto, moneda, descripcion, tienda, tarjetaId, fecha, categoriaId, gastoFijoId) SELECT id, monto, moneda, descripcion, tienda, tarjetaId, fecha, categoriaId, NULL FROM `registros_gastos_old`"
                                        )
                                        db.execSQL("DROP TABLE `registros_gastos_old`")
                                }
                        }

                val MIGRATION_16_17 =
                        object : androidx.room.migration.Migration(16, 17) {
                                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                                        db.execSQL(
                                                "ALTER TABLE `registros_gastos` RENAME TO `registros_gastos_old_16`"
                                        )
                                        db.execSQL(
                                                "CREATE TABLE IF NOT EXISTS `registros_gastos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `monto` REAL NOT NULL, `moneda` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `tienda` TEXT NOT NULL, `tarjetaId` INTEGER, `fecha` INTEGER NOT NULL, `categoriaId` INTEGER NOT NULL, `gastoFijoId` INTEGER, FOREIGN KEY(`tarjetaId`) REFERENCES `tarjetas`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL, FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT, FOREIGN KEY(`gastoFijoId`) REFERENCES `gastos_fijos`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL)"
                                        )
                                        db.execSQL(
                                                "CREATE INDEX IF NOT EXISTS `index_registros_gastos_tarjetaId` ON `registros_gastos` (`tarjetaId`)"
                                        )
                                        db.execSQL(
                                                "CREATE INDEX IF NOT EXISTS `index_registros_gastos_categoriaId` ON `registros_gastos` (`categoriaId`)"
                                        )
                                        db.execSQL(
                                                "CREATE INDEX IF NOT EXISTS `index_registros_gastos_gastoFijoId` ON `registros_gastos` (`gastoFijoId`)"
                                        )
                                        db.execSQL(
                                                "INSERT INTO `registros_gastos` (id, monto, moneda, descripcion, tienda, tarjetaId, fecha, categoriaId, gastoFijoId) SELECT id, monto, moneda, descripcion, tienda, tarjetaId, fecha, categoriaId, gastoFijoId FROM `registros_gastos_old_16`"
                                        )
                                        db.execSQL("DROP TABLE `registros_gastos_old_16`")
                                }
                        }

                @Volatile private var INSTANCE: AppDatabase? = null

                fun getDatabase(context: Context): AppDatabase {
                        return INSTANCE
                                ?: synchronized(this) {
                                        val instance =
                                                Room.databaseBuilder(
                                                                context.applicationContext,
                                                                AppDatabase::class.java,
                                                                "finanzas_db"
                                                        )
                                                        .addMigrations(
                                                                MIGRATION_7_8,
                                                                MIGRATION_8_9,
                                                                MIGRATION_9_10,
                                                                MIGRATION_10_11,
                                                                MIGRATION_11_12,
                                                                MIGRATION_12_13,
                                                                MIGRATION_13_14,
                                                                MIGRATION_14_15,
                                                                MIGRATION_15_16,
                                                                MIGRATION_16_17
                                                        )
                                                        .build()
                                        INSTANCE = instance
                                        instance
                                }
                }
        }
}
