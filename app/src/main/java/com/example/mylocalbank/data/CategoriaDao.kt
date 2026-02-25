package com.example.mylocalbank.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias ORDER BY activa DESC, esDefault DESC, nombre ASC")
    fun getAll(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias") fun getAllSync(): List<Categoria>

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(categoria: Categoria)

    @Update suspend fun update(categoria: Categoria)

    @Delete suspend fun delete(categoria: Categoria)

    @Query("SELECT * FROM categorias WHERE id = :id") suspend fun getById(id: Long): Categoria?

    @Query("SELECT COUNT(*) FROM categorias WHERE nombre = :nombre")
    suspend fun getCountByName(nombre: String): Int

    @Query("SELECT * FROM categorias") fun getAllForBackup(): Flow<List<Categoria>>

    @Query("DELETE FROM categorias") suspend fun deleteAll()
}
