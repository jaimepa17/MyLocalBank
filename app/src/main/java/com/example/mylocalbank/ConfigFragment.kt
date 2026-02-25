package com.example.mylocalbank

import android.app.AlertDialog
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylocalbank.data.*
import com.example.mylocalbank.databinding.FragmentConfigBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfigFragment : Fragment() {

    private var _binding: FragmentConfigBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var tarjetaAdapter: TarjetaAdapter
    private lateinit var fuenteAdapter: FuenteIngresoAdapter
    private lateinit var gastoFijoAdapter: GastoFijoAdapter
    private lateinit var categoriaAdapter: CategoriaAdapter

    // Shared ViewModel for caching
    private val viewModel: MainViewModel by activityViewModels()

    // Cache de tarjetas para lookup rápido en el adapter de gastos fijos
    private var tarjetasCache: List<Tarjeta> = emptyList()

    private val cardColors =
            listOf(
                    "#0F9B58",
                    "#3157D6",
                    "#F5C842",
                    "#E74C3C",
                    "#9B59B6",
                    "#1ABC9C",
                    "#E67E22",
                    "#2C3E50"
            )

    private val expenseIcons =
            listOf(
                    "☕",
                    "🍔",
                    "🚗",
                    "🏥",
                    "🎮",
                    "👕",
                    "📱",
                    "🎬",
                    "🍕",
                    "🚌",
                    "💊",
                    "📚",
                    "🏋️",
                    "✂️",
                    "🐕",
                    "🎁"
            )

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getDatabase(requireContext())
        setupTarjetas()
        setupFuentes()
        setupGastosFijos()
        setupCategorias()
        setupBackupRestore()
        setupSaldoInicial()
    }

    // ====================== TARJETAS ======================

    private fun setupTarjetas() {
        tarjetaAdapter =
                TarjetaAdapter(
                        onEdit = { showTarjetaDialog(it) },
                        onDelete = { confirmDeleteTarjeta(it) }
                )
        binding.rvTarjetas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTarjetas.adapter = tarjetaAdapter

        binding.btnAgregarTarjeta.setOnClickListener { showTarjetaDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            db.tarjetaDao().getAll().collectLatest { list ->
                tarjetaAdapter.submitList(list)
                binding.emptyStateTarjetas.visibility =
                        if (list.isEmpty()) View.VISIBLE else View.GONE
                // Update cache for gastos fijos adapter
                tarjetasCache = list
            }
        }
    }

    private fun showTarjetaDialog(tarjeta: Tarjeta?) {
        val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_tarjeta, null)

        val etAlias = dialogView.findViewById<EditText>(R.id.etAlias)
        val etUltimos4 = dialogView.findViewById<EditText>(R.id.etUltimos4)
        val etBanco = dialogView.findViewById<EditText>(R.id.etBanco)
        val spinnerTipo = dialogView.findViewById<Spinner>(R.id.spinnerTipo)
        val colorPicker = dialogView.findViewById<LinearLayout>(R.id.colorPicker)
        val btnCancelar = dialogView.findViewById<TextView>(R.id.btnCancelar)
        val btnGuardar = dialogView.findViewById<TextView>(R.id.btnGuardar)

        val tipos = arrayOf("DEBITO", "CREDITO", "PREPAGO")
        spinnerTipo.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tipos).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

        var selectedColor = cardColors[0]
        setupColorPicker(colorPicker, selectedColor) { selectedColor = it }

        if (tarjeta != null) {
            etAlias.setText(tarjeta.alias)
            etUltimos4.setText(tarjeta.ultimos4)
            etBanco.setText(tarjeta.banco)
            spinnerTipo.setSelection(tipos.indexOf(tarjeta.tipo).coerceAtLeast(0))
            selectedColor = tarjeta.color
            setupColorPicker(colorPicker, selectedColor) { selectedColor = it }
        }

        val dialog =
                AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setCancelable(true)
                        .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancelar.setOnClickListener { dialog.dismiss() }
        btnGuardar.setOnClickListener {
            val alias = etAlias.text.toString().trim()
            val u4 = etUltimos4.text.toString().trim()
            val banco = etBanco.text.toString().trim()
            val tipo = spinnerTipo.selectedItem.toString()

            if (alias.isEmpty() || u4.isEmpty() || banco.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT)
                        .show()
                return@setOnClickListener
            }
            if (u4.length != 4) {
                Toast.makeText(requireContext(), "Ingresa 4 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                if (tarjeta != null) {
                    db.tarjetaDao()
                            .update(
                                    tarjeta.copy(
                                            alias = alias,
                                            ultimos4 = u4,
                                            banco = banco,
                                            tipo = tipo,
                                            color = selectedColor
                                    )
                            )
                } else {
                    db.tarjetaDao()
                            .insert(
                                    Tarjeta(
                                            alias = alias,
                                            ultimos4 = u4,
                                            banco = banco,
                                            tipo = tipo,
                                            color = selectedColor
                                    )
                            )
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun setupColorPicker(
            container: LinearLayout,
            selected: String,
            onSelect: (String) -> Unit
    ) {
        container.removeAllViews()
        val dp = resources.displayMetrics.density
        cardColors.forEach { color ->
            val v =
                    View(requireContext()).apply {
                        layoutParams =
                                LinearLayout.LayoutParams((36 * dp).toInt(), (36 * dp).toInt())
                                        .apply {
                                            setMargins((6 * dp).toInt(), 0, (6 * dp).toInt(), 0)
                                        }
                        background =
                                GradientDrawable().apply {
                                    shape = GradientDrawable.RECTANGLE
                                    cornerRadius = 4 * dp
                                    setColor(Color.parseColor(color))
                                    setStroke(
                                            (if (color == selected) 3 else 1 * dp).toInt(),
                                            if (color == selected) Color.WHITE
                                            else Color.parseColor("#44FFFFFF")
                                    )
                                }
                        setOnClickListener {
                            onSelect(color)
                            setupColorPicker(container, color, onSelect)
                        }
                    }
            container.addView(v)
        }
    }

    private fun confirmDeleteTarjeta(tarjeta: Tarjeta) {
        AlertDialog.Builder(requireContext())
                .setTitle("Eliminar tarjeta")
                .setMessage("¿Eliminar \"${tarjeta.alias}\"?")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch { db.tarjetaDao().delete(tarjeta) }
                }
                .setNegativeButton("Cancelar", null)
                .show()
    }

    // ====================== FUENTES DE INGRESO ======================

    private fun setupFuentes() {
        fuenteAdapter =
                FuenteIngresoAdapter(
                        onEdit = { showFuenteDialog(it) },
                        onDelete = { confirmDeleteFuente(it) }
                )
        binding.rvFuentes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFuentes.adapter = fuenteAdapter

        binding.btnAgregarFuente.setOnClickListener { showFuenteDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            // Observe shared ViewModel (cached data)
            viewModel.fuentesAll.collectLatest { list ->
                fuenteAdapter.submitList(list)
                binding.emptyStateFuentes.visibility =
                        if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showFuenteDialog(fuente: FuenteIngreso?) {
        val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_fuente_ingreso, null)

        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val spinnerTipo = dialogView.findViewById<Spinner>(R.id.spinnerTipo)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
        val spinnerMoneda = dialogView.findViewById<Spinner>(R.id.spinnerMoneda)
        val layoutTrabajo = dialogView.findViewById<LinearLayout>(R.id.layoutTrabajo)
        val etMonto = dialogView.findViewById<EditText>(R.id.etMonto)
        val etDiaIngreso = dialogView.findViewById<EditText>(R.id.etDiaIngreso)
        val switchNotificar =
                dialogView.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(
                        R.id.switchNotificar
                )
        val btnCancelar = dialogView.findViewById<TextView>(R.id.btnCancelar)
        val btnGuardar = dialogView.findViewById<TextView>(R.id.btnGuardar)

        // Tipo — only TRABAJO allowed now
        val tipos = arrayOf("TRABAJO")
        spinnerTipo.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tipos).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
        spinnerTipo.isEnabled = false // no need to select, always TRABAJO

        // Moneda spinner
        val monedas = arrayOf("COR (C$)", "USD ($)")
        spinnerMoneda.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, monedas).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

        // TRABAJO fields always visible
        layoutTrabajo.visibility = View.VISIBLE

        // Pre-fill if editing
        if (fuente != null) {
            etNombre.setText(fuente.nombre)
            spinnerTipo.setSelection(tipos.indexOf(fuente.tipo).coerceAtLeast(0))
            etDescripcion.setText(fuente.descripcion)
            spinnerMoneda.setSelection(if (fuente.moneda == "USD") 1 else 0)
            if (fuente.monto > 0) etMonto.setText(String.format("%.2f", fuente.monto))
            fuente.diaIngreso?.let { etDiaIngreso.setText(it.toString()) }
            switchNotificar.isChecked = fuente.notificarCobro
        } else {
            switchNotificar.isChecked = true // Default ON
        }

        val dialog =
                AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setCancelable(true)
                        .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancelar.setOnClickListener { dialog.dismiss() }
        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val tipo = "TRABAJO" // always TRABAJO
            val descripcion = etDescripcion.text.toString().trim()
            val moneda = if (spinnerMoneda.selectedItemPosition == 1) "USD" else "COR"
            val notificar = switchNotificar.isChecked

            if (nombre.isEmpty()) {
                Toast.makeText(requireContext(), "Ingresa un nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var monto = 0.0
            var dia: Int? = null

            if (tipo == "TRABAJO") {
                val montoStr = etMonto.text.toString().trim()
                val diaStr = etDiaIngreso.text.toString().trim()

                if (montoStr.isEmpty() || diaStr.isEmpty()) {
                    Toast.makeText(
                                    requireContext(),
                                    "Monto y día son obligatorios para Trabajo",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                    return@setOnClickListener
                }

                monto = montoStr.toDoubleOrNull() ?: 0.0
                dia = diaStr.toIntOrNull()

                if (monto <= 0) {
                    Toast.makeText(requireContext(), "Monto debe ser mayor a 0", Toast.LENGTH_SHORT)
                            .show()
                    return@setOnClickListener
                }
                if (dia == null || dia < 1 || dia > 31) {
                    Toast.makeText(
                                    requireContext(),
                                    "Día debe ser entre 1 y 31",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                    return@setOnClickListener
                }

                // Round to 2 decimals
                monto = Math.round(monto * 100.0) / 100.0
            }

            viewLifecycleOwner.lifecycleScope.launch {
                if (fuente != null) {
                    db.fuenteIngresoDao()
                            .update(
                                    fuente.copy(
                                            nombre = nombre,
                                            tipo = tipo,
                                            descripcion = descripcion,
                                            moneda = moneda,
                                            monto = monto,
                                            diaIngreso = dia,
                                            notificarCobro = notificar
                                    )
                            )
                } else {
                    db.fuenteIngresoDao()
                            .insert(
                                    FuenteIngreso(
                                            nombre = nombre,
                                            tipo = tipo,
                                            descripcion = descripcion,
                                            moneda = moneda,
                                            monto = monto,
                                            diaIngreso = dia,
                                            notificarCobro = notificar
                                    )
                            )
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun confirmDeleteFuente(fuente: FuenteIngreso) {
        AlertDialog.Builder(requireContext())
                .setTitle("Eliminar fuente")
                .setMessage("¿Eliminar \"${fuente.nombre}\"?")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        db.fuenteIngresoDao().delete(fuente)
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
    }

    // ====================== GASTOS FIJOS ======================

    private fun setupGastosFijos() {
        gastoFijoAdapter =
                GastoFijoAdapter(
                        onEdit = { showGastoFijoDialog(it) },
                        onDelete = { confirmDeleteGastoFijo(it) },
                        getTarjetaAlias = { tarjetaId ->
                            tarjetaId?.let { id -> tarjetasCache.find { it.id == id }?.alias }
                        }
                )
        binding.rvGastosFijos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGastosFijos.adapter = gastoFijoAdapter

        binding.btnAgregarGastoFijo.setOnClickListener { showGastoFijoDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            db.gastoFijoDao().getAll().collectLatest { list ->
                gastoFijoAdapter.submitList(list)
                binding.emptyStateGastosFijos.visibility =
                        if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showGastoFijoDialog(gasto: GastoFijo?) {
        val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_gasto_fijo, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etMonto = dialogView.findViewById<EditText>(R.id.etMonto)
        val spinnerMoneda = dialogView.findViewById<Spinner>(R.id.spinnerMoneda)
        val etDiaCobro = dialogView.findViewById<EditText>(R.id.etDiaCobro)
        val spinnerTarjeta = dialogView.findViewById<Spinner>(R.id.spinnerTarjeta)
        val btnCancelar = dialogView.findViewById<TextView>(R.id.btnCancelar)
        val btnGuardar = dialogView.findViewById<TextView>(R.id.btnGuardar)

        if (gasto != null) tvTitle.text = "EDITAR GASTO FIJO"

        // Moneda spinner
        val monedas = arrayOf("COR (C$)", "USD ($)")
        spinnerMoneda.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, monedas).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

        // Tarjeta spinner — cargamos las tarjetas activas + opción "Ninguna"
        viewLifecycleOwner.lifecycleScope.launch {
            val tarjetasActivas = db.tarjetaDao().getActivas().first()
            val tarjetaNames = mutableListOf("Ninguna")
            tarjetaNames.addAll(tarjetasActivas.map { "${it.alias} (••${it.ultimos4})" })

            spinnerTarjeta.adapter =
                    ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    tarjetaNames
                            )
                            .also {
                                it.setDropDownViewResource(
                                        android.R.layout.simple_spinner_dropdown_item
                                )
                            }

            // Pre-fill if editing
            if (gasto != null) {
                etNombre.setText(gasto.nombre)
                etMonto.setText(String.format("%.2f", gasto.monto))
                spinnerMoneda.setSelection(if (gasto.moneda == "USD") 1 else 0)
                etDiaCobro.setText(gasto.diaCobro.toString())

                // Select the matching tarjeta in the spinner
                if (gasto.tarjetaId != null) {
                    val idx = tarjetasActivas.indexOfFirst { it.id == gasto.tarjetaId }
                    if (idx >= 0) spinnerTarjeta.setSelection(idx + 1) // +1 for "Ninguna"
                }
            }
        }

        val dialog =
                AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setCancelable(true)
                        .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancelar.setOnClickListener { dialog.dismiss() }
        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val montoStr = etMonto.text.toString().trim()
            val moneda = if (spinnerMoneda.selectedItemPosition == 1) "USD" else "COR"

            if (nombre.isEmpty()) {
                Toast.makeText(requireContext(), "Ingresa un nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val monto = montoStr.toDoubleOrNull()
            if (monto == null || monto <= 0) {
                Toast.makeText(requireContext(), "Monto debe ser mayor a 0", Toast.LENGTH_SHORT)
                        .show()
                return@setOnClickListener
            }

            val montoRedondeado = Math.round(monto * 100.0) / 100.0

            val diaCobroStr = etDiaCobro.text.toString().trim()
            val diaCobro = diaCobroStr.toIntOrNull() ?: 1
            if (diaCobro < 1 || diaCobro > 31) {
                Toast.makeText(requireContext(), "Día debe ser entre 1 y 31", Toast.LENGTH_SHORT)
                        .show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val tarjetasActivas = db.tarjetaDao().getActivas().first()
                val tarjetaPos = spinnerTarjeta.selectedItemPosition
                val tarjetaId =
                        if (tarjetaPos > 0 && tarjetaPos <= tarjetasActivas.size) {
                            tarjetasActivas[tarjetaPos - 1].id
                        } else null

                if (gasto != null) {
                    db.gastoFijoDao()
                            .update(
                                    gasto.copy(
                                            nombre = nombre,
                                            monto = montoRedondeado,
                                            moneda = moneda,
                                            tarjetaId = tarjetaId,
                                            diaCobro = diaCobro
                                    )
                            )
                } else {
                    db.gastoFijoDao()
                            .insert(
                                    GastoFijo(
                                            nombre = nombre,
                                            monto = montoRedondeado,
                                            moneda = moneda,
                                            tarjetaId = tarjetaId,
                                            diaCobro = diaCobro
                                    )
                            )
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun confirmDeleteGastoFijo(gasto: GastoFijo) {
        AlertDialog.Builder(requireContext())
                .setTitle("Eliminar gasto fijo")
                .setMessage("¿Eliminar \"${gasto.nombre}\"?")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch { db.gastoFijoDao().delete(gasto) }
                }
                .setNegativeButton("Cancelar", null)
                .show()
    }

    // ====================== CATEGORÍAS GASTOS ======================

    // Cache local de categorías para validación
    private var categoriasCache: List<Categoria> = emptyList()

    private fun setupCategorias() {
        categoriaAdapter =
                CategoriaAdapter(
                        onEdit = { showCategoriaDialog(it) },
                        onDelete = { confirmDeleteCategoria(it) }
                )
        binding.rvCategorias.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategorias.adapter = categoriaAdapter

        binding.btnAgregarCategoria.setOnClickListener { showCategoriaDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoriasAll.collectLatest { list: List<Categoria> ->
                categoriasCache = list
                // Ocultar "Gastos Rápidos", "Ingresos Rápidos" y "Apertura" de la configuración
                val visibleList =
                        list.filter {
                            it.nombre != "Gastos Rápidos" &&
                                    it.nombre != "Ingresos Rápidos" &&
                                    it.nombre != "Apertura"
                        }
                categoriaAdapter.submitList(visibleList)
                binding.emptyStateCategorias.visibility =
                        if (visibleList.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showCategoriaDialog(categoria: Categoria?) {
        val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_categoria, null)

        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etIcono = dialogView.findViewById<EditText>(R.id.etIcono)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val rgTipo = dialogView.findViewById<RadioGroup>(R.id.rgTipo)
        val rbGasto = dialogView.findViewById<RadioButton>(R.id.rbGasto)
        val rbIngreso = dialogView.findViewById<RadioButton>(R.id.rbIngreso)
        val rbAmbos = dialogView.findViewById<RadioButton>(R.id.rbAmbos)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardar)

        if (categoria != null) {
            tvTitle.text = "Editar Categoría"
            etNombre.setText(categoria.nombre)
            etIcono.setText(categoria.icono)

            when (categoria.tipo) {
                "GASTO" -> rbGasto.isChecked = true
                "INGRESO" -> rbIngreso.isChecked = true
                else -> rbAmbos.isChecked = true
            }
        } else {
            tvTitle.text = "Nueva Categoría"
            rbAmbos.isChecked = true
        }

        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val icono = etIcono.text.toString().trim().ifEmpty { "🏷️" }

            val tipo =
                    when (rgTipo.checkedRadioButtonId) {
                        R.id.rbGasto -> "GASTO"
                        R.id.rbIngreso -> "INGRESO"
                        else -> "AMBOS"
                    }

            if (nombre.isEmpty()) {
                etNombre.error = "Requerido"
                return@setOnClickListener
            }

            // Validación de duplicados
            val duplicado =
                    categoriasCache.find {
                        it.nombre.equals(nombre, ignoreCase = true) &&
                                it.id != (categoria?.id ?: 0L)
                    }

            if (duplicado != null) {
                if (duplicado.tipo == "AMBOS") {
                    etNombre.error = "Ya existe '${duplicado.nombre}' (Aplica a ambos)"
                    return@setOnClickListener
                }
                if (tipo == "AMBOS") {
                    etNombre.error =
                            "'${duplicado.nombre}' ya existe. No se puede crear como 'Ambos'."
                    return@setOnClickListener
                }
                if (duplicado.tipo == tipo) {
                    etNombre.error = "Ya existe esta categoría en ${tipo.lowercase()}"
                    return@setOnClickListener
                }
            }

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                if (categoria == null) {
                    val nueva = Categoria(nombre = nombre, icono = icono, tipo = tipo)
                    db.categoriaDao().insert(nueva)
                } else {
                    val actualizada = categoria.copy(nombre = nombre, icono = icono, tipo = tipo)
                    db.categoriaDao().update(actualizada)
                }
                withContext(Dispatchers.Main) { dialog.dismiss() }
            }
        }
        dialog.show()
    }

    private fun confirmDeleteCategoria(categoria: Categoria) {
        AlertDialog.Builder(requireContext())
                .setTitle("Eliminar categoría")
                .setMessage("Si tiene gastos asociados, no podrás eliminarla.")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            db.categoriaDao().delete(categoria)
                        } catch (e: SQLiteConstraintException) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                                requireContext(),
                                                "No se puede eliminar: Tiene gastos asociados",
                                                Toast.LENGTH_LONG
                                        )
                                        .show()
                            }
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
    }

    // ====================== BACKUP & RESTORE ======================

    private val createBackupLauncher =
            registerForActivityResult(
                    androidx.activity.result.contract.ActivityResultContracts.CreateDocument(
                            "application/json"
                    )
            ) { uri ->
                uri?.let {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val success =
                                com.example.mylocalbank.utils.BackupManager(requireContext(), db)
                                        .exportData(it)
                        if (success) {
                            Toast.makeText(
                                            requireContext(),
                                            "Respaldo guardado exitosamente",
                                            Toast.LENGTH_LONG
                                    )
                                    .show()
                        } else {
                            Toast.makeText(
                                            requireContext(),
                                            "Error al guardar respaldo",
                                            Toast.LENGTH_LONG
                                    )
                                    .show()
                        }
                    }
                }
            }

    private val restoreBackupLauncher =
            registerForActivityResult(
                    androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
            ) { uri ->
                uri?.let {
                    AlertDialog.Builder(requireContext())
                            .setTitle("Restaurar copia de seguridad")
                            .setMessage(
                                    "⚠️ ALERTA: Esta acción BORRARÁ todos los datos actuales y los reemplazará con los del archivo seleccionado.\n\n¿Estás seguro de continuar?"
                            )
                            .setPositiveButton("RESTAURAR") { _, _ ->
                                viewLifecycleOwner.lifecycleScope.launch {
                                    val success =
                                            com.example.mylocalbank.utils.BackupManager(
                                                            requireContext(),
                                                            db
                                                    )
                                                    .importData(it)
                                    if (success) {
                                        Toast.makeText(
                                                        requireContext(),
                                                        "Datos restaurados correctamente",
                                                        Toast.LENGTH_LONG
                                                )
                                                .show()
                                        // Refresh logic via ViewModel or just restart/notify
                                        // Since we use Flow in adapters, they *should* update
                                        // automatically if tables are cleared and refilled.
                                        // But categories might need a refresh signal if ViewModel
                                        // caches them strongly.
                                        // Ideally, trigger a reload in ViewModel if necessary.
                                    } else {
                                        Toast.makeText(
                                                        requireContext(),
                                                        "Error al restaurar datos. Verifica el archivo.",
                                                        Toast.LENGTH_LONG
                                                )
                                                .show()
                                    }
                                }
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                }
            }

    private fun setupBackupRestore() {
        // ... (existing backup code) ...
        binding.btnBackup.setOnClickListener {
            val fileName =
                    "finanzas_backup_${java.text.SimpleDateFormat("yyyyMMdd_HHmm", java.util.Locale.getDefault()).format(java.util.Date())}.json"
            createBackupLauncher.launch(fileName)
        }

        binding.btnRestore.setOnClickListener {
            restoreBackupLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
        }

        // BIOMETRIC LOGIC
        val switchBiometric =
                binding.root.findViewById<
                        com.google.android.material.switchmaterial.SwitchMaterial>(
                        R.id.switchBiometric
                ) // Safe lookup if binding not updated yet

        // 1. Check availability
        val canAuth =
                com.example.mylocalbank.utils.SecurityManager.canAuthenticate(requireContext())
        switchBiometric.isEnabled = canAuth
        if (!canAuth) {
            switchBiometric.text = "Biometría no disponible"
        }

        // 2. Set initial state
        switchBiometric.isChecked =
                com.example.mylocalbank.utils.SecurityManager.isBiometricEnabled(requireContext())

        // 3. Listener
        switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            if (!switchBiometric.isPressed)
                    return@setOnCheckedChangeListener // Avoid trigger on programmatic change

            // If turning ON: Require auth to confirm it works
            if (isChecked) {
                com.example.mylocalbank.utils.SecurityManager.authenticate(
                        requireActivity(),
                        onSuccess = {
                            com.example.mylocalbank.utils.SecurityManager.setBiometricEnabled(
                                    requireContext(),
                                    true
                            )
                            Toast.makeText(
                                            requireContext(),
                                            "Seguridad activada",
                                            Toast.LENGTH_SHORT
                                    )
                                    .show()
                        },
                        onError = {
                            switchBiometric.isChecked = false // Revert
                            Toast.makeText(
                                            requireContext(),
                                            "Autenticación fallida",
                                            Toast.LENGTH_SHORT
                                    )
                                    .show()
                        }
                )
            } else {
                // If turning OFF: Just disable (could require auth too, but keeping simple)
                com.example.mylocalbank.utils.SecurityManager.setBiometricEnabled(
                        requireContext(),
                        false
                )
            }
        }
    }

    private fun setupSaldoInicial() {
        val cardSaldo =
                binding.root.findViewById<androidx.cardview.widget.CardView>(R.id.cardSaldoInicial)
        val btnConfigurar = binding.root.findViewById<TextView>(R.id.btnConfigurarSaldo)

        if (cardSaldo == null || btnConfigurar == null) return

        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                            db.gastoFijoDao().getAll(),
                            viewModel.fuentesAll,
                            db.saldoInicialDao().getSaldoInicial()
                    ) { gastos, fuentes, saldoList ->
                Triple(gastos, fuentes, saldoList.firstOrNull())
            }
                    .collectLatest { (gastos, fuentes, saldo) ->
                        if (gastos.isNotEmpty() && fuentes.isNotEmpty() && saldo == null) {
                            cardSaldo.visibility = View.VISIBLE
                        } else {
                            cardSaldo.visibility = View.GONE
                        }
                    }
        }

        btnConfigurar.setOnClickListener {
            startActivity(Intent(requireContext(), SaldoInicialActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
