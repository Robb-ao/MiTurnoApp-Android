package com.example.miturnoapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miturnoapp.databinding.ItemCalendarDayBinding

class CalendarAdapter(
    private var days: List<String>,
    private val onDayClick: (String, String?) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var holidaysMap: Map<String, String> = emptyMap()
    private var assignedShifts: Map<String, String> = emptyMap()
    private val selectedDays = mutableSetOf<String>()

    inner class CalendarViewHolder(val binding: ItemCalendarDayBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day = days[position]
        holder.binding.tvDayNumber.text = day

        if (day.isNotEmpty()) {
            // Festivos
            val cleanDay = day.trim().toIntOrNull()?.toString() ?: day
            val holidayReason = holidaysMap[cleanDay] ?: holidaysMap[day]

            if (!holidayReason.isNullOrEmpty()) {
                holder.binding.tvDayNumber.setTextColor(Color.parseColor("#D32F2F"))
            } else {
                holder.binding.tvDayNumber.setTextColor(Color.parseColor("#1F2937"))
            }

            // Turnos
            val shiftLabel = assignedShifts[day]
            if (!shiftLabel.isNullOrEmpty()) {
                holder.binding.tvShiftLabel.text = shiftLabel
                holder.binding.tvShiftLabel.visibility = View.VISIBLE

                val lowerLabel = shiftLabel.lowercase()
                when {
                    lowerLabel.contains("libre") || lowerLabel.contains("descanso") || lowerLabel.contains("l") -> {
                        holder.binding.tvShiftLabel.setBackgroundColor(Color.parseColor("#4CAF50"))
                    }
                    else -> {
                        holder.binding.tvShiftLabel.setBackgroundColor(Color.parseColor("#FF9800"))
                    }
                }
            } else {
                holder.binding.tvShiftLabel.text = ""
                holder.binding.tvShiftLabel.visibility = View.GONE
            }

            // Selección
            holder.binding.root.isSelected = selectedDays.contains(day)

            holder.binding.root.setOnClickListener {
                if (selectedDays.contains(day)) {
                    selectedDays.remove(day)
                } else {
                    selectedDays.add(day)
                }
                notifyItemChanged(position)
                onDayClick(day, holidayReason)
            }
        } else {
            // Celda vacía
            holder.binding.tvDayNumber.text = ""
            holder.binding.tvShiftLabel.visibility = View.GONE
            holder.binding.root.isSelected = false
            holder.binding.root.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = days.size

    fun updateDays(newDays: List<String>) {
        this.days = newDays
        selectedDays.clear()
        notifyDataSetChanged()
    }

    fun updateHolidaysMap(newHolidaysMap: Map<String, String>) {
        this.holidaysMap = newHolidaysMap
        notifyDataSetChanged()
    }

    fun updateAssignedShifts(newShifts: Map<String, String>) {
        this.assignedShifts = newShifts
        notifyDataSetChanged()
    }

    fun getSelectedDays(): List<String> = selectedDays.toList()
}