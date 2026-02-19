package com.example.mylocalbank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuentes_ingreso")
data class FuenteIngreso(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val tipo: String,           // TRABAJO, OTRO
    val descripcion: String,
    val monto: Double = 0.0,    // obligatorio si tipo=TRABAJO
    val moneda: String = "COR", // COR o USD
    val diaIngreso: Int? = null, // 1-31, solo si tipo=TRABAJO
    val fechaPostergada: Long? = null, // Fecha si el usuario dijo "Aun no"
    val notificarCobro: Boolean = true,
    val activa: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis()
)
