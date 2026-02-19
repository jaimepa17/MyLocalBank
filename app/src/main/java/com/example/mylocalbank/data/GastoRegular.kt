package com.example.mylocalbank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos_regulares")
data class GastoRegular(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val icono: String = "☕",        // Emoji representativo
    val activa: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis()
)
