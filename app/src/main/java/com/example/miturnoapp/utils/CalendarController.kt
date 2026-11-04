package com.example.miturnoapp.utils

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.miturnoapp.adapters.CalendarAdapter
import com.example.miturnoapp.data.HolidayRepository
import com.example.miturnoapp.data.ShiftTemplateDAO

class CalendarController(
    private val context: Context,
    private val calendarAdapter: CalendarAdapter,
    private val holidayRepository: HolidayRepository,
    private val shiftDAO: ShiftTemplateDAO,
    private val updateMonthTitle: (String) -> Unit
) {

    // Carga de datos mensuales y renderizado de colores
    fun loadMonthData(year: Int, month: Int) {
        updateMonthTitle("${CalendarEngine().getMonthName(month)} $year")

        holidayRepository.fetchHolidaysForMonth(year, month) { holidaysMap ->
            calendarAdapter.updateHolidaysMap(holidaysMap)
        }

        val monthShiftsColors = mutableMapOf<String, String>()
        val totalDays = CalendarEngine().generateDaysForMonth(year, month)

        totalDays.forEach { day ->
            if (day.isNotEmpty()) {
                val dateKey = "$year-$month-$day"
                val shift = shiftDAO.getShiftForDate(dateKey)
                if (shift != null) {
                    val label = shift.labelName.lowercase()
                    val calculatedColor = when {
                        label.contains("mañana") || label.contains("m") -> "#2196F3"
                        label.contains("tarde") || label.contains("t") -> "#FF9800"
                        label.contains("noche") || label.contains("n") -> "#9C27B0"
                        label.contains("guardia") || label.contains("g") -> "#E91E63"
                        else -> "#4CAF50"
                    }
                    monthShiftsColors[day] = calculatedColor
                }
            }
        }

        calendarAdapter.updateAssignedShifts(monthShiftsColors)
    }

    // Cuadro de asignación de turnos
    fun showShiftAssignmentDialog(selectedDays: List<String>, year: Int, month: Int, onSuccess: () -> Unit) {
        var availableShifts = shiftDAO.getAllShifts()
        if (availableShifts.isEmpty()) {
            Toast.makeText(context, "Inicializando catálogo de turnos...", Toast.LENGTH_SHORT).show()
            shiftDAO.assignShiftToDay("mock_setup_M", 1, "Mañana (07-15)")
            shiftDAO.assignShiftToDay("mock_setup_T", 2, "Tarde (15-23)")
            shiftDAO.assignShiftToDay("mock_setup_N", 3, "Noche (23-07)")
            availableShifts = shiftDAO.getAllShifts()
        }

        val shiftNames = if (availableShifts.isNotEmpty()) {
            availableShifts.map { it.labelName }.toTypedArray()
        } else {
            arrayOf("Mañana (07-15)", "Tarde (15-23)", "Noche (23-07)", "Guardia 24h", "Libre / Descanso")
        }

        var selectedShiftIndex = 0

        AlertDialog.Builder(context)
            .setTitle("Asignar turno rotativo a ${selectedDays.size} días")
            .setSingleChoiceItems(shiftNames, 0) { _, which ->
                selectedShiftIndex = which
            }
            .setPositiveButton("Aplicar") { dialog, _ ->
                if (availableShifts.isNotEmpty() && selectedShiftIndex < availableShifts.size) {
                    val chosenShift = availableShifts[selectedShiftIndex]
                    selectedDays.forEach { day ->
                        val dateKey = "$year-$month-$day"
                        shiftDAO.assignShiftToDay(dateKey, chosenShift.id, chosenShift.labelName)
                    }
                    Toast.makeText(context, "¡Turno aplicado con éxito!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
                onSuccess()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
