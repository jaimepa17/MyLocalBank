package com.example.mylocalbank.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoFijoDao {

    @Query("SELECT * FROM gastos_fijos ORDER BY fechaCreacion DESC")
    fun getAll(): Flow<List<GastoFijo>>

    @Query("SELECT * FROM gastos_fijos WHERE activa = 1 ORDER BY diaCobro ASC")
    fun getActivos(): Flow<List<GastoFijo>>

    @Query("SELECT * FROM gastos_fijos WHERE activa = 1 AND diaCobro <= :maxDay ORDER BY diaCobro ASC")
    fun getActivosCobradosHasta(maxDay: Int): Flow<List<GastoFijo>>

    @Query("SELECT * FROM gastos_fijos WHERE id = :id")
    suspend fun getById(id: Long): GastoFijo?

    @Insert
    suspend fun insert(gastoFijo: GastoFijo): Long

    @Update
    suspend fun update(gastoFijo: GastoFijo)

    @Delete
    suspend fun delete(gastoFijo: GastoFijo)

    @Query("DELETE FROM gastos_fijos")
    suspend fun deleteAll()
}
