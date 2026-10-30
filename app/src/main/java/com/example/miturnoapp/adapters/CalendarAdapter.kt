package com.example.miturnoapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.miturnoapp.databinding.ItemCalendarDayBinding

class CalendarAdapter(
    private val dayList: List<String>,
    private val onDayClick: (String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    // Lista de días seleccionados
    private val selectedDays = mutableListOf<String>()
    // Diccionario de dias festivos (Dia -> Nombre del Festivo)
    private val holidayMap = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(dayList[position])
    }

    override fun getItemCount(): Int = dayList.size

    // Recibe el diccionario de festivos y repinta
    fun updateHolidaysMap(holidays: Map<String, String>) {
        holidayMap.clear()
        holidayMap.putAll(holidays)
        notifyDataSetChanged()
    }

    inner class CalendarViewHolder(private val binding: ItemCalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: String) {
            // Asigna número del día
            binding.tvDayNumber.text = day

            // Resetea fondo por defecto
            binding.flDayBackground.setBackgroundColor(android.graphics.Color.TRANSPARENT)

            if (day.isNotEmpty()) {
                // Si el dia es festivo, lo pinta de rojo suave
                if (holidayMap.containsKey(day)) {
                    binding.flDayBackground.setBackgroundColor(android.graphics.Color.parseColor("#FFCDD2"))
                }
                // Si esta seleccionado por el usuario, lo pinta de gris
                if (selectedDays.contains(day)) {
                    binding.flDayBackground.setBackgroundColor(android.graphics.Color.LTGRAY)
                }

                // Click para seleccionar/deseleccionar día o ver festivo
                binding.root.setOnClickListener {
                    // Si el dia es festivo, muestra el flotante con el motivo antes de seleccionar
                    if (holidayMap.containsKey(day)) {
                        val holidayName = holidayMap[day]
                        Toast.makeText(binding.root.context, "🎉 Holiday: $holidayName", Toast.LENGTH_SHORT).show()
                    }

                    if (selectedDays.contains(day)) {
                        selectedDays.remove(day)
                        if (holidayMap.containsKey(day)) {
                            binding.flDayBackground.setBackgroundColor(android.graphics.Color.parseColor("#FFCDD2"))
                        } else {
                            binding.flDayBackground.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        }
                    } else {
                        selectedDays.add(day)
                        binding.flDayBackground.setBackgroundColor(android.graphics.Color.LTGRAY)
                    }
                    onDayClick(day)
                }
            }
        }
    }

    // Devuelve los días seleccionados
    fun getSelectedDays(): List<String> = selectedDays
}

