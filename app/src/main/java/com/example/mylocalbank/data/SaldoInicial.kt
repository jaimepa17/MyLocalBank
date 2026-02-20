package com.example.mylocalbank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saldo_inicial")
data class SaldoInicial(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val monto: Double,
    val mesApertura: Int,
    val anioApertura: Int
)
