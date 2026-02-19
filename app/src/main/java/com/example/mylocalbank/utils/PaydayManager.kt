package com.example.mylocalbank.utils

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.example.mylocalbank.data.AppDatabase
import com.example.mylocalbank.data.FuenteIngreso
import com.example.mylocalbank.data.RegistroIngreso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object PaydayManager {

    fun checkPaydays(activity: FragmentActivity) {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Delay to ensure BiometricPrompt is fully dismissed and UI is ready
                kotlinx.coroutines.delay(500)

                val db = AppDatabase.getDatabase(activity)
                val fuentes = db.fuenteIngresoDao().getTrabajos().first()
                val now = Calendar.getInstance()
                val todayDay = now.get(Calendar.DAY_OF_MONTH)

                // Start/End of current month for checking payments
                val startMonth =
                        Calendar.getInstance()
                                .apply {
                                    set(Calendar.DAY_OF_MONTH, 1)
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                .timeInMillis

                val endMonth =
                        Calendar.getInstance()
                                .apply {
                                    add(Calendar.MONTH, 1)
                                    set(Calendar.DAY_OF_MONTH, 1)
                                    add(Calendar.MILLISECOND, -1)
                                }
                                .timeInMillis

                val pendingSources = mutableListOf<FuenteIngreso>()

                for (fuente in fuentes) {
                    // Check if notification is enabled for this source
                    if (!fuente.notificarCobro) continue

                    // Check if already paid this month
                    val payment =
                            db.registroIngresoDao()
                                    .getPaymentForSource(fuente.id, startMonth, endMonth)
                    if (payment != null) continue // Already paid

                    // Check if today is the day
                    var isPayday = false

                    // 1. Regular Schedule
                    if (fuente.diaIngreso == todayDay) isPayday = true

                    // 2. Postponed Date
                    if (fuente.fechaPostergada != null) {
                        val postponedCal =
                                Calendar.getInstance().apply {
                                    timeInMillis = fuente.fechaPostergada
                                }
                        if (isSameDay(now, postponedCal)) isPayday = true
                    }

                    if (isPayday) {
                        pendingSources.add(fuente)
                    }
                }

                if (pendingSources.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        // Show dialog for the first pending source (one at a time to avoid spam)
                        if (!activity.isFinishing && !activity.isDestroyed) {
                            try {
                                showPaydayDialog(activity, pendingSources.first(), db)
                            } catch (e: Exception) {
                                // Ignore window leak or bad token
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    // Fail silently or log? User needs to know if it's broken, but better not to
                    // crash.
                    // Toast.makeText(activity, "Error verificando pagos: ${e.message}",
                    // Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun showPaydayDialog(
            activity: FragmentActivity,
            fuente: FuenteIngreso,
            db: AppDatabase
    ) {
        AlertDialog.Builder(activity)
                .setTitle("💰 ¡Día de Pago!")
                .setMessage(
                        "Hoy deberías recibir tu pago de: ${fuente.nombre}\n\n¿Ya lo recibiste?"
                )
                .setPositiveButton("Sí, ya cayó") { _, _ ->
                    showConfirmAmountDialog(activity, fuente, db)
                }
                .setNegativeButton("Aún no") { _, _ -> showRescheduleDialog(activity, fuente, db) }
                .setCancelable(false)
                .show()
    }

    private fun showConfirmAmountDialog(
            activity: FragmentActivity,
            fuente: FuenteIngreso,
            db: AppDatabase
    ) {
        // Simple amount input dialog

        // We reuse the layout but will hide unnecessary fields programmatically or just pre-fill
        // strictly
        // For simplicity/UX, let's just use a clean custom view or just the standard dialog
        // pre-filled?
        // User said: "abre otro modalsito que me dice el monto que deberia caer ... si das click a
        // si"

        // Let's reuse the standard dialog logic via a direct insert for cleaner UX or a small
        // Confirm Dialog
        // "Expected: 5000. Is this correct?" [Yes] [Edit]
        // User requested: "abre otro modalsito que me dice el monto que deberia caer (segun lo
        // configurado o cayo mas)"

        val input =
                EditText(activity).apply {
                    setText(fuente.monto.toString())
                    inputType =
                            android.text.InputType.TYPE_CLASS_NUMBER or
                                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                }

        AlertDialog.Builder(activity)
                .setTitle("Confirmar Monto")
                .setMessage("Monto esperado: ${fuente.monto} ${fuente.moneda}")
                .setView(input) // Let them edit if "cayo mas"
                .setPositiveButton("Confirmar") { _, _ ->
                    val amount = input.text.toString().toDoubleOrNull() ?: fuente.monto
                    savePayment(activity, fuente, amount, db)
                }
                .setNegativeButton("Cancelar", null)
                .show()
    }

    private fun savePayment(
            activity: FragmentActivity,
            fuente: FuenteIngreso,
            amount: Double,
            db: AppDatabase
    ) {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            // Find "Trabajo" category or fallback
            // We need a category ID. We'll search for one or default to 1
            // Ideally we should have linked Fuente to Category, but for now we search for "Trabajo"
            // or "Sueldo"
            val cats = db.categoriaDao().getAllSync() // We need a sync method or use flow.first()
            val trabajoCat =
                    cats.find {
                        it.nombre.contains("Trabajo", ignoreCase = true) ||
                                it.nombre.contains("Sueldo", ignoreCase = true)
                    }
                            ?: cats.find { it.nombre == "Ingresos Rápidos" } ?: cats.firstOrNull()

            val catId = trabajoCat?.id ?: 1L

            val registro =
                    RegistroIngreso(
                            monto = amount,
                            moneda = fuente.moneda,
                            descripcion = "Pago: ${fuente.nombre}",
                            fecha = System.currentTimeMillis(),
                            categoriaId = catId,
                            fuenteId = fuente.id
                    )

            db.registroIngresoDao().insert(registro)

            // Clear postponed date if any
            if (fuente.fechaPostergada != null) {
                db.fuenteIngresoDao().update(fuente.copy(fechaPostergada = null))
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(activity, "¡Pago registrado!", Toast.LENGTH_SHORT).show()
                // Check if there are MORE pending paydays (recursion)
                checkPaydays(activity)
            }
        }
    }

    private fun showRescheduleDialog(
            activity: FragmentActivity,
            fuente: FuenteIngreso,
            db: AppDatabase
    ) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
                        activity,
                        { _, year, month, day ->
                            val newDate = Calendar.getInstance().apply { set(year, month, day) }
                            activity.lifecycleScope.launch(Dispatchers.IO) {
                                db.fuenteIngresoDao()
                                        .update(fuente.copy(fechaPostergada = newDate.timeInMillis))
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                                    activity,
                                                    "Te recordaremos el ${SimpleDateFormat("dd/MM", Locale.getDefault()).format(newDate.time)}",
                                                    Toast.LENGTH_SHORT
                                            )
                                            .show()
                                }
                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                )
                .show()
    }
}
