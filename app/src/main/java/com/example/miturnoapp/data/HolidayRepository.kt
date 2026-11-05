package com.example.miturnoapp.data

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HolidayRepository(private val context: Context) {

    // Fetch holidays from Nager.Date v4 API
    fun fetchHolidaysForMonth(year: Int, month: Int, onResult: (Map<String, String>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // API Request matching v4 order: countryCode first, then year
                val holidays = RetrofitClient.apiService.getPublicHolidays("ES", year)

                val monthString = String.format("%02d", month)
                val prefix = "$year-$monthString"

                val monthHolidaysMap = mutableMapOf<String, String>()

                // Filtro simplificado: Solo festivos nacionales (nationalHoliday == true)
                holidays.filter { it.date.startsWith(prefix) && it.nationalHoliday }.forEach { holiday ->
                    val parts = holiday.date.split("-")
                    if (parts.size == 3) {
                        val rawDay = parts[2]
                        val cleanDay = rawDay.toIntOrNull()?.toString()

                        if (cleanDay != null) {
                            // Store both clean and padded day keys
                            monthHolidaysMap[cleanDay] = holiday.name
                            monthHolidaysMap[String.format("%02d", cleanDay.toInt())] = holiday.name
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    onResult(monthHolidaysMap)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}