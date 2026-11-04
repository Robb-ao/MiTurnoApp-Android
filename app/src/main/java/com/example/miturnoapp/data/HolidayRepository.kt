package com.example.miturnoapp.data

import android.content.Context
import android.widget.Toast
import com.example.miturnoapp.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HolidayRepository(private val context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://nagerholidays.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    fun fetchHolidaysForMonth(year: Int, month: Int, onResult: (Map<String, String>) -> Unit) {
        apiService.getHolidays("ES", year).enqueue(object : Callback<List<PublicHoliday>> {
            override fun onResponse(call: Call<List<PublicHoliday>>, response: Response<List<PublicHoliday>>) {
                if (response.isSuccessful) {
                    val holidays = response.body() ?: emptyList()

                    // Formateamos el mes a dos dígitos para el filtro de la API
                    val monthString = String.format("%02d", month)
                    val prefix = "$year-$monthString"

                    // Filtramos por el prefijo dinámico del año y mes actual
                    val monthHolidaysMap = holidays.filter { it.date.startsWith(prefix) }
                        .associate {
                            val dayNumber = it.date.split("-").last().trim().toInt().toString()
                            dayNumber to it.name
                        }

                    // Devolvemos el diccionario de festivos
                    onResult(monthHolidaysMap)
                } else {
                    Toast.makeText(context, "Error en el servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PublicHoliday>>, t: Throwable) {
                Toast.makeText(context, "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
