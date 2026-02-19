package com.example.mylocalbank.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoRegularDao {

    @Query("SELECT * FROM gastos_regulares ORDER BY fechaCreacion DESC")
    fun getAll(): Flow<List<GastoRegular>>

    @Query("SELECT * FROM gastos_regulares WHERE activa = 1 ORDER BY nombre ASC")
    fun getActivos(): Flow<List<GastoRegular>>

    @Query("SELECT * FROM gastos_regulares WHERE id = :id")
    suspend fun getById(id: Long): GastoRegular?

    @Insert
    suspend fun insert(gastoRegular: GastoRegular): Long

    @Update
    suspend fun update(gastoRegular: GastoRegular)

    @Delete
    suspend fun delete(gastoRegular: GastoRegular)
}
