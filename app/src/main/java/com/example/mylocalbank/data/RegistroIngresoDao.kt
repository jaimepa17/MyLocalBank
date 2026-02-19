package com.example.mylocalbank.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroIngresoDao {

    @Query("SELECT * FROM registros_ingresos ORDER BY fecha DESC")
    fun getAll(): Flow<List<RegistroIngreso>>

    @Query("SELECT * FROM registros_ingresos WHERE fecha BETWEEN :start AND :end ORDER BY fecha DESC")
    fun getByDateRange(start: Long, end: Long): Flow<List<RegistroIngreso>>

    @Query("SELECT * FROM registros_ingresos WHERE fuenteId = :sourceId AND fecha BETWEEN :start AND :end LIMIT 1")
    suspend fun getPaymentForSource(sourceId: Long, start: Long, end: Long): RegistroIngreso?

    @Insert
    suspend fun insert(registro: RegistroIngreso): Long

    @Update
    suspend fun update(registro: RegistroIngreso)

    @Delete
    suspend fun delete(registro: RegistroIngreso)

    @Query("DELETE FROM registros_ingresos")
    suspend fun deleteAll()
}
