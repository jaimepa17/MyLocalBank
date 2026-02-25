package com.example.mylocalbank.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        tableName = "registros_ingresos",
        foreignKeys =
                [
                        androidx.room.ForeignKey(
                                entity = Categoria::class,
                                parentColumns = ["id"],
                                childColumns = ["categoriaId"],
                                onDelete = androidx.room.ForeignKey.RESTRICT
                        ),
                        androidx.room.ForeignKey(
                                entity = FuenteIngreso::class,
                                parentColumns = ["id"],
                                childColumns = ["fuenteId"],
                                onDelete = androidx.room.ForeignKey.SET_NULL
                        )],
        indices = [Index(value = ["categoriaId"]), Index(value = ["fuenteId"])]
)
data class RegistroIngreso(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val monto: Double,
        val moneda: String = "COR", // COR o USD
        val descripcion: String = "",
        val fecha: Long = System.currentTimeMillis(),
        val categoriaId: Long = 1, // Default to "Otros" (ID 1)
        val fuenteId: Long? = null // Link to fixed source if applicable
)
