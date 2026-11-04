package com.example.miturnoapp.adapters

import android.graphics.Color
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
    private val assignedShifts = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(dayList[position])
    }

    override fun getItemCount(): Int = dayList.size

    // Actualización de datos del calendario
    fun updateDays(newDays: List<String>) {
        this.dayList = newDays
        selectedDays.clear()
        notifyDataSetChanged()
    }

    fun updateHolidaysMap(holidays: Map<String, String>) {
        holidayMap.clear()
        holidayMap.putAll(holidays)
        notifyDataSetChanged()
    }

    fun updateAssignedShifts(shifts: Map<String, String>) {
        assignedShifts.clear()
        assignedShifts.putAll(shifts)
        notifyDataSetChanged()
    }

    fun getSelectedDays(): List<String> = selectedDays

    inner class CalendarViewHolder(private val binding: ItemCalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: String) {
            binding.tvDayNumber.text = day
            binding.flDayBackground.setBackgroundColor(Color.TRANSPARENT)

            if (day.isEmpty()) return

            // Renderizado de estados y colores
            if (selectedDays.contains(day)) {
                binding.flDayBackground.setBackgroundColor(Color.parseColor("#0A1128"))
                binding.tvDayNumber.setTextColor(Color.WHITE)
            } else {
                binding.tvDayNumber.setTextColor(Color.parseColor("#1F2937"))

                if (assignedShifts.containsKey(day)) {
                    val colorHex = assignedShifts[day] ?: "#FFFFFF"
                    try {
                        binding.flDayBackground.setBackgroundColor(Color.parseColor(colorHex))
                    } catch (e: Exception) {
                        binding.flDayBackground.setBackgroundColor(Color.TRANSPARENT)
                    }
                } else if (holidayMap.containsKey(day)) {
                    binding.flDayBackground.setBackgroundColor(Color.parseColor("#FFCDD2"))
                }
            }

            // Gestión de clics y selección de celdas
            binding.root.setOnClickListener {
                if (holidayMap.containsKey(day)) {
                    val holidayName = holidayMap[day]
                    Toast.makeText(binding.root.context, "🎉 Festivo: $holidayName", Toast.LENGTH_SHORT).show()
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
