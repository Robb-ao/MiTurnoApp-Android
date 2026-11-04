package com.example.miturnoapp.data

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HolidayRepository(private val context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://nagerholidays.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    // Consulta de festivos mensuales
    fun fetchHolidaysForMonth(year: Int, month: Int, onResult: (Map<String, String>) -> Unit) {
        // Hilo secundario IO
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Petición HTTP
                val holidays = apiService.getPublicHolidays(year, "ES")

                // Formato de mes
                val monthString = String.format("%02d", month)
                val prefix = "$year-$monthString"

                // Filtrado y mapeo
                val monthHolidaysMap = holidays.filter { it.date.startsWith(prefix) }
                    .associate {
                        val dayNumber = it.date.split("-").last().trim().toInt().toString()
                        dayNumber to it.localName
                    }

                // Respuesta a la interfaz
                withContext(Dispatchers.Main) {
                    onResult(monthHolidaysMap)
                }
            } catch (e: Exception) {
                // Control de errores
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Fallo de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
