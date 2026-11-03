package com.example.miturnoapp.utils

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.miturnoapp.data.ShiftTemplateDAO

class ShiftAssignmentDialog(private val context: Context) {

    fun show(selectedDays: List<String>, onAssignmentComplete: () -> Unit) {
        val shiftDAO = ShiftTemplateDAO(context)
        val availableShifts = shiftDAO.getAllShifts()

        if (availableShifts.isEmpty()) {
            Toast.makeText(context, "No shifts available. Please create one first.", Toast.LENGTH_LONG).show()
            return
        }

        val shiftNames = availableShifts.map { it.labelName }.toTypedArray()
        var selectedShiftIndex = 0

        AlertDialog.Builder(context)
            .setTitle("Assign Shift to ${selectedDays.size} days")
            .setSingleChoiceItems(shiftNames, 0) { _, which ->
                selectedShiftIndex = which
            }
            .setPositiveButton("Apply") { dialog, _ ->
                val chosenShift = availableShifts[selectedShiftIndex]

                selectedDays.forEach { day ->
                    shiftDAO.assignShiftToDay(
                        date = "2026-10-$day",
                        shiftId = chosenShift.id,
                        notes = "Turno asignado"
                    )
                }

                Toast.makeText(context, "Applied ${chosenShift.labelName} successfully!", Toast.LENGTH_SHORT).show()
                onAssignmentComplete()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
