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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylocalbank.data.AppDatabase
import com.example.mylocalbank.data.Categoria
import com.example.mylocalbank.data.FuenteIngreso
import com.example.mylocalbank.data.RegistroIngreso
import com.example.mylocalbank.databinding.FragmentIngresosBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class IngresosFragment : Fragment() {

    companion object {
        const val USD_TO_COR = 36.72
    }

    private var _binding: FragmentIngresosBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var extrasAdapter: RegistroIngresoAdapter
    private val viewModel: MainViewModel by activityViewModels()

    private var categoriasCache: List<Categoria> = emptyList()

    // Current month being viewed
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH)

    private val nf =
            NumberFormat.getNumberInstance(Locale("es", "NI")).apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }

    private val monthNames =
            arrayOf(
                    "ENERO",
                    "FEBRERO",
                    "MARZO",
                    "ABRIL",
                    "MAYO",
                    "JUNIO",
                    "JULIO",
                    "AGOSTO",
                    "SEPTIEMBRE",
                    "OCTUBRE",
                    "NOVIEMBRE",
                    "DICIEMBRE"
            )

    private fun toCor(monto: Double, moneda: String): Double {
        return if (moneda == "USD") monto * USD_TO_COR else monto
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngresosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getDatabase(requireContext())

        setupMonthNav()
        setupRecyclerView()
        setupFab()
        observeData()
    }

    private fun setupMonthNav() {
        updateMonthLabel()
        binding.btnPrevMonth.setOnClickListener {
            currentMonth--
            if (currentMonth < 0) {
                currentMonth = 11
                currentYear--
            }
            updateMonthLabel()
            observeData()
        }
        binding.btnNextMonth.setOnClickListener {
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
        binding.tvCurrentMonth.text = "${monthNames[currentMonth]} $currentYear"
    }

    private fun setupRecyclerView() {
        extrasAdapter =
                RegistroIngresoAdapter(
                        onEdit = { showIngresoDialog(it) },
                        onDelete = { registro ->
                            AlertDialog.Builder(requireContext())
                                    .setTitle("Eliminar ingreso")
                                    .setMessage("¿Eliminar este registro?")
                                    .setPositiveButton("Sí") { _, _ ->
                                        viewLifecycleOwner.lifecycleScope.launch {
                                            db.registroIngresoDao().delete(registro)
                                        }
                                    }
                                    .setNegativeButton("No", null)
                                    .show()
                        },
                        getCategoria = { id -> categoriasCache.find { it.id == id } }
                )
        binding.rvExtras.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExtras.adapter = extrasAdapter
    }

    private fun setupFab() {
        binding.fabAddIngreso.setOnClickListener { showIngresoDialog(null) }
    }

    private fun getMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(currentYear, currentMonth, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val end = calendar.timeInMillis
        return Pair(start, end)
    }

    // ─── Income Dialog ───────────────────────────────────────────────

    private fun showIngresoDialog(registro: RegistroIngreso?, templateFijo: FuenteIngreso? = null) {
        val dialogView =
                LayoutInflater.from(requireContext())
                        .inflate(R.layout.dialog_registro_ingreso, null)

        val spinnerMoneda = dialogView.findViewById<Spinner>(R.id.spinnerMoneda)
        val spinnerCategoria = dialogView.findViewById<Spinner>(R.id.spinnerCategoria)
        val etMonto = dialogView.findViewById<EditText>(R.id.etMonto)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
        val btnFecha = dialogView.findViewById<TextView>(R.id.btnFecha)
        val btnCancelar = dialogView.findViewById<TextView>(R.id.btnCancelar)
        val btnGuardar = dialogView.findViewById<TextView>(R.id.btnGuardar)

        // Advanced Toggle
        val btnToggle = dialogView.findViewById<TextView>(R.id.btnToggleAvanzado)
        val layoutAvanzado = dialogView.findViewById<LinearLayout>(R.id.layoutAvanzado)
        var isAdvanced = false // Start hidden (Quick Mode)

        if (registro != null) {
            // If editing, start in advanced mode
            isAdvanced = true
            layoutAvanzado.visibility = View.VISIBLE
            btnToggle.text = "Ocultar opciones avanzadas ▲"
        } else if (templateFijo != null) {
            isAdvanced = true
            layoutAvanzado.visibility = View.VISIBLE
            btnToggle.text = "Ocultar opciones avanzadas ▲"
        }

        btnToggle.setOnClickListener {
            isAdvanced = !isAdvanced
            if (isAdvanced) {
                layoutAvanzado.visibility = View.VISIBLE
                btnToggle.text = "Ocultar opciones avanzadas ▲"
            } else {
                layoutAvanzado.visibility = View.GONE
                btnToggle.text = "Mostrar opciones avanzadas ▼"
            }
        }

        // Moneda spinner
        val monedas = listOf("C$ (Córdobas)", "$ (Dólares)")
        spinnerMoneda.adapter =
                ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        monedas
                )

        // Category Spinner
        viewLifecycleOwner.lifecycleScope.launch {
            val allCats = viewModel.categoriasAll.first()
            val incomeCats =
                    allCats.filter { (it.tipo == "INGRESO" || it.tipo == "AMBOS") && it.activa }
            val catNames = incomeCats.map { "${it.icono} ${it.nombre}" }

            spinnerCategoria.adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, catNames)
                            .also {
                                it.setDropDownViewResource(
                                        android.R.layout.simple_spinner_dropdown_item
                                )
                            }

            // Sources Spinner
            val fuentes =
                    db.fuenteIngresoDao().getAll().first().filter {
                        it.activa && it.tipo == "TRABAJO"
                    }
            val fuenteNames = mutableListOf("Ninguna (Ingreso Extra)")
            fuenteNames.addAll(fuentes.map { it.nombre })

            val spinnerFuente = dialogView.findViewById<Spinner>(R.id.spinnerFuente)
            spinnerFuente.adapter =
                    ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    fuenteNames
                            )
                            .also {
                                it.setDropDownViewResource(
                                        android.R.layout.simple_spinner_dropdown_item
                                )
                            }

            if (registro != null) {
                // Category
                val idxCat = incomeCats.indexOfFirst { it.id == registro.categoriaId }
                if (idxCat >= 0) spinnerCategoria.setSelection(idxCat)

                // Source
                if (registro.fuenteId != null) {
                    val idxFuente = fuentes.indexOfFirst { it.id == registro.fuenteId }
                    if (idxFuente >= 0)
                            spinnerFuente.setSelection(idxFuente + 1) // +1 for "Ninguna"
                }
            } else if (templateFijo != null) {
                val idxFuente = fuentes.indexOfFirst { it.id == templateFijo.id }
                if (idxFuente >= 0) spinnerFuente.setSelection(idxFuente + 1)
            } else {
                // Find "Ingresos Rápidos" as default for validation awareness
                val idx = incomeCats.indexOfFirst { it.nombre == "Ingresos Rápidos" }
                if (idx >= 0) spinnerCategoria.setSelection(idx)
            }
        }

        // Date
        val selectedDate = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("es", "NI"))

        fun updateFechaLabel() {
            btnFecha.text = "📅 ${sdf.format(selectedDate.time)}"
        }
        updateFechaLabel()

        btnFecha.setOnClickListener {
            DatePickerDialog(
                            requireContext(),
                            { _, y, m, d ->
                                selectedDate.set(y, m, d)
                                updateFechaLabel()
                            },
                            selectedDate.get(Calendar.YEAR),
                            selectedDate.get(Calendar.MONTH),
                            selectedDate.get(Calendar.DAY_OF_MONTH)
                    )
                    .show()
        }

        // Pre-fill if editing
        if (registro != null) {
            etMonto.setText(registro.monto.toString())
            etDescripcion.setText(registro.descripcion)
            spinnerMoneda.setSelection(if (registro.moneda == "USD") 1 else 0)
            selectedDate.timeInMillis = registro.fecha
            updateFechaLabel()
        } else if (templateFijo != null) {
            etMonto.setText(templateFijo.monto.toString())
            etDescripcion.setText("Ingreso Fijo")
            spinnerMoneda.setSelection(if (templateFijo.moneda == "USD") 1 else 0)
        }

        val dialog =
                AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setCancelable(true)
                        .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancelar.setOnClickListener { dialog.dismiss() }
        btnGuardar.setOnClickListener {
            val montoStr = etMonto.text.toString().trim()
            val moneda = if (spinnerMoneda.selectedItemPosition == 1) "USD" else "COR"

            val monto = montoStr.toDoubleOrNull()
            if (monto == null || monto <= 0) {
                Toast.makeText(requireContext(), "Ingresa un monto válido", Toast.LENGTH_SHORT)
                        .show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val allCats = viewModel.categoriasAll.first()
                val incomeCats = allCats.filter { it.tipo == "INGRESO" || it.tipo == "AMBOS" }
                val catPos = spinnerCategoria.selectedItemPosition

                // Source Selection
                val spinnerFuente = dialogView.findViewById<Spinner>(R.id.spinnerFuente)
                val fuentes =
                        db.fuenteIngresoDao().getAll().first().filter {
                            it.activa && it.tipo == "TRABAJO"
                        }
                val fuentePos = spinnerFuente.selectedItemPosition
                val selectedFuente = if (fuentePos > 0) fuentes[fuentePos - 1] else null
                val fuenteId = selectedFuente?.id

                // Defaults for Quick Mode / Advanced Logic
                var descripcion: String
                var fecha: Long
                var categoriaId: Long

                if (!isAdvanced) {
                    // Quick Mode
                    descripcion =
                            if (selectedFuente != null) "Pago: ${selectedFuente.nombre}"
                            else "Ingreso Rápido"
                    fecha = System.currentTimeMillis() // Now

                    val quickCat = incomeCats.find { it.nombre == "Ingresos Rápidos" }
                    categoriaId = quickCat?.id ?: 1L // Fallback to "Otros" (1L) if not found
                } else {
                    // Advanced Mode logic
                    fecha = selectedDate.timeInMillis
                    descripcion = etDescripcion.text.toString().trim()

                    val selectedCat =
                            if (catPos >= 0 && catPos < incomeCats.size) incomeCats[catPos]
                            else null
                    categoriaId = selectedCat?.id ?: 1L
                    val catName = selectedCat?.nombre ?: ""

                    // Smart Validation
                    if (catName == "Ingresos Rápidos") {
                        // Allow empty description (default it)
                        if (descripcion.isEmpty()) {
                            descripcion =
                                    if (selectedFuente != null) "Pago: ${selectedFuente.nombre}"
                                    else "Ingreso Rápido"
                        }
                    } else {
                        // Mandate Description for specific categories
                        if (descripcion.isEmpty()) {
                            Toast.makeText(
                                            requireContext(),
                                            "Agrega una descripción para esta categoría",
                                            Toast.LENGTH_SHORT
                                    )
                                    .show()
                            return@launch
                        }
                    }
                }

                val montoRedondeado = Math.round(monto * 100.0) / 100.0

                if (registro != null) {
                    db.registroIngresoDao()
                            .update(
                                    registro.copy(
                                            monto = montoRedondeado,
                                            moneda = moneda,
                                            descripcion = descripcion,
                                            fecha = fecha,
                                            categoriaId = categoriaId,
                                            fuenteId = fuenteId
                                    )
                            )
                } else {
                    db.registroIngresoDao()
                            .insert(
                                    RegistroIngreso(
                                            monto = montoRedondeado,
                                            moneda = moneda,
                                            descripcion = descripcion,
                                            fecha = fecha,
                                            categoriaId = categoriaId,
                                            fuenteId = fuenteId
                                    )
                            )
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    // ─── Observe Data ────────────────────────────────────────────────

    private fun observeData() {
        val (start, end) = getMonthRange()
        val now = System.currentTimeMillis()

        // For first-month salary logic: viewing month year-month
        val viewingYM = currentYear * 12 + currentMonth

        // Determine the "effective day" for the viewing month
        // - If viewing a PAST month → all days are past, use 31 (everything is effective)
        // - If viewing the CURRENT month → use today's day
        // - If viewing a FUTURE month → nothing is effective yet, use 0
        val todayCal = Calendar.getInstance()
        val todayYM = todayCal.get(Calendar.YEAR) * 12 + todayCal.get(Calendar.MONTH)
        val effectiveDay =
                when {
                    viewingYM < todayYM -> 31 // past month: all paydays settled
                    viewingYM == todayYM -> todayCal.get(Calendar.DAY_OF_MONTH) // current month
                    else -> 0 // future month: nothing effective yet
                }

        // Fixed income sources observe call removed here; renderer moved inside combined block.

        // Observe Categories for caching
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoriasAll.collectLatest {
                categoriasCache = it
                extrasAdapter.notifyDataSetChanged()
            }
        }

        // Observe extra income records and combine with fixed
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                            db.fuenteIngresoDao().getActivas(),
                            db.registroIngresoDao().getByDateRange(start, end)
                    ) { fuentes, extras -> Pair(fuentes, extras) }
                    .collectLatest { (fuentes, extras) ->
                        // Update extras list
                        extrasAdapter.submitList(extras)
                        val isEmpty = extras.isEmpty()
                        binding.emptyStateExtras.visibility =
                                if (isEmpty) View.VISIBLE else View.GONE
                        binding.rvExtras.visibility = if (isEmpty) View.GONE else View.VISIBLE
                        binding.tvConteoExtras.text =
                                "${extras.size} registro${if (extras.size != 1) "s" else ""}"

                        // All records exist as real money
                        val totalEfectivo = extras.sumOf { toCor(it.monto, it.moneda) }

                        // Pendientes are only fixed sources without an extra record (whose fuenteId
                        // matches)
                        var totalPendiente = 0.0
                        for (f in fuentes) {
                            if (!extras.any { it.fuenteId == f.id }) {
                                totalPendiente += toCor(f.monto, f.moneda)
                            }
                        }

                        // Breakdown: Fijos Cobrados vs Extras libres
                        val fijosCobrados =
                                extras.filter { it.fuenteId != null }.sumOf {
                                    toCor(it.monto, it.moneda)
                                }
                        val extrasLibres = totalEfectivo - fijosCobrados

                        binding.tvTotalIngresos.text = "C$ ${nf.format(totalEfectivo)}"
                        binding.tvTotalFijos.text = "C$ ${nf.format(fijosCobrados)}"
                        binding.tvTotalExtras.text = "C$ ${nf.format(extrasLibres)}"
                        binding.tvIngresosEfectivo.text =
                                "✓ Efectivo: C$ ${nf.format(totalEfectivo)}"
                        binding.tvIngresosPendiente.text =
                                "⏳ Pendiente: C$ ${nf.format(totalPendiente)}"

                        updateIngresosFijos(fuentes, extras)
                    }
        }
    }

    // ─── Update fixed income section ─────────────────────────────────

    private fun updateIngresosFijos(fuentes: List<FuenteIngreso>, extras: List<RegistroIngreso>) {
        binding.layoutIngresosFijos.removeAllViews()

        if (fuentes.isEmpty()) {
            binding.tvEmptyFijos.visibility = View.VISIBLE
            return
        }
        binding.tvEmptyFijos.visibility = View.GONE

        val dp = resources.displayMetrics.density

        fuentes.forEach { fuente ->
            val isCobrado = extras.any { it.fuenteId == fuente.id }
            val amountColor = if (isCobrado) R.color.green_accent else R.color.red_accent
            val paidIcon = if (isCobrado) "✅" else "💼"

            val row =
                    LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER_VERTICAL
                        setPadding(0, (6 * dp).toInt(), 0, (6 * dp).toInt())

                        if (!isCobrado) {
                            setOnClickListener { showIngresoDialog(null, fuente) }
                        } else {
                            alpha = 0.5f
                        }
                    }

            row.addView(
                    TextView(requireContext()).apply {
                        text = paidIcon
                        textSize = 16f
                        layoutParams =
                                LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        .apply { marginEnd = (8 * dp).toInt() }
                    }
            )

            row.addView(
                    TextView(requireContext()).apply {
                        text = fuente.nombre
                        typeface = Typeface.MONOSPACE
                        textSize = 11f
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                        layoutParams =
                                LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1f
                                )
                    }
            )

            if (fuente.diaIngreso != null) {
                row.addView(
                        TextView(requireContext()).apply {
                            text = "Día ${fuente.diaIngreso}"
                            typeface = Typeface.MONOSPACE
                            textSize = 9f
                            setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.text_secondary)
                            )
                            layoutParams =
                                    LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                            )
                                            .apply { marginEnd = (8 * dp).toInt() }
                        }
                )
            }

            val symbol = if (fuente.moneda == "USD") "$" else "C$"
            row.addView(
                    TextView(requireContext()).apply {
                        text = "$symbol ${nf.format(fuente.monto)}"
                        typeface = Typeface.MONOSPACE
                        textSize = 11f
                        setTextColor(ContextCompat.getColor(requireContext(), amountColor))
                        setTypeface(typeface, Typeface.BOLD)
                    }
            )

            binding.layoutIngresosFijos.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
