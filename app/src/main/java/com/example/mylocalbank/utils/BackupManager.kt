package com.example.mylocalbank.utils

import android.content.Context
import android.net.Uri
import com.example.mylocalbank.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupManager(private val context: Context, private val db: AppDatabase) {

    suspend fun exportData(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject()
            
            // Metadata
            root.put("version", 1)
            root.put("timestamp", System.currentTimeMillis())
            root.put("date", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
            root.put("appVersion", "1.0") // Should allow tracking schema version

            // 1. Categorias
            val categorias = db.categoriaDao().getAllForBackup().first()
            val catArray = JSONArray()
            categorias.forEach { 
                catArray.put(JSONObject().apply {
                    put("id", it.id)
                    put("nombre", it.nombre)
                    put("icono", it.icono)
                    put("esDefault", it.esDefault)
                    put("activa", it.activa)
                    put("tipo", it.tipo)
                })
            }
            root.put("categorias", catArray)

            // 2. Tarjetas
            val tarjetas = db.tarjetaDao().getAll().first()
            val tarArray = JSONArray()
            tarjetas.forEach {
                tarArray.put(JSONObject().apply {
                    put("id", it.id)
                    put("alias", it.alias)
                    put("ultimos4", it.ultimos4)
                    put("tipo", it.tipo)
                    put("banco", it.banco)
                    put("color", it.color)
                    put("activa", it.activa)
                })
            }
            root.put("tarjetas", tarArray)

            // 3. Fuentes
            val fuentes = db.fuenteIngresoDao().getAll().first()
            val fueArray = JSONArray()
            fuentes.forEach {
                fueArray.put(JSONObject().apply {
                    put("id", it.id)
                    put("nombre", it.nombre)
                    put("tipo", it.tipo)
                    put("descripcion", it.descripcion)
                    put("monto", it.monto)
                    put("moneda", it.moneda)
                    put("diaIngreso", it.diaIngreso ?: JSONObject.NULL)
                    put("activa", it.activa)
                })
            }
            root.put("fuentes", fueArray)

            // 4. Gastos Fijos
            val fijos = db.gastoFijoDao().getAll().first()
            val fijArray = JSONArray()
            fijos.forEach {
                fijArray.put(JSONObject().apply {
                    put("id", it.id)
                    put("nombre", it.nombre)
                    put("monto", it.monto)
                    put("moneda", it.moneda)
                    put("tarjetaId", it.tarjetaId ?: JSONObject.NULL)
                    put("diaCobro", it.diaCobro)
                    put("activa", it.activa)
                    put("categoriaId", it.categoriaId)
                })
            }
            root.put("gastosFijos", fijArray)

            // 5. Registros Gastos
            val gastos = db.registroGastoDao().getAll().first() // Need getAll in DAO
            val gasArray = JSONArray()
            gastos.forEach {
                gasArray.put(JSONObject().apply {
                    put("id", it.id)
                    put("monto", it.monto)
                    put("moneda", it.moneda)
                    put("descripcion", it.descripcion)
                    put("tienda", it.tienda)
                    put("tarjetaId", it.tarjetaId ?: JSONObject.NULL)
                    put("fecha", it.fecha)
                    put("categoriaId", it.categoriaId)
                })
            }
            root.put("gastos", gasArray)

            // 6. Registros Ingresos
            val ingresos = db.registroIngresoDao().getAll().first() // Need getAll in DAO
            val ingArray = JSONArray()
            ingresos.forEach {
                ingArray.put(JSONObject().apply {
                    put("id", it.id)
                    put("monto", it.monto)
                    put("moneda", it.moneda)
                    put("descripcion", it.descripcion)
                    put("fecha", it.fecha)
                    put("categoriaId", it.categoriaId)
                })
            }
            root.put("ingresos", ingArray)

            // Write to file
            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(root.toString(2).toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun importData(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val sb = StringBuilder()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        sb.append(line)
                        line = reader.readLine()
                    }
                }
            }

            val root = JSONObject(sb.toString())

            // Clear DB Tables (Order matters for FKs)
            // Delete Children first: Transactions, Fixed Expenses
            db.registroGastoDao().deleteAll()
            db.registroIngresoDao().deleteAll()
            db.gastoFijoDao().deleteAll()
            
            // Delete Parents: Tarjetas, Fuentes, Categorias
            // Note: Categorias usually has a default 1, we might need to preserve it or re-insert it
            db.tarjetaDao().deleteAll()
            db.fuenteIngresoDao().deleteAll()
            db.categoriaDao().deleteAll()

            // Restore Order: Parents -> Children

            // 1. Categorias
            val catArray = root.optJSONArray("categorias")
            if (catArray != null) {
                for (i in 0 until catArray.length()) {
                    val obj = catArray.getJSONObject(i)
                    // Best Effort: Use defaults if fields missing
                    val item = Categoria(
                        id = obj.optLong("id", 0), // If 0, autogenerate, but we prefer reusing ID for mapping
                        nombre = obj.optString("nombre", "Sin Nombre"),
                        icono = obj.optString("icono", "🏷️"),
                        esDefault = obj.optBoolean("esDefault", false),
                        activa = obj.optBoolean("activa", true),
                        tipo = obj.optString("tipo", "AMBOS")
                    )
                    db.categoriaDao().insert(item) // ConflictStrategy.REPLACE implied usually or manually handled
                }
            }

            // 2. Tarjetas
            val tarArray = root.optJSONArray("tarjetas")
            if (tarArray != null) {
                for (i in 0 until tarArray.length()) {
                    val obj = tarArray.getJSONObject(i)
                    val item = Tarjeta(
                        id = obj.optLong("id", 0),
                        alias = obj.optString("alias", "Tarjeta"),
                        ultimos4 = obj.optString("ultimos4", "0000"),
                        tipo = obj.optString("tipo", "DEBITO"),
                        banco = obj.optString("banco", "Desconocido"),
                        color = obj.optString("color", "#000000"),
                        activa = obj.optBoolean("activa", true)
                    )
                    db.tarjetaDao().insert(item)
                }
            }

            // 3. Fuentes
            val fueArray = root.optJSONArray("fuentes")
            if (fueArray != null) {
                for (i in 0 until fueArray.length()) {
                    val obj = fueArray.getJSONObject(i)
                    val item = FuenteIngreso(
                        id = obj.optLong("id", 0),
                        nombre = obj.optString("nombre", "Fuente"),
                        tipo = obj.optString("tipo", "OTRO"),
                        descripcion = obj.optString("descripcion", ""),
                        monto = obj.optDouble("monto", 0.0),
                        moneda = obj.optString("moneda", "COR"),
                        diaIngreso = if (obj.isNull("diaIngreso")) null else obj.optInt("diaIngreso"),
                        activa = obj.optBoolean("activa", true)
                    )
                    db.fuenteIngresoDao().insert(item)
                }
            }

            // 4. Gastos Fijos
            val fijArray = root.optJSONArray("gastosFijos")
            if (fijArray != null) {
                for (i in 0 until fijArray.length()) {
                    val obj = fijArray.getJSONObject(i)
                    val item = GastoFijo(
                        id = obj.optLong("id", 0),
                        nombre = obj.optString("nombre", "Fixed Expense"),
                        monto = obj.optDouble("monto", 0.0),
                        moneda = obj.optString("moneda", "COR"),
                        tarjetaId = if (obj.isNull("tarjetaId")) null else obj.optLong("tarjetaId"),
                        diaCobro = obj.optInt("diaCobro", 1),
                        activa = obj.optBoolean("activa", true),
                        categoriaId = obj.optLong("categoriaId", 1)
                    )
                    db.gastoFijoDao().insert(item)
                }
            }

            // 5. Gastos
            val gasArray = root.optJSONArray("gastos")
            if (gasArray != null) {
                for (i in 0 until gasArray.length()) {
                    val obj = gasArray.getJSONObject(i)
                    val item = RegistroGasto(
                        id = obj.optLong("id", 0),
                        monto = obj.optDouble("monto", 0.0),
                        moneda = obj.optString("moneda", "COR"),
                        descripcion = obj.optString("descripcion", ""),
                        tienda = obj.optString("tienda", ""),
                        tarjetaId = if (obj.isNull("tarjetaId")) null else obj.optLong("tarjetaId"),
                        fecha = obj.optLong("fecha", System.currentTimeMillis()),
                        categoriaId = obj.optLong("categoriaId", 1)
                    )
                    db.registroGastoDao().insert(item)
                }
            }

            // 6. Ingresos
            val ingArray = root.optJSONArray("ingresos")
            if (ingArray != null) {
                for (i in 0 until ingArray.length()) {
                    val obj = ingArray.getJSONObject(i)
                    val item = RegistroIngreso(
                        id = obj.optLong("id", 0),
                        monto = obj.optDouble("monto", 0.0),
                        moneda = obj.optString("moneda", "COR"),
                        descripcion = obj.optString("descripcion", ""),
                        fecha = obj.optLong("fecha", System.currentTimeMillis()),
                        categoriaId = obj.optLong("categoriaId", 1)
                    )
                    db.registroIngresoDao().insert(item)
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
