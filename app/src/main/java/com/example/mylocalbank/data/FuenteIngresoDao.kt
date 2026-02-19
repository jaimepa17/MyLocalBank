package com.example.mylocalbank.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FuenteIngresoDao {

    @Query("SELECT * FROM fuentes_ingreso ORDER BY fechaCreacion DESC")
    fun getAll(): Flow<List<FuenteIngreso>>

    @Query("SELECT * FROM fuentes_ingreso WHERE activa = 1 ORDER BY nombre ASC")
    fun getActivas(): Flow<List<FuenteIngreso>>

    @Query("SELECT * FROM fuentes_ingreso WHERE tipo = 'TRABAJO' AND activa = 1")
    fun getTrabajos(): Flow<List<FuenteIngreso>>

    @Query("SELECT * FROM fuentes_ingreso WHERE id = :id")
    suspend fun getById(id: Long): FuenteIngreso?

    @Insert
    suspend fun insert(fuenteIngreso: FuenteIngreso): Long

    @Update
    suspend fun update(fuenteIngreso: FuenteIngreso)

    @Delete
    suspend fun delete(fuenteIngreso: FuenteIngreso)

    @Query("DELETE FROM fuentes_ingreso")
    suspend fun deleteAll()
}
