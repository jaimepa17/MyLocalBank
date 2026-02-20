package com.example.mylocalbank.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SaldoInicialDao {

    @Insert
    suspend fun insert(saldoInicial: SaldoInicial)

    @Query("SELECT * FROM saldo_inicial LIMIT 1")
    fun getSaldoInicial(): Flow<SaldoInicial?>

    @Query("SELECT COUNT(*) FROM saldo_inicial")
    suspend fun hasSaldoInicial(): Int

    @Query("SELECT * FROM saldo_inicial LIMIT 1")
    suspend fun getSaldoInicialSync(): SaldoInicial?
}
