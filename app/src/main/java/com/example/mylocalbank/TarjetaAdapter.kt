package com.example.mylocalbank

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocalbank.data.Tarjeta

class TarjetaAdapter(
    private val onEdit: (Tarjeta) -> Unit,
    private val onDelete: (Tarjeta) -> Unit
) : ListAdapter<Tarjeta, TarjetaAdapter.TarjetaViewHolder>(TarjetaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarjetaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarjeta, parent, false)
        return TarjetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TarjetaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TarjetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewColor: View = itemView.findViewById(R.id.viewColor)
        private val tvAlias: TextView = itemView.findViewById(R.id.tvAlias)
        private val tvBanco: TextView = itemView.findViewById(R.id.tvBanco)
        private val tvUltimos4: TextView = itemView.findViewById(R.id.tvUltimos4)
        private val tvTipo: TextView = itemView.findViewById(R.id.tvTipo)
        private val btnDelete: TextView = itemView.findViewById(R.id.btnDelete)

        fun bind(tarjeta: Tarjeta) {
            tvAlias.text = tarjeta.alias
            tvBanco.text = tarjeta.banco
            tvUltimos4.text = "••${tarjeta.ultimos4}"
            tvTipo.text = tarjeta.tipo

            try {
                viewColor.setBackgroundColor(Color.parseColor(tarjeta.color))
            } catch (e: Exception) {
                viewColor.setBackgroundColor(Color.parseColor("#0F9B58"))
            }

            // Tap card to edit
            itemView.setOnClickListener { onEdit(tarjeta) }

            // Delete
            btnDelete.setOnClickListener { onDelete(tarjeta) }
        }
    }

    class TarjetaDiffCallback : DiffUtil.ItemCallback<Tarjeta>() {
        override fun areItemsTheSame(oldItem: Tarjeta, newItem: Tarjeta) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Tarjeta, newItem: Tarjeta) = oldItem == newItem
    }
}
