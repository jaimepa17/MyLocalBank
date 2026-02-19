package com.example.mylocalbank

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mylocalbank.data.AppDatabase
import com.example.mylocalbank.data.FuenteIngreso
import com.example.mylocalbank.databinding.FragmentHomeBinding
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    companion object {
        const val USD_TO_COR = 36.72
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var db: AppDatabase

    private val chartColors by lazy {
        listOf(
                ContextCompat.getColor(requireContext(), R.color.chart_1),
                ContextCompat.getColor(requireContext(), R.color.chart_2),
                ContextCompat.getColor(requireContext(), R.color.chart_3),
                ContextCompat.getColor(requireContext(), R.color.chart_4),
                ContextCompat.getColor(requireContext(), R.color.chart_5),
                ContextCompat.getColor(requireContext(), R.color.chart_6),
                ContextCompat.getColor(requireContext(), R.color.chart_7),
                ContextCompat.getColor(requireContext(), R.color.chart_8)
        )
    }

    private val nf =
            NumberFormat.getNumberInstance(Locale("es", "NI")).apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }

    private val insightTitles =
            arrayOf(
                    "🥧 GASTOS POR CATEGORÍA",
                    "🥧 INGRESOS POR CATEGORÍA",
                    "🏆 TOP INGRESOS DEL MES",
                    "🏆 TOP GASTOS DEL MES",
                    "💳 GASTOS POR TARJETA",
                    "💼 DISTRIBUCIÓN FIJOS"
            )

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getDatabase(requireContext())

        setupBarChart()
        setupInsightsCarousel()
        observeData()
    }

    // ─── Currency helper ─────────────────────────────────────────────

    /** Convert any amount to COR based on its moneda */
    private fun toCor(monto: Double, moneda: String): Double {
        return if (moneda == "USD") monto * USD_TO_COR else monto
    }

    /** Convert COR amount to USD */
    private fun corToUsd(corAmount: Double): Double {
        return corAmount / USD_TO_COR
    }

    // ─── Time helpers ────────────────────────────────────────────────

    private fun getMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val end = calendar.timeInMillis
        return Pair(start, end)
    }

    private fun getEffectiveDayForCurrentMonth(): Int {
        val cal = Calendar.getInstance()
        val today = cal.get(Calendar.DAY_OF_MONTH)
        val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        return minOf(today, lastDay)
    }

    // ─── Bar Chart config ────────────────────────────────────────────

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setFitBars(true)
            setPinchZoom(false)
            setScaleEnabled(false)
            setTouchEnabled(false)
            setExtraOffsets(8f, 8f, 8f, 8f)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                typeface = Typeface.MONOSPACE
                textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
                textSize = 10f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(requireContext(), R.color.card_border_subtle)
                axisMinimum = 0f
                typeface = Typeface.MONOSPACE
                textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
                textSize = 9f
            }

            axisRight.isEnabled = false

            legend.apply {
                isEnabled = true
                typeface = Typeface.MONOSPACE
                textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
                textSize = 10f
                form = Legend.LegendForm.SQUARE
            }

            animateY(600)
        }
    }

    // ─── Insights Carousel ──────────────────────────────────────────

    private fun setupInsightsCarousel() {
        binding.insightsViewPager!!.adapter = InsightsAdapter()
        setupDots(0)

        binding.insightsViewPager!!.registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        setupDots(position)
                        binding.tvInsightTitle!!.text = insightTitles[position]
                    }
                }
        )

        binding.tvInsightTitle!!.text = insightTitles[0]
    }

    private fun setupDots(selectedPosition: Int) {
        binding.dotsIndicator!!.removeAllViews()
        val dp = resources.displayMetrics.density

        for (i in 0 until 6) {
            val dot =
                    TextView(requireContext()).apply {
                        text = if (i == selectedPosition) "●" else "○"
                        textSize = 12f
                        setTextColor(
                                if (i == selectedPosition)
                                        ContextCompat.getColor(requireContext(), R.color.gold)
                                else
                                        ContextCompat.getColor(
                                                requireContext(),
                                                R.color.text_secondary
                                        )
                        )
                        layoutParams =
                                LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        .apply {
                                            marginStart = (4 * dp).toInt()
                                            marginEnd = (4 * dp).toInt()
                                        }
                    }
            binding.dotsIndicator!!.addView(dot)
        }
    }

    inner class InsightsAdapter : RecyclerView.Adapter<InsightsAdapter.InsightVH>() {
        override fun getItemCount() = 6
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsightVH {
            val container =
                    LinearLayout(parent.context).apply {
                        orientation = LinearLayout.VERTICAL
                        layoutParams =
                                ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                )
                        gravity = Gravity.CENTER
                    }
            return InsightVH(container)
        }

        override fun onBindViewHolder(holder: InsightVH, position: Int) {
            holder.bind(position)
        }

        inner class InsightVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(position: Int) {
                val container = itemView as LinearLayout
                container.removeAllViews()

                when (position) {
                    0 -> buildCategoryExpensePage(container) // #1 Priority
                    1 -> buildCategoryIncomePage(container)
                    2 -> buildTopIncomePage(container)
                    3 -> buildTopExpensesPage(container)
                    4 -> buildCardExpensePage(container)
                    5 -> buildFixedDistributionPage(container)
                }
            }
        }
    }

    // ─── Page 1: Gastos por Categoría (Pie) ─────────────────────────

    private fun buildCategoryExpensePage(container: LinearLayout) {
        val pieChart =
                PieChart(requireContext()).apply {
                    layoutParams =
                            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
                    description.isEnabled = false
                    setUsePercentValues(true)
                    setDrawEntryLabels(false)
                    isDrawHoleEnabled = true
                    holeRadius = 45f
                    transparentCircleRadius = 50f
                    setHoleColor(ContextCompat.getColor(requireContext(), R.color.bg_card))
                    setCenterTextTypeface(Typeface.MONOSPACE)
                    setCenterTextSize(10f)
                    setCenterTextColor(
                            ContextCompat.getColor(requireContext(), R.color.text_primary)
                    )
                    setTouchEnabled(false)
                    setExtraOffsets(4f, 4f, 4f, 4f)
                    animateY(600)

                    legend.apply {
                        isEnabled = true
                        typeface = Typeface.MONOSPACE
                        textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
                        textSize = 9f
                        form = Legend.LegendForm.CIRCLE
                        verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                        horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                        orientation = Legend.LegendOrientation.HORIZONTAL
                        setDrawInside(false)
                        isWordWrapEnabled = true
                        yOffset = 4f
                        xEntrySpace = 10f
                    }
                }
        container.addView(pieChart)

        viewLifecycleOwner.lifecycleScope.launch {
            val (start, end) = getMonthRange()
            val registros = db.registroGastoDao().getByDateRange(start, end).first()

            if (registros.isEmpty()) {
                pieChart.clear()
                pieChart.centerText = "Sin gastos"
                return@launch
            }

            val categorias = db.categoriaDao().getAll().first()

            // Group by category
            val grouped = registros.groupBy { it.categoriaId }
            val entries =
                    grouped
                            .mapNotNull { (catId, items) ->
                                val cat =
                                        categorias.find { it.id == catId } ?: return@mapNotNull null
                                val total = items.sumOf { toCor(it.monto, it.moneda) }
                                if (total > 0) PieEntry(total.toFloat(), cat.nombre) else null
                            }
                            .sortedByDescending { it.value }

            val dataSet =
                    PieDataSet(entries, "").apply {
                        colors = chartColors.take(entries.size.coerceAtMost(chartColors.size))
                        sliceSpace = 2f
                        valueTextSize = 10f
                        valueTypeface = Typeface.MONOSPACE
                        valueTextColor = Color.WHITE
                        valueFormatter = PercentFormatter(pieChart)
                    }

            pieChart.apply {
                data = PieData(dataSet)
                val total = registros.sumOf { toCor(it.monto, it.moneda) }
                centerText = "Gastos\nC$ ${nf.format(total)}"
                invalidate()
            }
        }
    }

    // ─── Page 2: Ingresos por Categoría (Pie) ───────────────────────

    private fun buildCategoryIncomePage(container: LinearLayout) {
        val pieChart =
                PieChart(requireContext()).apply {
                    layoutParams =
                            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
                    description.isEnabled = false
                    setUsePercentValues(true)
                    setDrawEntryLabels(false)
                    isDrawHoleEnabled = true
                    holeRadius = 45f
                    transparentCircleRadius = 50f
                    setHoleColor(ContextCompat.getColor(requireContext(), R.color.bg_card))
                    setCenterTextTypeface(Typeface.MONOSPACE)
                    setCenterTextSize(10f)
                    setCenterTextColor(
                            ContextCompat.getColor(requireContext(), R.color.text_primary)
                    )
                    setTouchEnabled(false)
                    setExtraOffsets(4f, 4f, 4f, 4f)
                    animateY(600)

                    legend.apply {
                        isEnabled = true
                        typeface = Typeface.MONOSPACE
                        textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
                        textSize = 9f
                        form = Legend.LegendForm.CIRCLE
                        verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                        horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                        orientation = Legend.LegendOrientation.HORIZONTAL
                        setDrawInside(false)
                        isWordWrapEnabled = true
                        yOffset = 4f
                        xEntrySpace = 10f
                    }
                }
        container.addView(pieChart)

        viewLifecycleOwner.lifecycleScope.launch {
            val (start, end) = getMonthRange()
            val registros = db.registroIngresoDao().getByDateRange(start, end).first()

            if (registros.isEmpty()) {
                pieChart.clear()
                pieChart.centerText = "Sin ingresos"
                return@launch
            }

            val categorias = db.categoriaDao().getAll().first()

            // Group by category
            val grouped = registros.groupBy { it.categoriaId }
            val entries =
                    grouped
                            .mapNotNull { (catId, items) ->
                                val cat =
                                        categorias.find { it.id == catId } ?: return@mapNotNull null
                                val total = items.sumOf { toCor(it.monto, it.moneda) }
                                if (total > 0) PieEntry(total.toFloat(), cat.nombre) else null
                            }
                            .sortedByDescending { it.value }

            val dataSet =
                    PieDataSet(entries, "").apply {
                        colors = chartColors.take(entries.size.coerceAtMost(chartColors.size))
                        sliceSpace = 2f
                        valueTextSize = 10f
                        valueTypeface = Typeface.MONOSPACE
                        valueTextColor = Color.WHITE
                        valueFormatter = PercentFormatter(pieChart)
                    }

            pieChart.apply {
                data = PieData(dataSet)
                val total = registros.sumOf { toCor(it.monto, it.moneda) }
                centerText = "Ingresos\nC$ ${nf.format(total)}"
                invalidate()
            }
        }
    }

    // ─── Page 3: Top Ingresos del Mes (List) ────────────────────────

    private fun buildTopIncomePage(container: LinearLayout) {
        val dp = resources.displayMetrics.density

        viewLifecycleOwner.lifecycleScope.launch {
            val (start, end) = getMonthRange()
            val registros =
                    db.registroIngresoDao()
                            .getByDateRange(start, end)
                            .first()
                            .sortedByDescending { toCor(it.monto, it.moneda) }
                            .take(5)

            if (registros.isEmpty()) {
                val emptyTv =
                        TextView(requireContext()).apply {
                            text = "🏆\nSin ingresos este mes"
                            typeface = Typeface.MONOSPACE
                            textSize = 12f
                            setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.text_secondary)
                            )
                            gravity = Gravity.CENTER
                            layoutParams =
                                    LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            0,
                                            1f
                                    )
                        }
                container.addView(emptyTv)
                return@launch
            }

            registros.forEachIndexed { index, reg ->
                val row =
                        LinearLayout(requireContext()).apply {
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER_VERTICAL
                            setPadding(
                                    (8 * dp).toInt(),
                                    (10 * dp).toInt(),
                                    (8 * dp).toInt(),
                                    (10 * dp).toInt()
                            )
                            layoutParams =
                                    LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                        }

                // Rank badge
                val rankColors = listOf("#FFD700", "#C0C0C0", "#CD7F32", "#888888", "#888888")
                row.addView(
                        TextView(requireContext()).apply {
                            text = "#${index + 1}"
                            typeface = Typeface.MONOSPACE
                            textSize = 13f
                            setTextColor(
                                    Color.parseColor(
                                            if (index < rankColors.size) rankColors[index]
                                            else "#888888"
                                    )
                            )
                            setTypeface(typeface, Typeface.BOLD)
                            layoutParams =
                                    LinearLayout.LayoutParams(
                                            (32 * dp).toInt(),
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                        }
                )

                // Description
                val desc = if (reg.descripcion.isNotEmpty()) reg.descripcion else "Ingreso"

                val infoLayout =
                        LinearLayout(requireContext()).apply {
                            orientation = LinearLayout.VERTICAL
                            layoutParams =
                                    LinearLayout.LayoutParams(
                                            0,
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            1f
                                    )
                        }

                infoLayout.addView(
                        TextView(requireContext()).apply {
                            text = desc
                            typeface = Typeface.MONOSPACE
                            textSize = 11f
                            setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.text_primary)
                            )
                            maxLines = 1
                        }
                )

                row.addView(infoLayout)

                // Amount — converted to COR
                val corAmount = toCor(reg.monto, reg.moneda)
                row.addView(
                        TextView(requireContext()).apply {
                            text = "+C$ ${nf.format(corAmount)}"
                            typeface = Typeface.MONOSPACE
                            textSize = 11f
                            setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.green_accent)
                            )
                            setTypeface(typeface, Typeface.BOLD)
                        }
                )

                container.addView(row)
            }
        }
    }

    // ─── Page 4: Top Gastos del Mes (List) ──────────────────────────
    // (Renamed/Reordered Existing Function)

    private fun buildTopExpensesPage(container: LinearLayout) {
        val dp = resources.displayMetrics.density

        viewLifecycleOwner.lifecycleScope.launch {
            val (start, end) = getMonthRange()
            val registros =
                    db.registroGastoDao()
                            .getByDateRange(start, end)
                            .first()
                            .sortedByDescending { toCor(it.monto, it.moneda) }
                            .take(5)

            if (registros.isEmpty()) {
                val emptyTv =
                        TextView(requireContext()).apply {
                            text = "🏆\nSin gastos este mes"
                            typeface = Typeface.MONOSPACE
                            textSize = 12f
                            setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.text_secondary)
                            )
                            gravity = Gravity.CENTER
                            layoutParams =
                                    LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            0,
                                            1f
                                    )
                        }
                container.addView(emptyTv)
                return@launch
            }

            val tarjetas = db.tarjetaDao().getAll().first()

            registros.forEachIndexed { index, reg ->
                val row =
                        LinearLayout(requireContext()).apply {
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER_VERTICAL
                            setPadding(
                                    (8 * dp).toInt(),
                                    (10 * dp).toInt(),
                                    (8 * dp).toInt(),
                                    (10 * dp).toInt()
                            )
                            layoutParams =
                                    LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                        }

                // Rank badge
                val rankColors = listOf("#FFD700", "#C0C0C0", "#CD7F32", "#888888", "#888888")
                row.addView(
                        TextView(requireContext()).apply {
                            text = "#${index + 1}"
                            typeface = Typeface.MONOSPACE
                            textSize = 13f
                            setTextColor(
                                    Color.parseColor(
                                            if (index < rankColors.size) rankColors[index]
                                            else "#888888"
                                    )
                            )
                            setTypeface(typeface, Typeface.BOLD)
                            layoutParams =
                                    LinearLayout.LayoutParams(
                                            (32 * dp).toInt(),
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                        }
                )

                // Description
                val desc = if (reg.descripcion.isNotEmpty()) reg.descripcion else "Gasto"
                val tarjeta = tarjetas.find { it.id == reg.tarjetaId }
                val sub =
                        if (reg.tienda.isNotEmpty()) reg.tienda
                        else if (tarjeta != null) "💳 ${tarjeta.alias}" else ""

                val infoLayout =
                        LinearLayout(requireContext()).apply {
                            orientation = LinearLayout.VERTICAL
                            layoutParams =
                                    LinearLayout.LayoutParams(
                                            0,
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            1f
                                    )
                        }

                infoLayout.addView(
                        TextView(requireContext()).apply {
                            text = desc
                            typeface = Typeface.MONOSPACE
                            textSize = 11f
                            setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.text_primary)
                            )
                            maxLines = 1
                        }
                )

                if (sub.isNotEmpty()) {
                    infoLayout.addView(
                            TextView(requireContext()).apply {
                                text = sub
                                typeface = Typeface.MONOSPACE
                                textSize = 9f
                                setTextColor(
                                        ContextCompat.getColor(
                                                requireContext(),
                                                R.color.text_secondary
                                        )
                                )
                                maxLines = 1
                            }
                    )
                }

                row.addView(infoLayout)

                // Amount — converted to COR
                val corAmount = toCor(reg.monto, reg.moneda)
                row.addView(
                        TextView(requireContext()).apply {
                            text = "-C$ ${nf.format(corAmount)}"
                            typeface = Typeface.MONOSPACE
                            textSize = 11f
                            setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.red_accent)
                            )
                            setTypeface(typeface, Typeface.BOLD)
                        }
                )

                container.addView(row)
            }
        }
    }

    // ─── Page 5: Gastos por Tarjeta ─────────────────────────────────

    private fun buildCardExpensePage(container: LinearLayout) {
        val chartView =
                HorizontalBarChart(requireContext()).apply {
                    layoutParams =
                            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
                    description.isEnabled = false
                    setDrawGridBackground(false)
                    setFitBars(true)
                    setTouchEnabled(false)
                    setDrawValueAboveBar(false) // Fix: values inside bars so they don't clip
                    setExtraOffsets(8f, 8f, 60f, 8f) // Extra right offset for value labels
                    animateX(600)

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        typeface = Typeface.MONOSPACE
                        textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
                        textSize = 9f
                        granularity = 1f
                    }

                    axisLeft.apply {
                        axisMinimum = 0f
                        setDrawGridLines(true)
                        gridColor =
                                ContextCompat.getColor(requireContext(), R.color.card_border_subtle)
                        typeface = Typeface.MONOSPACE
                        textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
                        textSize = 9f
                    }
                    axisRight.isEnabled = false
                    legend.isEnabled = false
                }

        container.addView(chartView)

        // Load data
        viewLifecycleOwner.lifecycleScope.launch {
            val (start, end) = getMonthRange()
            val registros = db.registroGastoDao().getByDateRange(start, end).first()
            val tarjetas = db.tarjetaDao().getAll().first()

            // Group by tarjeta, converting to COR
            data class CardSpend(val name: String, val total: Double)

            val cardSpends = mutableListOf<CardSpend>()

            // Gastos sin tarjeta
            val sinTarjeta =
                    registros.filter { it.tarjetaId == null }.sumOf { toCor(it.monto, it.moneda) }
            if (sinTarjeta > 0) cardSpends.add(CardSpend("Efectivo", sinTarjeta))

            // Gastos por tarjeta
            val grouped = registros.filter { it.tarjetaId != null }.groupBy { it.tarjetaId }
            grouped.forEach { (tarjetaId, items) ->
                val tarjeta = tarjetas.find { it.id == tarjetaId }
                val name = tarjeta?.alias ?: "Tarjeta ?"
                cardSpends.add(CardSpend(name, items.sumOf { toCor(it.monto, it.moneda) }))
            }

            cardSpends.sortByDescending { it.total }

            if (cardSpends.isEmpty()) {
                chartView.visibility = View.GONE
                val emptyTv =
                        TextView(requireContext()).apply {
                            text = "💳\nSin gastos registrados"
                            typeface = Typeface.MONOSPACE
                            textSize = 12f
                            setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.text_secondary)
                            )
                            gravity = Gravity.CENTER
                            layoutParams =
                                    LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            0,
                                            1f
                                    )
                        }
                container.addView(emptyTv, 0)
                return@launch
            }

            val entries =
                    cardSpends.mapIndexed { i, cs -> BarEntry(i.toFloat(), cs.total.toFloat()) }

            val dataSet =
                    BarDataSet(entries, "").apply {
                        color =
                                ContextCompat.getColor(
                                        requireContext(),
                                        R.color.red_accent
                                ) // User requested RED for expenses
                        valueTypeface = Typeface.MONOSPACE
                        valueTextSize = 9f
                        valueTextColor = Color.WHITE
                        valueFormatter =
                                object : ValueFormatter() {
                                    override fun getFormattedValue(value: Float): String {
                                        return "C$ ${nf.format(value.toDouble())}"
                                    }
                                }
                    }

            chartView.apply {
                data = BarData(dataSet).apply { barWidth = 0.6f }
                xAxis.valueFormatter = IndexAxisValueFormatter(cardSpends.map { it.name })
                xAxis.labelCount = cardSpends.size
                invalidate()
            }
        }
    }

    // ─── Page 6: Distribución Fijos (Pie) ───────────────────────────

    private fun buildFixedDistributionPage(container: LinearLayout) {
        val pieChart =
                PieChart(requireContext()).apply {
                    layoutParams =
                            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
                    description.isEnabled = false
                    setUsePercentValues(true)
                    setDrawEntryLabels(false)
                    isDrawHoleEnabled = true
                    holeRadius = 45f
                    transparentCircleRadius = 50f
                    setHoleColor(ContextCompat.getColor(requireContext(), R.color.bg_card))
                    setCenterTextTypeface(Typeface.MONOSPACE)
                    setCenterTextSize(11f)
                    setCenterTextColor(
                            ContextCompat.getColor(requireContext(), R.color.text_primary)
                    )
                    setTouchEnabled(false)
                    setExtraOffsets(4f, 4f, 4f, 4f)
                    animateY(800)

                    legend.apply {
                        isEnabled = true
                        typeface = Typeface.MONOSPACE
                        textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
                        textSize = 9f
                        form = Legend.LegendForm.CIRCLE
                        verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                        horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                        orientation = Legend.LegendOrientation.HORIZONTAL
                        setDrawInside(false)
                        yOffset = 8f
                        xEntrySpace = 12f
                    }
                }

        container.addView(pieChart)

        viewLifecycleOwner.lifecycleScope.launch {
            val fijos = db.gastoFijoDao().getActivos().first()

            if (fijos.isEmpty()) {
                pieChart.clear()
                pieChart.centerText = "Sin gastos fijos"
                return@launch
            }

            // Convert to COR for proper comparison
            val entries = fijos.map { PieEntry(toCor(it.monto, it.moneda).toFloat(), it.nombre) }
            val dataSet =
                    PieDataSet(entries, "").apply {
                        colors = chartColors.take(entries.size.coerceAtMost(chartColors.size))
                        sliceSpace = 2f
                        valueTextSize = 10f
                        valueTypeface = Typeface.MONOSPACE
                        valueTextColor = Color.WHITE
                        valueFormatter = PercentFormatter(pieChart)
                    }

            pieChart.apply {
                data = PieData(dataSet)
                val total = fijos.sumOf { toCor(it.monto, it.moneda) }
                centerText = "Total\nC$ ${nf.format(total)}"
                invalidate()
            }
        }
    }

    private fun observeData() {
        val (start, end) = getMonthRange()
        val now = System.currentTimeMillis()
        val todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        // Current year-month for first-month salary logic
        val currentYM =
                Calendar.getInstance().let { it.get(Calendar.YEAR) * 12 + it.get(Calendar.MONTH) }

        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                            db.fuenteIngresoDao().getActivas(),
                            db.registroIngresoDao().getByDateRange(start, end),
                            db.gastoFijoDao().getActivos(), // ALL active, not filtered by day
                            db.registroGastoDao().getByDateRange(start, end)
                    ) { fuentes, regIngresos, gastosFijos, regGastos ->

                // ── INCOME: fixed sources ──
                var ingresosEfectivos = 0.0
                var ingresosPendientes = 0.0
                for (f in fuentes) {
                    val cor = toCor(f.monto, f.moneda)
                    val dia = f.diaIngreso ?: 1

                    // First-month logic: compare at MONTH level
                    // If source was created in a future month → skip entirely
                    val creationCal =
                            Calendar.getInstance().apply { timeInMillis = f.fechaCreacion }
                    val creationYM =
                            creationCal.get(Calendar.YEAR) * 12 + creationCal.get(Calendar.MONTH)
                    if (creationYM > currentYM) continue // source didn't exist yet

                    // Source exists in this month → use diaIngreso vs today
                    if (dia <= todayDay) {
                        ingresosEfectivos += cor
                    } else {
                        ingresosPendientes += cor
                    }
                }

                // ── INCOME: variable records ──
                for (r in regIngresos) {
                    val cor = toCor(r.monto, r.moneda)
                    if (r.fecha <= now) ingresosEfectivos += cor else ingresosPendientes += cor
                }

                // ── EXPENSES: fixed ──
                var gastosEfectivos = 0.0
                var gastosPendientes = 0.0
                for (g in gastosFijos) {
                    val cor = toCor(g.monto, g.moneda)
                    if (g.diaCobro <= todayDay) gastosEfectivos += cor else gastosPendientes += cor
                }

                // ── EXPENSES: variable records ──
                for (r in regGastos) {
                    val cor = toCor(r.monto, r.moneda)
                    if (r.fecha <= now) gastosEfectivos += cor else gastosPendientes += cor
                }

                arrayOf(ingresosEfectivos, ingresosPendientes, gastosEfectivos, gastosPendientes)
            }
                    .collectLatest { totals ->
                        val (ingEfect, ingPend, gasEfect, gasPend) = totals
                        updateBalanceCard(ingEfect, ingPend, gasEfect, gasPend)
                        // Bar chart shows total (effective + pending) for the full monthly picture
                        updateBarChart(ingEfect + ingPend, gasEfect + gasPend)
                    }
        }

        // Income sources list
        viewLifecycleOwner.lifecycleScope.launch {
            db.fuenteIngresoDao().getActivas().collectLatest { list -> updateIngresosResumen(list) }
        }
    }

    // ─── Update balance card ─────────────────────────────────────────

    private fun updateBalanceCard(
            ingresosEfectivos: Double,
            ingresosPendientes: Double,
            gastosEfectivos: Double,
            gastosPendientes: Double
    ) {
        // Saldo actual = only effective amounts
        val balance = ingresosEfectivos - gastosEfectivos
        binding.tvBalance.text = "C$ ${nf.format(balance)}"

        // Show total income/expenses (effective + pending)
        val totalIngresos = ingresosEfectivos + ingresosPendientes
        val totalGastos = gastosEfectivos + gastosPendientes
        binding.tvTotalIngresos.text = "C$ ${nf.format(totalIngresos)}"
        binding.tvTotalGastos.text = "C$ ${nf.format(totalGastos)}"

        // USD equivalent of actual balance
        val balanceUsd = corToUsd(balance)
        binding.tvBalanceUsd.text = "≈ \$${nf.format(balanceUsd)} USD"

        // Pending labels
        binding.tvPendienteIngresar.text =
                "📥 Pendiente ingresar: C$ ${nf.format(ingresosPendientes)}"
        binding.tvPendientePagar.text = "📤 Pendiente a pagar: C$ ${nf.format(gastosPendientes)}"

        val balanceColor =
                if (balance >= 0) Color.WHITE
                else ContextCompat.getColor(requireContext(), R.color.red_accent)
        binding.tvBalance.setTextColor(balanceColor)
    }

    // ─── Update bar chart ────────────────────────────────────────────

    private fun updateBarChart(totalIngresos: Double, totalGastos: Double) {
        val greenColor = ContextCompat.getColor(requireContext(), R.color.green_accent)
        val redColor = ContextCompat.getColor(requireContext(), R.color.red_accent)

        // Single dataset with 2 entries for perfect alignment
        val entries =
                listOf(BarEntry(0f, totalIngresos.toFloat()), BarEntry(1f, totalGastos.toFloat()))

        val dataSet =
                BarDataSet(entries, "").apply {
                    setColors(greenColor, redColor) // 0 -> Green, 1 -> Red
                    valueTypeface = Typeface.MONOSPACE
                    valueTextSize = 10f
                    valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
                    valueFormatter =
                            object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    return "C$ ${nf.format(value.toDouble())}"
                                }
                            }
                }

        binding.barChart.apply {
            data =
                    BarData(dataSet).apply {
                        barWidth = 0.5f // Reasonable width
                    }
            xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Ingresos", "Gastos"))
            xAxis.labelCount = 2
            // No grouping needed for single dataset
            setFitBars(true)
            invalidate()
        }
    }

    // ─── Update income summary list ──────────────────────────────────

    private fun updateIngresosResumen(fuentes: List<FuenteIngreso>) {
        binding.layoutIngresosResumen.removeAllViews()
        binding.tvEmptyIngresos.visibility = if (fuentes.isEmpty()) View.VISIBLE else View.GONE

        fuentes.forEach { fuente ->
            val row =
                    createSummaryRow(
                            icon =
                                    when (fuente.tipo) {
                                        "TRABAJO" -> "💼"
                                        else -> "💰"
                                    },
                            label = fuente.nombre,
                            amount = fuente.monto,
                            moneda = fuente.moneda,
                            isIncome = true
                    )
            binding.layoutIngresosResumen.addView(row)
        }
    }

    // ─── Helper: create a summary row ────────────────────────────────

    private fun createSummaryRow(
            icon: String,
            label: String,
            amount: Double,
            moneda: String,
            isIncome: Boolean
    ): LinearLayout {
        val dp = resources.displayMetrics.density

        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, (6 * dp).toInt(), 0, (6 * dp).toInt())

            addView(
                    TextView(context).apply {
                        text = icon
                        textSize = 16f
                        layoutParams =
                                LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        .apply { marginEnd = (8 * dp).toInt() }
                    }
            )

            addView(
                    TextView(context).apply {
                        text = label
                        typeface = Typeface.MONOSPACE
                        textSize = 11f
                        setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                        layoutParams =
                                LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1f
                                )
                    }
            )

            val prefix = if (moneda == "USD") "$ " else "C$ "
            val amountColor = if (isIncome) R.color.green_text else R.color.red_text
            addView(
                    TextView(context).apply {
                        text = "$prefix${nf.format(amount)}"
                        typeface = Typeface.MONOSPACE
                        textSize = 11f
                        setTextColor(ContextCompat.getColor(context, amountColor))
                        setTypeface(typeface, Typeface.BOLD)
                    }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
