package com.example.mylocalbank.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroGastoDao {

    @Query("SELECT * FROM registros_gastos ORDER BY fecha DESC")
    fun getAll(): Flow<List<RegistroGasto>>

    @Query("SELECT * FROM registros_gastos WHERE fecha BETWEEN :start AND :end ORDER BY fecha DESC")
    fun getByDateRange(start: Long, end: Long): Flow<List<RegistroGasto>>

    @Insert
    suspend fun insert(registro: RegistroGasto): Long

    @Update
    suspend fun update(registro: RegistroGasto)

    @Delete
    suspend fun delete(registro: RegistroGasto)

    @Query("DELETE FROM registros_gastos")
    suspend fun deleteAll()
}
