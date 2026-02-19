package com.example.mylocalbank.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        tableName = "gastos_fijos",
        foreignKeys =
                [
                        ForeignKey(
                                entity = Tarjeta::class,
                                parentColumns = ["id"],
                                childColumns = ["tarjetaId"],
                                onDelete = ForeignKey.SET_NULL
                        ),
                        ForeignKey(
                                entity = Categoria::class,
                                parentColumns = ["id"],
                                childColumns = ["categoriaId"],
                                onDelete = ForeignKey.RESTRICT
                        )],
        indices = [Index(value = ["tarjetaId"]), Index(value = ["categoriaId"])]
)
data class GastoFijo(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val nombre: String,
        val monto: Double,
        val moneda: String = "COR", // COR o USD
        val tarjetaId: Long? = null, // FK opcional a tarjetas
        val diaCobro: Int = 1, // Día del mes en que se cobra (1-31)
        val activa: Boolean = true,
        val fechaCreacion: Long = System.currentTimeMillis(),
        val categoriaId: Long = 1 // Default to "Otros" (ID 1)
)
