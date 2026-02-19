package com.example.mylocalbank

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mylocalbank.data.AppDatabase
import com.example.mylocalbank.data.FuenteIngreso
import com.example.mylocalbank.data.Categoria
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val fuenteDao = db.fuenteIngresoDao()

    // Cache for all income sources.
    // Started eagerly to load data as soon as the ViewModel is created (app start / main activity creation).
    // Kept alive for 5 seconds after last subscriber leaves (rotation/config change), 
    // but since it's scoped to Activity, it stays alive as long as the Activity is.
    val fuentesAll: StateFlow<List<FuenteIngreso>> = fuenteDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val categoriaDao = db.categoriaDao()
    val categoriasAll: StateFlow<List<Categoria>> = categoriaDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        ensureDefaults()
    }

    private fun ensureDefaults() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = categoriaDao.getCountByName("Gastos Rápidos")
            if (count == 0) {
                categoriaDao.insert(
                    Categoria(
                        nombre = "Gastos Rápidos",
                        icono = "⚡",
                        esDefault = true,
                        activa = true,
                        tipo = "GASTO"
                    )
                )
            }
            
            val countIngresos = categoriaDao.getCountByName("Ingresos Rápidos")
            if (countIngresos == 0) {
                categoriaDao.insert(
                    Categoria(
                        nombre = "Ingresos Rápidos",
                        icono = "⚡",
                        esDefault = true,
                        activa = true,
                        tipo = "INGRESO"
                    )
                )
            }
        }
    }
}
