package com.example.miturnoapp.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.miturnoapp.adapters.CalendarAdapter
import com.example.miturnoapp.data.HolidayRepository
import com.example.miturnoapp.data.ShiftTemplateDAO
import java.util.Calendar

class CalendarController(
    private val context: Context,
    private val calendarAdapter: CalendarAdapter,
    private val holidayRepository: HolidayRepository,
    private val shiftDAO: ShiftTemplateDAO,
    private val updateMonthTitle: (String) -> Unit,
    private val statsUpdater: ((total: String, extra: String, rest: String) -> Unit)? = null
) {

    fun loadMonthData(year: Int, month: Int) {
        updateMonthTitle("${CalendarEngine().getMonthName(month)} $year")

        // Fetch Holidays from Repository/API
        holidayRepository.fetchHolidaysForMonth(year, month) { holidaysMap ->
            // Aseguramos que la actualización del mapa de festivos ocurra en el hilo principal
            Handler(Looper.getMainLooper()).post {
                calendarAdapter.updateHolidaysMap(holidaysMap)
            }
        }

        val monthShifts = mutableMapOf<String, String>()
        val totalDays = CalendarEngine().generateDaysForMonth(year, month)

        var workedDaysCount = 0
        var restDaysCount = 0

        totalDays.forEach { day ->
            if (day.isNotEmpty()) {
                val formattedMonth = String.format("%02d", month)
                val formattedDay = String.format("%02d", day.toInt())
                val dateKey = "$year-$formattedMonth-$formattedDay"

                val shift = shiftDAO.getShiftForDate(dateKey)
                if (shift != null) {
                    val label = shift.labelName
                    monthShifts[day] = label

                    val lowerLabel = label.lowercase()
                    if (lowerLabel.contains("libre") || lowerLabel.contains("descanso") || lowerLabel.contains("l")) {
                        restDaysCount++
                    } else {
                        workedDaysCount++
                    }
                }
            }
        }

        calendarAdapter.updateAssignedShifts(monthShifts)

        // Statistics Calculation
        val totalHoursNum = workedDaysCount * 8
        val standardHours = 160
        val extraHoursNum = if (totalHoursNum > standardHours) totalHoursNum - standardHours else 0

        statsUpdater?.invoke(
            "${totalHoursNum}h / ${standardHours}h",
            if (extraHoursNum > 0) "+${extraHoursNum}h" else "0h",
            "$restDaysCount days"
        )
    }

    fun showShiftAssignmentDialog(selectedDays: List<String>, year: Int, month: Int, onSuccess: () -> Unit) {
        val dialog = ShiftAssignmentDialog(context)
        dialog.show(selectedDays, year, month, onSuccess)
    }
}