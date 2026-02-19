package com.example.mylocalbank.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TarjetaDao {

    @Query("SELECT * FROM tarjetas ORDER BY fechaCreacion DESC")
    fun getAll(): Flow<List<Tarjeta>>

    @Query("SELECT * FROM tarjetas WHERE activa = 1 ORDER BY alias ASC")
    fun getActivas(): Flow<List<Tarjeta>>

    @Query("SELECT * FROM tarjetas WHERE id = :id")
    suspend fun getById(id: Long): Tarjeta?

    @Insert
    suspend fun insert(tarjeta: Tarjeta): Long

    @Update
    suspend fun update(tarjeta: Tarjeta)

    @Delete
    suspend fun delete(tarjeta: Tarjeta)

    @Query("DELETE FROM tarjetas")
    suspend fun deleteAll()
}
