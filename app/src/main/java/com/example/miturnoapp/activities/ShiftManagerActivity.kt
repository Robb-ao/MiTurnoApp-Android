package com.example.miturnoapp.activities

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.miturnoapp.R
import com.example.miturnoapp.data.ShiftTemplateDAO
import com.google.android.material.card.MaterialCardView
import java.util.Calendar
import java.util.Locale

class ShiftManagerActivity : AppCompatActivity() {

    // Vistas
    private lateinit var etShiftName: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var btnPickColor: MaterialCardView
    private lateinit var btnSaveShift: Button

    // DAO
    private lateinit var shiftTemplateDAO: ShiftTemplateDAO

    private var selectedColor: Int = Color.parseColor("#03A9F4")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shift_manager)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Base de datos
        shiftTemplateDAO = ShiftTemplateDAO(this)

        // Inicializar vistas
        etShiftName = findViewById(R.id.etShiftName)
        etStartTime = findViewById(R.id.etStartTime)
        etEndTime = findViewById(R.id.etEndTime)
        btnPickColor = findViewById(R.id.btnPickColor)
        btnSaveShift = findViewById(R.id.btnSaveShift)

        btnPickColor.setCardBackgroundColor(selectedColor)

        // Eventos
        etStartTime.setOnClickListener { showTimePicker(etStartTime) }
        etEndTime.setOnClickListener { showTimePicker(etEndTime) }
        btnPickColor.setOnClickListener { showColorPickerDialog() }
        btnSaveShift.setOnClickListener { saveShiftToDatabase() }
    }

    // TimePicker
    private fun showTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            editText.setText(formattedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    // ColorPicker
    private fun showColorPickerDialog() {
        val colorNames = arrayOf("Azul Celesta", "Verde Esmeralda", "Naranja", "Rojo Coral", "Púrpura", "Amarillo")
        val colorValues = arrayOf("#03A9F4", "#2ECC71", "#E67E22", "#E74C3C", "#9B59B6", "#F1C40F")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Shift Color")
        builder.setItems(colorNames) { _, which ->
            selectedColor = Color.parseColor(colorValues[which])
            btnPickColor.setCardBackgroundColor(selectedColor)
        }
        builder.show()
    }

    // Guardar datos
    private fun saveShiftToDatabase() {
        val nameInput = etShiftName.text.toString().trim()
        val startTime = etStartTime.text.toString().trim()
        val endTime = etEndTime.text.toString().trim()

        if (nameInput.isEmpty()) {
            etShiftName.error = "Please enter a shift name"
            return
        }
        if (startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please select start and end hours", Toast.LENGTH_SHORT).show()
            return
        }

        // Formato etiqueta
        val finalLabel = "$nameInput $startTime"

        // Color Hexadecimal
        val hexColor = String.format("#%06X", 0xFFFFFF and selectedColor)

        // SQLite insert
        val resultId = shiftTemplateDAO.insertShift(finalLabel, startTime, endTime, hexColor)

        if (resultId != -1L) {
            Toast.makeText(this, "Shift saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error saving shift", Toast.LENGTH_SHORT).show()
        }
    }
}
