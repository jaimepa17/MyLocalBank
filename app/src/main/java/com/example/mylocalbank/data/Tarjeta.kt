package com.example.mylocalbank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarjetas")
data class Tarjeta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alias: String,
    val ultimos4: String,
    val tipo: String,       // CREDITO, DEBITO, PREPAGO
    val banco: String,
    val color: String,      // hex color e.g. "#0F9B58"
    val activa: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis()
)
