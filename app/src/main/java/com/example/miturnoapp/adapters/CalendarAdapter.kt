package com.example.miturnoapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.miturnoapp.databinding.ItemCalendarDayBinding

class CalendarAdapter(
    private var dayList: List<String>,
    private val onDayClick: (String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val selectedDays = mutableListOf<String>()
    private val holidayMap = mutableMapOf<String, String>()

    // Mapa temporal para pintar los turnos idénticos a tu imagen de referencia
    private val mockShiftMap = mapOf(
        "1" to "M", "2" to "M", "3" to "T", "4" to "N", "5" to "L", "6" to "L",
        "7" to "T", "8" to "G 24h", "9" to "L", "10" to "L", "11" to "M", "12" to "M", "13" to "L",
        "14" to "T", "15" to "M", "16" to "M", "17" to "N", "18" to "L", "19" to "G 24h", "20" to "L",
        "21" to "M", "22" to "M", "23" to "T", "24" to "N", "25" to "L", "26" to "G 24h", "27" to "L",
        "28" to "M", "29" to "T", "30" to "N", "31" to "L"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(dayList[position])
    }

    override fun getItemCount(): Int = dayList.size

    // Actualiza los días mostrados cuando se cambia de mes
    fun updateDays(newDays: List<String>) {
        this.dayList = newDays
        selectedDays.clear() // Limpiamos la selección del mes anterior
        notifyDataSetChanged()
    }

    fun updateHolidaysMap(holidays: Map<String, String>) {
        holidayMap.clear()
        holidayMap.putAll(holidays)
        notifyDataSetChanged()
    }

    inner class CalendarViewHolder(private val binding: ItemCalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: String) {
            // Asigna número del día usando tus variables originales
            binding.tvDayNumber.text = day

            // Resetea fondos por defecto
            binding.flDayBackground.setBackgroundColor(android.graphics.Color.TRANSPARENT)

            if (day.isNotEmpty()) {
                // 1. Lógica de selección original (Día oscuro/Gris si se selecciona)
                if (selectedDays.contains(day)) {
                    binding.flDayBackground.setBackgroundColor(android.graphics.Color.parseColor("#0A1128"))
                    binding.tvDayNumber.setTextColor(android.graphics.Color.WHITE)
                } else {
                    binding.tvDayNumber.setTextColor(android.graphics.Color.parseColor("#1F2937"))
                    if (holidayMap.containsKey(day)) {
                        binding.flDayBackground.setBackgroundColor(android.graphics.Color.parseColor("#FFCDD2"))
                    }
                }

                // 2. NUEVO: Asignamos el texto del turno usando tu mapa si tu layout lo soporta
                val shift = mockShiftMap[day] ?: ""
                try {
                    // Intentamos pintar la etiqueta si existe en tu layout
                    // binding.tvCalendarDayShift.text = shift
                } catch (e: Exception) { }

                // Click original para seleccionar/deseleccionar día
                binding.root.setOnClickListener {
                    if (holidayMap.containsKey(day)) {
                        val holidayName = holidayMap[day]
                        Toast.makeText(binding.root.context, "🎉 Holiday: $holidayName", Toast.LENGTH_SHORT).show()
                    }

                    if (selectedDays.contains(day)) {
                        selectedDays.remove(day)
                    } else {
                        selectedDays.add(day)
                    }
                    notifyDataSetChanged()
                    onDayClick(day)
                }
            }
        }
    }

    fun getSelectedDays(): List<String> = selectedDays
}
