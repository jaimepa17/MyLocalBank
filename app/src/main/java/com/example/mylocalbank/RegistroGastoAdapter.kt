package com.example.mylocalbank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocalbank.data.RegistroGasto
import com.example.mylocalbank.data.Tarjeta
import com.example.mylocalbank.data.Categoria
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroGastoAdapter(
    private val onEdit: (RegistroGasto) -> Unit,
    private val onDelete: (RegistroGasto) -> Unit,
    private val getTarjeta: (Long?) -> Tarjeta?,
    private val getCategoria: (Long) -> Categoria?
) : ListAdapter<RegistroGasto, RegistroGastoAdapter.ViewHolder>(DiffCallback()) {

    private val decimalFormat = DecimalFormat("#,##0.00")
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale("es", "NI"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro_gasto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvIcono: TextView = itemView.findViewById(R.id.tvIcono)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val tvTienda: TextView = itemView.findViewById(R.id.tvTienda)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        private val btnEliminar: TextView = itemView.findViewById(R.id.btnEliminar)

        fun bind(registro: RegistroGasto) {
            // Icon - show card icon if linked, else default
            // Icon - show category icon
            val categoria = getCategoria(registro.categoriaId)
            tvIcono.text = categoria?.icono ?: "💸"

            // Description
            tvDescripcion.text = if (registro.descripcion.isNotEmpty()) registro.descripcion else "Gasto"

            // Tienda
            if (registro.tienda.isNotEmpty()) {
                tvTienda.text = registro.tienda
                tvTienda.visibility = View.VISIBLE
            } else {
                tvTienda.visibility = View.GONE
            }

            // Date
            tvFecha.text = dateFormat.format(Date(registro.fecha))

            // Amount
            val symbol = if (registro.moneda == "USD") "$" else "C$"
            tvMonto.text = "-$symbol${decimalFormat.format(registro.monto)}"

            // Clicks
            itemView.setOnClickListener { onEdit(registro) }
            btnEliminar.setOnClickListener { onDelete(registro) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RegistroGasto>() {
        override fun areItemsTheSame(a: RegistroGasto, b: RegistroGasto) = a.id == b.id
        override fun areContentsTheSame(a: RegistroGasto, b: RegistroGasto) = a == b
    }
}
