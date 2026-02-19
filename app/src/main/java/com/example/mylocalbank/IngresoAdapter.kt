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

class IngresoAdapter : ListAdapter<FuenteIngreso, IngresoAdapter.ViewHolder>(DiffCallback()) {

    private val decimalFormat = DecimalFormat("#,##0.00")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingreso, parent, false)
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
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val layoutMonto: LinearLayout = itemView.findViewById(R.id.layoutMonto)
        private val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        private val tvMoneda: TextView = itemView.findViewById(R.id.tvMoneda)

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

            // Description
            if (fuente.descripcion.isNotEmpty()) {
                tvDescripcion.text = fuente.descripcion
                tvDescripcion.visibility = View.VISIBLE
            } else {
                tvDescripcion.visibility = View.GONE
            }

            // Amount + day for TRABAJO
            if (fuente.monto > 0) {
                layoutMonto.visibility = View.VISIBLE
                val symbol = if (fuente.moneda == "USD") "$" else "C$"
                tvMonto.text = "$symbol${decimalFormat.format(fuente.monto)}"
                tvMoneda.text = fuente.moneda

                if (fuente.diaIngreso != null) {
                    tvDia.visibility = View.VISIBLE
                    tvDia.text = "  •  Día ${fuente.diaIngreso}"
                } else {
                    tvDia.visibility = View.GONE
                }
            } else {
                layoutMonto.visibility = View.GONE
                tvDia.visibility = View.GONE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FuenteIngreso>() {
        override fun areItemsTheSame(a: FuenteIngreso, b: FuenteIngreso) = a.id == b.id
        override fun areContentsTheSame(a: FuenteIngreso, b: FuenteIngreso) = a == b
    }
}
