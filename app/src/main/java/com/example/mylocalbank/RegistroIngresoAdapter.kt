package com.example.mylocalbank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocalbank.data.RegistroIngreso
import com.example.mylocalbank.data.Categoria
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroIngresoAdapter(
    private val onEdit: (RegistroIngreso) -> Unit,
    private val onDelete: (RegistroIngreso) -> Unit,
    private val getCategoria: (Long) -> Categoria?
) : ListAdapter<RegistroIngreso, RegistroIngresoAdapter.ViewHolder>(DiffCallback()) {

    private val decimalFormat = DecimalFormat("#,##0.00")
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale("es", "NI"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro_ingreso, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvIcono: TextView = itemView.findViewById(R.id.tvIcono)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        private val btnEliminar: TextView = itemView.findViewById(R.id.btnEliminar)

        fun bind(registro: RegistroIngreso) {
            // Icon - show category icon
            val categoria = getCategoria(registro.categoriaId)
            tvIcono.text = categoria?.icono ?: "💰"

            tvDescripcion.text = if (registro.descripcion.isNotEmpty()) registro.descripcion else "Ingreso"
            tvFecha.text = dateFormat.format(Date(registro.fecha))

            val symbol = if (registro.moneda == "USD") "$" else "C$"
            tvMonto.text = "+$symbol${decimalFormat.format(registro.monto)}"

            itemView.setOnClickListener { onEdit(registro) }
            btnEliminar.setOnClickListener { onDelete(registro) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RegistroIngreso>() {
        override fun areItemsTheSame(a: RegistroIngreso, b: RegistroIngreso) = a.id == b.id
        override fun areContentsTheSame(a: RegistroIngreso, b: RegistroIngreso) = a == b
    }
}
