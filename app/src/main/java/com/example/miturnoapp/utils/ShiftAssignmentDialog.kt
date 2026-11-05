package com.example.miturnoapp.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.example.miturnoapp.R
import com.example.miturnoapp.data.ShiftTemplate
import com.example.miturnoapp.data.ShiftTemplateDAO
import com.example.miturnoapp.databinding.ItemShiftSelectionBinding

class ShiftAssignmentDialog(private val context: Context) {

    fun show(selectedDays: List<String>, year: Int, month: Int, onAssignmentComplete: () -> Unit) {
        val shiftDAO = ShiftTemplateDAO(context)
        var availableShifts = shiftDAO.getAllShifts()

        if (availableShifts.isEmpty()) {
            Toast.makeText(context, "No shifts available. Please create one first.", Toast.LENGTH_LONG).show()
            return
        }

        var selectedShiftIndex = 0

        // Adaptador utilizando ViewBinding para la celda item_shift_selection.xml
        val adapter = object : ArrayAdapter<ShiftTemplate>(context, R.layout.item_shift_selection, availableShifts) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val binding = if (convertView == null) {
                    ItemShiftSelectionBinding.inflate(LayoutInflater.from(context), parent, false)
                } else {
                    ItemShiftSelectionBinding.bind(convertView)
                }

                val shift = getItem(position)
                binding.radioShift.text = shift?.labelName ?: ""
                binding.radioShift.isChecked = (position == selectedShiftIndex)

                // Selección de fila completa
                binding.root.setOnClickListener {
                    selectedShiftIndex = position
                    notifyDataSetChanged()
                }
                binding.radioShift.setOnClickListener {
                    selectedShiftIndex = position
                    notifyDataSetChanged()
                }

                // Botón de editar ubicado a la derecha
                binding.btnEditShift.setOnClickListener {
                    if (shift != null) {
                        showEditShiftDialog(shift) {
                            availableShifts = shiftDAO.getAllShifts()
                            clear()
                            addAll(availableShifts)
                            notifyDataSetChanged()
                        }
                    }
                }

                return binding.root
            }
        }

        AlertDialog.Builder(context)
            .setTitle("Assign Shift to ${selectedDays.size} days")
            .setAdapter(adapter) { _, which ->
                selectedShiftIndex = which
            }
            .setPositiveButton("Apply") { dialog, _ ->
                val chosenShift = availableShifts[selectedShiftIndex]

                selectedDays.forEach { day ->
                    val formattedMonth = String.format("%02d", month)
                    val formattedDay = String.format("%02d", day.toInt())
                    val formattedDate = "$year-$formattedMonth-$formattedDay"

                    shiftDAO.assignShiftToDay(
                        date = formattedDate,
                        shiftId = chosenShift.id,
                        notes = "Turno asignado"
                    )
                }

                Toast.makeText(context, "Applied ${chosenShift.labelName} successfully!", Toast.LENGTH_SHORT).show()
                onAssignmentComplete()
                dialog.dismiss()
            }
            .setNeutralButton("Remove Shift") { dialog, _ ->
                selectedDays.forEach { day ->
                    val formattedMonth = String.format("%02d", month)
                    val formattedDay = String.format("%02d", day.toInt())
                    val formattedDate = "$year-$formattedMonth-$formattedDay"

                    shiftDAO.deleteShiftForDate(formattedDate)
                }

                Toast.makeText(context, "Shifts removed successfully!", Toast.LENGTH_SHORT).show()
                onAssignmentComplete()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showEditShiftDialog(shift: ShiftTemplate, onUpdated: () -> Unit) {
        val input = EditText(context).apply {
            setText(shift.labelName)
            setSelection(text.length)
            hint = "Nombre del turno"
        }

        AlertDialog.Builder(context)
            .setTitle("Editar Turno")
            .setView(input)
            .setPositiveButton("Guardar") { dialog, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    val shiftDAO = ShiftTemplateDAO(context)
                    shiftDAO.updateShift(
                        id = shift.id,
                        label = newName,
                        start = shift.startTime,
                        end = shift.endTime,
                        color = shift.colorCode
                    )
                    Toast.makeText(context, "Turno actualizado", Toast.LENGTH_SHORT).show()
                    onUpdated()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}