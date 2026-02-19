package com.example.mylocalbank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val icono: String = "🏷️", // Default icon
    val esDefault: Boolean = false, // True ONLY for "Otros"
    val activa: Boolean = true,
    val tipo: String = "AMBOS" // GASTO, INGRESO, AMBOS
)
