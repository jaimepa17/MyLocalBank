package com.example.mylocalbank.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        tableName = "registros_gastos",
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
                        ),
                        ForeignKey(
                                entity = GastoFijo::class,
                                parentColumns = ["id"],
                                childColumns = ["gastoFijoId"],
                                onDelete = ForeignKey.SET_NULL
                        )],
        indices =
                [
                        Index(value = ["tarjetaId"]),
                        Index(value = ["categoriaId"]),
                        Index(value = ["gastoFijoId"])]
)
data class RegistroGasto(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val monto: Double,
        val moneda: String = "COR",
        val descripcion: String = "",
        val tienda: String = "",
        val tarjetaId: Long? = null,
        val fecha: Long = System.currentTimeMillis(),
        val categoriaId: Long = 1, // Default to "Otros" (ID 1)
        val gastoFijoId: Long? = null
)
