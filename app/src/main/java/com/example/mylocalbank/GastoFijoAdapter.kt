package com.example.mylocalbank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocalbank.data.GastoFijo
import java.text.DecimalFormat

class GastoFijoAdapter(
    private val onEdit: (GastoFijo) -> Unit,
    private val onDelete: (GastoFijo) -> Unit,
    private val getTarjetaAlias: (Long?) -> String?
) : ListAdapter<GastoFijo, GastoFijoAdapter.ViewHolder>(DiffCallback()) {

    private val decimalFormat = DecimalFormat("#,##0.00")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gasto_fijo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvIcono: TextView = itemView.findViewById(R.id.tvIcono)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvTarjeta: TextView = itemView.findViewById(R.id.tvTarjeta)
        private val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        private val tvMoneda: TextView = itemView.findViewById(R.id.tvMoneda)
        private val btnDelete: TextView = itemView.findViewById(R.id.btnDelete)

        fun bind(gasto: GastoFijo) {
            tvIcono.text = "🏠"
            tvNombre.text = gasto.nombre

            val symbol = if (gasto.moneda == "USD") "$" else "C$"
            tvMonto.text = "$symbol${decimalFormat.format(gasto.monto)}"
            tvMoneda.text = gasto.moneda

            // Show tarjeta alias if associated
            val alias = getTarjetaAlias(gasto.tarjetaId)
            if (alias != null) {
                tvTarjeta.text = "💳 $alias"
                tvTarjeta.visibility = View.VISIBLE
            } else {
                tvTarjeta.visibility = View.GONE
            }

            itemView.setOnClickListener { onEdit(gasto) }
            btnDelete.setOnClickListener { onDelete(gasto) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<GastoFijo>() {
        override fun areItemsTheSame(a: GastoFijo, b: GastoFijo) = a.id == b.id
        override fun areContentsTheSame(a: GastoFijo, b: GastoFijo) = a == b
    }
}
