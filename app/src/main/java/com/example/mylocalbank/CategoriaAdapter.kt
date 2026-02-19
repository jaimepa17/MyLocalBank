package com.example.mylocalbank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocalbank.data.Categoria
import com.example.mylocalbank.databinding.ItemCategoriaBinding

class CategoriaAdapter(
    private val onEdit: (Categoria) -> Unit,
    private val onDelete: (Categoria) -> Unit
) : ListAdapter<Categoria, CategoriaAdapter.CategoriaViewHolder>(CategoriaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class CategoriaViewHolder(private val binding: ItemCategoriaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Categoria) {
            binding.tvNombre.text = item.nombre
            binding.tvIcono.text = item.icono

            // Prevent Action on "Otros" (System category)
            if (item.esDefault) {
                binding.btnRecycle.visibility = View.GONE
                // Optional: Disable edit too? Or allow rename?
                // User said: "esa categoria incial no se podra añadir ni eliminar"
                // Usually system categories shouldn't be interactable or at least restricted.
                // We'll allow clicking to "edit" (maybe just view) but hide delete.
                binding.root.setOnClickListener { /* Maybe show simplified dialog? */ }
            } else {
                binding.btnRecycle.visibility = View.VISIBLE
                binding.btnRecycle.setOnClickListener { onDelete(item) }
                binding.root.setOnClickListener { onEdit(item) }
            }
        }
    }

    class CategoriaDiffCallback : DiffUtil.ItemCallback<Categoria>() {
        override fun areItemsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
            return oldItem == newItem
        }
    }
}
