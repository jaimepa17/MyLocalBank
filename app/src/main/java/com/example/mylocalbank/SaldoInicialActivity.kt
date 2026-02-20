package com.example.mylocalbank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocalbank.data.*
import com.example.mylocalbank.databinding.ActivitySaldoInicialBinding
import java.util.Calendar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SaldoInicialActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaldoInicialBinding
    private lateinit var db: AppDatabase

    // Listas de datos completos
    private var gastosList: List<GastoFijo> = emptyList()
    private var fuentesList: List<FuenteIngreso> = emptyList()

    // Sets de items seleccionados
    private val selectedGastos = mutableSetOf<GastoFijo>()
    private val selectedFuentes = mutableSetOf<FuenteIngreso>()

    private val meses =
            arrayOf(
                    "Enero",
                    "Febrero",
                    "Marzo",
                    "Abril",
                    "Mayo",
                    "Junio",
                    "Julio",
                    "Agosto",
                    "Septiembre",
                    "Octubre",
                    "Noviembre",
                    "Diciembre"
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaldoInicialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        setupSpinners()
        setupButtons()

        lifecycleScope.launch { loadData() }
    }

    private fun setupSpinners() {
        // Spinner Mes
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        binding.spinnerMes.adapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, meses).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
        binding.spinnerMes.setSelection(currentMonth)

        // Spinner Año
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val anios =
                arrayOf(
                        (currentYear - 1).toString(),
                        currentYear.toString(),
                        (currentYear + 1).toString()
                )
        binding.spinnerAnio.adapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, anios).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
        binding.spinnerAnio.setSelection(1) // currentYear
    }

    private fun setupButtons() {
        binding.btnCancelar.setOnClickListener { finish() }

        binding.btnGuardar.setOnClickListener {
            val montoStr = binding.etMontoApertura.text.toString().trim()
            if (montoStr.isEmpty()) {
                Toast.makeText(this, "Ingresa un monto base", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val monto = montoStr.toDoubleOrNull()
            if (monto == null || monto < 0) {
                Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val mesApertura = binding.spinnerMes.selectedItemPosition + 1 // 1-12
            val anioApertura = binding.spinnerAnio.selectedItem.toString().toInt()

            guardarApertura(monto, mesApertura, anioApertura)
        }
    }

    private suspend fun loadData() {
        gastosList = db.gastoFijoDao().getAll().first()
        fuentesList = db.fuenteIngresoDao().getAll().first() // Use DAO to get everything

        setupLists()
    }

    private fun setupLists() {
        // Gastos RecyclerView
        val gastosAdapter =
                ChecklistAdapter(
                        items =
                                gastosList.map {
                                    ChecklistItem(it.id, it.nombre, it.moneda, it.monto, it)
                                },
                        onSelectionChanged = { item, isSelected ->
                            val gasto = item.tag as GastoFijo
                            if (isSelected) selectedGastos.add(gasto)
                            else selectedGastos.remove(gasto)
                        }
                )
        binding.rvGastosApertura.layoutManager = LinearLayoutManager(this)
        binding.rvGastosApertura.adapter = gastosAdapter

        // Ingresos RecyclerView
        val ingresosAdapter =
                ChecklistAdapter(
                        items =
                                fuentesList.map {
                                    ChecklistItem(it.id, it.nombre, it.moneda, it.monto, it)
                                },
                        onSelectionChanged = { item, isSelected ->
                            val fuente = item.tag as FuenteIngreso
                            if (isSelected) selectedFuentes.add(fuente)
                            else selectedFuentes.remove(fuente)
                        }
                )
        binding.rvIngresosApertura.layoutManager = LinearLayoutManager(this)
        binding.rvIngresosApertura.adapter = ingresosAdapter
    }

    private fun guardarApertura(monto: Double, mes: Int, anio: Int) {
        lifecycleScope.launch {
            // 1. Guardar SaldoInicial
            val saldo = SaldoInicial(monto = monto, mesApertura = mes, anioApertura = anio)
            db.saldoInicialDao().insert(saldo)

            // 2. Crear RegistroGasto para cada gasto marcado
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, anio)
            calendar.set(Calendar.MONTH, mes - 1)

            for (gasto in selectedGastos) {
                // Fechas aleatorias en el mes, pongamos el dia del cobro si existe
                calendar.set(
                        Calendar.DAY_OF_MONTH,
                        gasto.diaCobro.coerceAtMost(
                                calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        )
                )

                db.registroGastoDao()
                        .insert(
                                RegistroGasto(
                                        monto = gasto.monto,
                                        moneda = gasto.moneda,
                                        descripcion = "Apertura: ${gasto.nombre}",
                                        tienda = "Saldo Inicial",
                                        tarjetaId = gasto.tarjetaId,
                                        fecha = calendar.timeInMillis,
                                        categoriaId = gasto.categoriaId
                                )
                        )
            }

            // 3. Crear RegistroIngreso para cada ingreso marcado
            for (fuente in selectedFuentes) {
                calendar.set(
                        Calendar.DAY_OF_MONTH,
                        (fuente.diaIngreso ?: 1).coerceAtMost(
                                calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        )
                )

                db.registroIngresoDao()
                        .insert(
                                RegistroIngreso(
                                        monto = fuente.monto,
                                        moneda = fuente.moneda,
                                        descripcion = "Apertura: ${fuente.nombre}",
                                        fecha = calendar.timeInMillis,
                                        categoriaId =
                                                2, // Ingresos Rapidos id approx. Mejor forma seria
                                        // buscar la default, pero 2 suele ser Otros
                                        // ingresos o lo ignoramos.
                                        fuenteId = fuente.id
                                )
                        )
            }

            Toast.makeText(
                            this@SaldoInicialActivity,
                            "¡Saldo de apertura configurado!",
                            Toast.LENGTH_LONG
                    )
                    .show()
            finish()
        }
    }

    // --- Helper classes for RecyclerViews ---

    data class ChecklistItem(
            val id: Long,
            val nombre: String,
            val moneda: String,
            val monto: Double,
            val tag: Any // To store the original object
    )

    inner class ChecklistAdapter(
            private val items: List<ChecklistItem>,
            private val onSelectionChanged: (ChecklistItem, Boolean) -> Unit
    ) : RecyclerView.Adapter<ChecklistAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvNombre: android.widget.TextView = view.findViewById(R.id.tvNombre)
            val tvMonedaInfo: android.widget.TextView = view.findViewById(R.id.tvMonedaInfo)
            val tvMonto: android.widget.TextView = view.findViewById(R.id.tvMonto)
            val cbSelected: CheckBox = view.findViewById(R.id.cbSelected)

            init {
                view.setOnClickListener {
                    cbSelected.isChecked = !cbSelected.isChecked
                    onSelectionChanged(items[adapterPosition], cbSelected.isChecked)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v =
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_saldo_check, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            holder.tvNombre.text = item.nombre
            holder.tvMonedaInfo.text = "Fijo • ${item.moneda}"
            val prefix = if (item.moneda == "USD") "$" else "C$"
            holder.tvMonto.text = String.format("%s %.2f", prefix, item.monto)
            // It starts unchecked
            holder.cbSelected.isChecked = false
        }

        override fun getItemCount(): Int = items.size
    }
}
