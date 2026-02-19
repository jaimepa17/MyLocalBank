package com.example.mylocalbank

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylocalbank.data.AppDatabase
import com.example.mylocalbank.data.GastoFijo
import com.example.mylocalbank.data.RegistroGasto
import com.example.mylocalbank.data.Tarjeta
import com.example.mylocalbank.data.Categoria
import com.example.mylocalbank.databinding.FragmentGastosBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.fragment.app.activityViewModels

class GastosFragment : Fragment() {

    companion object {
        const val USD_TO_COR = 36.72
    }

    private var _binding: FragmentGastosBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var recordsAdapter: RegistroGastoAdapter
    private val viewModel: MainViewModel by activityViewModels()

    private var tarjetasCache: List<Tarjeta> = emptyList()
    private var categoriasCache: List<Categoria> = emptyList()

    // Current month being viewed
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH)

    private val nf = NumberFormat.getNumberInstance(Locale("es", "NI")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    private val monthNames = arrayOf(
        "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
        "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"
    )

    private fun toCor(monto: Double, moneda: String): Double {
        return if (moneda == "USD") monto * USD_TO_COR else monto
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGastosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getDatabase(requireContext())

        setupAdapters()
        setupListeners()
        updateMonthLabel()
        observeData()
    }

    private fun setupListeners() {
        binding.fabAgregarGasto.setOnClickListener {
            showRegistroDialog(null)
        }
        binding.btnMesAnterior.setOnClickListener {
            currentMonth--
            if (currentMonth < 0) {
                currentMonth = 11
                currentYear--
            }
            updateMonthLabel()
            observeData()
        }
        binding.btnMesSiguiente.setOnClickListener {
            currentMonth++
            if (currentMonth > 11) {
                currentMonth = 0
                currentYear++
            }
            updateMonthLabel()
            observeData()
        }
    }

    private fun updateMonthLabel() {
        binding.tvMesActual.text = "${monthNames[currentMonth]} $currentYear"
    }

    /** Returns the effective max day to check for diaCobro based on selected month. */
    private fun getEffectiveDay(): Int {
        val now = Calendar.getInstance()
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
        }
        val lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        // For past months: all fixed expenses apply (return 31 so all pass the query)
        // For current month: only up to today's day (clamped to last day of month)
        // For future months: none apply (return 0)
        return when {
            currentYear < now.get(Calendar.YEAR) -> 31
            currentYear > now.get(Calendar.YEAR) -> 0
            currentMonth < now.get(Calendar.MONTH) -> 31
            currentMonth > now.get(Calendar.MONTH) -> 0
            else -> minOf(now.get(Calendar.DAY_OF_MONTH), lastDayOfMonth)
        }
    }

    private fun getMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val end = calendar.timeInMillis
        return Pair(start, end)
    }

    private fun setupAdapters() {
        recordsAdapter = RegistroGastoAdapter(
            onEdit = { showRegistroDialog(it) },
            onDelete = { confirmDelete(it) },
            getTarjeta = { id -> if (id != null) tarjetasCache.find { it.id == id } else null },
            getCategoria = { id -> categoriasCache.find { it.id == id } }
        )
        binding.rvRegistros.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRegistros.adapter = recordsAdapter
    }

    private fun showRegistroDialog(registro: RegistroGasto?) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_registro_gasto, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val spinnerMoneda = dialogView.findViewById<Spinner>(R.id.spinnerMoneda)
        val etMonto = dialogView.findViewById<EditText>(R.id.etMonto)
        val btnToggleAvanzado = dialogView.findViewById<TextView>(R.id.btnToggleAvanzado)
        val layoutAvanzado = dialogView.findViewById<LinearLayout>(R.id.layoutAvanzado)
        val spinnerCategoria = dialogView.findViewById<Spinner>(R.id.spinnerCategoria)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
        val etTienda = dialogView.findViewById<EditText>(R.id.etTienda)
        val spinnerTarjeta = dialogView.findViewById<Spinner>(R.id.spinnerTarjeta)
        val btnFecha = dialogView.findViewById<TextView>(R.id.btnFecha)
        val btnCancelar = dialogView.findViewById<TextView>(R.id.btnCancelar)
        val btnGuardar = dialogView.findViewById<TextView>(R.id.btnGuardar)

        if (registro != null) {
            tvTitle.text = "EDITAR GASTO"
            // Edit mode always shows advanced fields to see what's being edited
            layoutAvanzado.visibility = View.VISIBLE
            btnToggleAvanzado.text = "Ocultar opciones avanzadas ▲"
        }

        // Toggle Advanced Logic
        btnToggleAvanzado.setOnClickListener {
            if (layoutAvanzado.visibility == View.VISIBLE) {
                layoutAvanzado.visibility = View.GONE
                btnToggleAvanzado.text = "Mostrar opciones avanzadas ▼"
            } else {
                layoutAvanzado.visibility = View.VISIBLE
                btnToggleAvanzado.text = "Ocultar opciones avanzadas ▲"
            }
        }

        // Date tracking
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "NI"))
        var selectedDate = Calendar.getInstance()
        if (registro != null) {
            selectedDate.timeInMillis = registro.fecha
        }
        btnFecha.text = "📅 ${dateFormat.format(selectedDate.time)}"

        btnFecha.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate.set(year, month, dayOfMonth)
                    btnFecha.text = "📅 ${dateFormat.format(selectedDate.time)}"
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Moneda spinner
        val monedas = arrayOf("COR (C$)", "USD ($)")
        spinnerMoneda.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, monedas)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Load Tarjetas and Categorias
        viewLifecycleOwner.lifecycleScope.launch {
            val tarjetas = db.tarjetaDao().getActivas().first()
            val tarjetaNames = mutableListOf("Ninguna")
            tarjetaNames.addAll(tarjetas.map { "${it.alias} (••${it.ultimos4})" })

            spinnerTarjeta.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tarjetaNames)
                .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            // Categories
            val allCats = viewModel.categoriasAll.first()
            val expenseCats = allCats.filter { it.tipo == "GASTO" || it.tipo == "AMBOS" }
            val categoriaNames: List<String> = expenseCats.map { "${it.icono} ${it.nombre}" }
            spinnerCategoria.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoriaNames)
                .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            // Pre-fill if editing
            if (registro != null) {
                etMonto.setText(String.format("%.2f", registro.monto))
                spinnerMoneda.setSelection(if (registro.moneda == "USD") 1 else 0)
                etDescripcion.setText(registro.descripcion)
                etTienda.setText(registro.tienda)

                // Select Category
                val catIdx = expenseCats.indexOfFirst { it.id == registro.categoriaId }
                if (catIdx >= 0) spinnerCategoria.setSelection(catIdx)

                if (registro.tarjetaId != null) {
                    val idx = tarjetas.indexOfFirst { it.id == registro.tarjetaId }
                    if (idx >= 0) spinnerTarjeta.setSelection(idx + 1)
                }
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView).setCancelable(true).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancelar.setOnClickListener { dialog.dismiss() }
        btnGuardar.setOnClickListener {
            val montoStr = etMonto.text.toString().trim()
            val moneda = if (spinnerMoneda.selectedItemPosition == 1) "USD" else "COR"
            
            val isAdvanced = layoutAvanzado.visibility == View.VISIBLE

            val monto = montoStr.toDoubleOrNull()
            if (monto == null || monto <= 0) {
                Toast.makeText(requireContext(), "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val montoRedondeado = Math.round(monto * 100.0) / 100.0

            viewLifecycleOwner.lifecycleScope.launch {
                val allCats = viewModel.categoriasAll.first()
                val expenseCats = allCats.filter { it.tipo == "GASTO" || it.tipo == "AMBOS" }
                val tarjetas = db.tarjetaDao().getActivas().first()

                // Variables determined by mode
                val descripcion: String
                val tienda: String
                val fecha: Long
                val categoriaId: Long
                val tarjetaId: Long?

                if (isAdvanced) {
                    val catPos = spinnerCategoria.selectedItemPosition
                    val selectedCat = if (catPos >= 0 && catPos < expenseCats.size) expenseCats[catPos] else null
                    categoriaId = selectedCat?.id ?: 1L

                    // Validation Logic
                    if (selectedCat != null && selectedCat.nombre == "Gastos Rápidos") {
                        // Gastos Rápidos: Only Amount required (checked above)
                        // Optional fields can be empty or used if provided
                        val descInput = etDescripcion.text.toString().trim()
                        descripcion = if (descInput.isNotEmpty()) descInput else "Gasto Rápido"
                        tienda = etTienda.text.toString().trim()
                    } else {
                        // Other Categories: ALL fields required
                        val descInput = etDescripcion.text.toString().trim()
                        val tiendaInput = etTienda.text.toString().trim()

                        if (descInput.isEmpty()) {
                            Toast.makeText(requireContext(), "Descripción es requerida para esta categoría", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        if (tiendaInput.isEmpty()) {
                            Toast.makeText(requireContext(), "Tienda es requerida para esta categoría", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        descripcion = descInput
                        tienda = tiendaInput
                    }

                    fecha = selectedDate.timeInMillis

                    val tarjetaPos = spinnerTarjeta.selectedItemPosition
                    tarjetaId = if (tarjetaPos > 0 && tarjetaPos <= tarjetas.size) {
                        tarjetas[tarjetaPos - 1].id
                    } else null

                } else {
                    // Quick Mode: Defaults (Implicitly Gastos Rápidos)
                    descripcion = "Gasto Rápido"
                    tienda = ""
                    fecha = System.currentTimeMillis() // Today / Now

                    // Find "Gastos Rápidos" category or fallback to "Otros"
                    val quickCat = expenseCats.find { it.nombre == "Gastos Rápidos" }
                    categoriaId = quickCat?.id ?: 1L // Default to Other if not found

                    tarjetaId = null
                }

                if (registro != null) {
                    db.registroGastoDao().update(
                        registro.copy(
                            monto = montoRedondeado,
                            moneda = moneda,
                            descripcion = descripcion,
                            tienda = tienda,
                            tarjetaId = tarjetaId,
                            fecha = fecha,
                            categoriaId = categoriaId
                        )
                    )
                } else {
                    db.registroGastoDao().insert(
                        RegistroGasto(
                            monto = montoRedondeado,
                            moneda = moneda,
                            descripcion = descripcion,
                            tienda = tienda,
                            tarjetaId = tarjetaId,
                            fecha = fecha,
                            categoriaId = categoriaId
                        )
                    )
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun observeData() {
        val (start, end) = getMonthRange()
        val now = System.currentTimeMillis()
        val todayDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)

        // Observe tarjetas cache
        // Observe tarjetas cache
        viewLifecycleOwner.lifecycleScope.launch {
            db.tarjetaDao().getAll().collectLatest { list ->
                tarjetasCache = list
                recordsAdapter.notifyDataSetChanged()
            }
        }

        // Observe categories cache
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoriasAll.collectLatest { list: List<Categoria> ->
                categoriasCache = list
                recordsAdapter.notifyDataSetChanged()
            }
        }

        // Observe variable + fixed expenses with effective/pending split
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                db.registroGastoDao().getByDateRange(start, end),
                db.gastoFijoDao().getActivos()          // ALL active, not filtered by day
            ) { registros, fijos ->
                Pair(registros, fijos)
            }.collectLatest { (registros, fijos) ->
                // Update variable expenses list (show all)
                recordsAdapter.submitList(registros)
                val isEmpty = registros.isEmpty()
                binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
                binding.rvRegistros.visibility = if (isEmpty) View.GONE else View.VISIBLE

                // Split variable expenses: effective vs pending
                val varEfectivo = registros.filter { it.fecha <= now }.sumOf { toCor(it.monto, it.moneda) }
                val varPendiente = registros.filter { it.fecha > now }.sumOf { toCor(it.monto, it.moneda) }

                // Split fixed expenses: Calculate based on Month/Year relation
                val viewedYM = currentYear * 12 + currentMonth
                val currentCal = java.util.Calendar.getInstance()
                val currentYM = currentCal.get(java.util.Calendar.YEAR) * 12 + currentCal.get(java.util.Calendar.MONTH)
                
                val fijEfectivo: Double
                val fijPendiente: Double

                if (viewedYM < currentYM) {
                    // Past Month: All Effective
                    fijEfectivo = fijos.sumOf { toCor(it.monto, it.moneda) }
                    fijPendiente = 0.0
                } else if (viewedYM > currentYM) {
                    // Future Month: All Pending
                    fijEfectivo = 0.0
                    fijPendiente = fijos.sumOf { toCor(it.monto, it.moneda) }
                } else {
                    // Current Month: Use Day Logic
                    fijEfectivo = fijos.filter { it.diaCobro <= todayDay }.sumOf { toCor(it.monto, it.moneda) }
                    fijPendiente = fijos.filter { it.diaCobro > todayDay }.sumOf { toCor(it.monto, it.moneda) }
                }

                val totalVariables = varEfectivo + varPendiente
                val totalFijos = fijEfectivo + fijPendiente
                val totalEfectivo = varEfectivo + fijEfectivo
                val totalPendiente = varPendiente + fijPendiente

                binding.tvDesgloseVariables.text = "Variables: C$ ${nf.format(totalVariables)}"
                binding.tvDesgloseFijos.text = "Fijos: C$ ${nf.format(totalFijos)}"
                binding.tvTotalGastado.text = "C$ ${nf.format(totalFijos + totalVariables)}"
                binding.tvTotalEfectivo.text = "✓ Efectivo: C$ ${nf.format(totalEfectivo)}"
                binding.tvTotalPendiente.text = "⏳ Pendiente: C$ ${nf.format(totalPendiente)}"

                updateGastosFijosSection(fijos)
            }
        }
    }

    private fun updateGastosFijosSection(fijos: List<GastoFijo>) {
        binding.layoutGastosFijos.removeAllViews()

        if (fijos.isEmpty()) {
            val emptyTv = TextView(requireContext()).apply {
                text = "Sin gastos fijos cobrados"
                typeface = Typeface.MONOSPACE
                textSize = 11f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                gravity = Gravity.CENTER
                setPadding(0, 16, 0, 16)
            }
            binding.layoutGastosFijos.addView(emptyTv)
            return
        }

        val dp = resources.displayMetrics.density
        fijos.forEach { gf ->
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_stat_card)
                setPadding((12 * dp).toInt(), (10 * dp).toInt(), (12 * dp).toInt(), (10 * dp).toInt())
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = (4 * dp).toInt() }
            }

            // Icon
            row.addView(TextView(requireContext()).apply {
                text = "📌"
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginEnd = (8 * dp).toInt() }
            })

            // Name + day
            row.addView(TextView(requireContext()).apply {
                text = "${gf.nombre} (día ${gf.diaCobro})"
                typeface = Typeface.MONOSPACE
                textSize = 11f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })

            // Amount
            val symbol = if (gf.moneda == "USD") "$" else "C$"
            row.addView(TextView(requireContext()).apply {
                text = "-$symbol${nf.format(gf.monto)}"
                typeface = Typeface.MONOSPACE
                textSize = 11f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.red_accent))
                setTypeface(typeface, Typeface.BOLD)
            })

            binding.layoutGastosFijos.addView(row)
        }
    }

    private fun confirmDelete(registro: RegistroGasto) {
        val desc = if (registro.descripcion.isNotEmpty()) registro.descripcion else "Gasto"

        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar registro")
            .setMessage("¿Eliminar \"$desc\" (C$ ${nf.format(registro.monto)})?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    db.registroGastoDao().delete(registro)
                    Toast.makeText(requireContext(), "Eliminado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
