package com.example.miturnoapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miturnoapp.databinding.ItemCalendarDayBinding

class CalendarAdapter(
    private val dayList: List<String>,
    private val onDayClick: (String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    // Lista de días seleccionados
    private val selectedDays = mutableListOf<String>()
    // Lista de dias festivos
    private val holidayDays = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(dayList[position])
    }

    override fun getItemCount(): Int = dayList.size

    // Actualiza la lista de festivos y repinta la malla
    fun updateHolidays(holidays: List<String>) {
        holidayDays.clear()
        holidayDays.addAll(holidays)
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
                if (holidayDays.contains(day)) {
                    binding.flDayBackground.setBackgroundColor(android.graphics.Color.parseColor("#FFCDD2"))
                }
                // Si esta seleccionado por el usuario, lo pinta de gris
                if (selectedDays.contains(day)) {
                    binding.flDayBackground.setBackgroundColor(android.graphics.Color.LTGRAY)
                }

                // Click para seleccionar/deseleccionar día
                binding.root.setOnClickListener {
                    if (selectedDays.contains(day)) {
                        selectedDays.remove(day)
                        if (holidayDays.contains(day)) {
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
