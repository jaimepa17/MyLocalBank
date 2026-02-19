package com.example.mylocalbank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocalbank.data.FuenteIngreso
import java.text.DecimalFormat

class FuenteIngresoAdapter(
    private val onEdit: (FuenteIngreso) -> Unit,
    private val onDelete: (FuenteIngreso) -> Unit
) : ListAdapter<FuenteIngreso, FuenteIngresoAdapter.ViewHolder>(DiffCallback()) {

    private val decimalFormat = DecimalFormat("#,##0.00")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fuente_ingreso, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvIcono: TextView = itemView.findViewById(R.id.tvIcono)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvTipo: TextView = itemView.findViewById(R.id.tvTipo)
        private val tvDia: TextView = itemView.findViewById(R.id.tvDia)
        private val layoutMonto: LinearLayout = itemView.findViewById(R.id.layoutMonto)
        private val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        private val tvMoneda: TextView = itemView.findViewById(R.id.tvMoneda)
        private val btnDelete: TextView = itemView.findViewById(R.id.btnDelete)

        fun bind(fuente: FuenteIngreso) {
            tvNombre.text = fuente.nombre

            // Icon + type label
            when (fuente.tipo) {
                "TRABAJO" -> {
                    tvIcono.text = "💼"
                    tvTipo.text = "Trabajo"
                }
                else -> {
                    tvIcono.text = "💰"
                    tvTipo.text = "Otro"
                }
            }

            // Show amount + day for TRABAJO
            if (fuente.tipo == "TRABAJO" && fuente.monto > 0) {
                layoutMonto.visibility = View.VISIBLE
                val symbol = if (fuente.moneda == "USD") "$" else "C$"
                tvMonto.text = "$symbol${decimalFormat.format(fuente.monto)}"
                tvMoneda.text = fuente.moneda

                tvDia.visibility = View.VISIBLE
                tvDia.text = "  •  Día ${fuente.diaIngreso ?: "?"}"
            } else {
                layoutMonto.visibility = View.GONE
                tvDia.visibility = View.GONE
            }

            itemView.setOnClickListener { onEdit(fuente) }
            btnDelete.setOnClickListener { onDelete(fuente) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FuenteIngreso>() {
        override fun areItemsTheSame(a: FuenteIngreso, b: FuenteIngreso) = a.id == b.id
        override fun areContentsTheSame(a: FuenteIngreso, b: FuenteIngreso) = a == b
    }
}
