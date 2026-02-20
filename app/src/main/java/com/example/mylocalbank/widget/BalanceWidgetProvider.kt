package com.example.mylocalbank.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.mylocalbank.MainActivity
import com.example.mylocalbank.R
import com.example.mylocalbank.data.AppDatabase
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BalanceWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
    ) {
        // Launch Main App on click
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val views = RemoteViews(context.packageName, R.layout.widget_balance)
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        // Use goAsync to keep receiver alive
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val (start, end) = getMonthRange()

                // Correct Flow usage
                val ingresos = db.registroIngresoDao().getByDateRange(start, end).first()
                val gastos = db.registroGastoDao().getByDateRange(start, end).first()
                val saldo = db.saldoInicialDao().getSaldoInicial().first()

                val totalIngresos =
                        ingresos.sumOf { if (it.moneda == "USD") it.monto * 36.72 else it.monto }
                val totalGastos =
                        gastos.sumOf { if (it.moneda == "USD") it.monto * 36.72 else it.monto }

                val saldoBase = saldo?.monto ?: 0.0
                val balance = saldoBase + totalIngresos - totalGastos

                withContext(Dispatchers.Main) {
                    val nf =
                            NumberFormat.getNumberInstance(Locale("es", "NI")).apply {
                                minimumFractionDigits = 2
                                maximumFractionDigits = 2
                            }

                    views.setTextViewText(R.id.tvBalance, "C$ ${nf.format(balance)}")
                    views.setTextViewText(R.id.tvIngresos, "+${nf.format(totalIngresos)}")
                    views.setTextViewText(R.id.tvGastos, "-${nf.format(totalGastos)}")

                    // Color logic
                    val balColor = if (balance >= 0) "#FF0F7A3F" else "#D94040"
                    views.setTextColor(R.id.tvBalance, android.graphics.Color.parseColor(balColor))

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

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
}
