package com.example.mylocalbank

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mylocalbank.data.AppDatabase
import com.example.mylocalbank.data.RegistroGasto
import com.example.mylocalbank.widget.BalanceWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuickAddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_add)

        // Set dimensions for dialog feeling (if not handled by theme)
        window.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val etMonto = findViewById<EditText>(R.id.etMonto) // Corrected ID request
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        // Auto-show keyboard
        etMonto.requestFocus()
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        btnGuardar.setOnClickListener {
            val montoStr = etMonto.text.toString().trim()
            if (montoStr.isEmpty()) return@setOnClickListener

            val monto = montoStr.toDoubleOrNull()
            if (monto == null || monto <= 0) {
                Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveExpense(monto)
        }
    }

    private fun saveExpense(monto: Double) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            
            val gasto = RegistroGasto(
                monto = monto,
                moneda = "COR", // Default currency
                descripcion = "Gasto Rápido",
                categoriaId = 1, // Default "Otros" or similar
                fecha = System.currentTimeMillis()
            )

            db.registroGastoDao().insert(gasto)

            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Gasto guardado", Toast.LENGTH_SHORT).show()
                
                // Update Balance Widget
                val intent = Intent(applicationContext, BalanceWidgetProvider::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                val ids = AppWidgetManager.getInstance(applicationContext).getAppWidgetIds(
                    ComponentName(applicationContext, BalanceWidgetProvider::class.java)
                )
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                sendBroadcast(intent)

                finish()
            }
        }
    }
}
