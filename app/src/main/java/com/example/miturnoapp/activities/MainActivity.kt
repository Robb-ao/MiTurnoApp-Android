package com.example.miturnoapp.activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.miturnoapp.adapters.CalendarAdapter
import com.example.miturnoapp.data.ShiftTemplateDAO
import com.example.miturnoapp.data.HolidayRepository
import com.example.miturnoapp.databinding.ActivityMainBinding
import com.example.miturnoapp.utils.CalendarEngine

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var holidayRepository: HolidayRepository

    private var currentYear = 2026
    private var currentMonth = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var userName = intent.getStringExtra("USER_NAME_EXTRA")

        if (userName == null) {
            val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            userName = sharedPreferences.getString("user_name", "User")
        }

        binding.tvUserHello.text = "Hello, $userName!"

        // Inicializamos el repositorio
        holidayRepository = HolidayRepository(this)

        setupCalendar()
        loadMonthData()

        binding.fabAssignShift.setOnClickListener {
            val selected = calendarAdapter.getSelectedDays()
            if (selected.isEmpty()) {
                Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show()
            } else {
                val shiftDAO = ShiftTemplateDAO(this)
                val availableShifts = shiftDAO.getAllShifts()

                val shiftNames = availableShifts.map { it.labelName }.toTypedArray()
                var selectedShiftIndex = 0

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Assign Shift to ${selected.size} days")
                    .setSingleChoiceItems(shiftNames, 0) { _, which ->
                        selectedShiftIndex = which
                    }
                    .setPositiveButton("Apply") { dialog, _ ->
                        val chosenShift = availableShifts[selectedShiftIndex]

                        selected.forEach { day ->
                            shiftDAO.assignShiftToDay("$currentYear-$currentMonth-$day", chosenShift.id, "Turno asignado")
                        }

                        Toast.makeText(this, "Applied ${chosenShift.labelName} successfully!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun loadMonthData() {
        // Accedemos al TextView que está dentro del archivo layout_month_navigator
        binding.layoutMonthNavigator.tvCurrentMonth.text = "${CalendarEngine().getMonthName(currentMonth)} $currentYear"

        // Llamada corregida usando el nombre exacto de tu función
        holidayRepository.fetchHolidaysForMonth(currentYear, currentMonth) { holidaysMap ->
            calendarAdapter.updateHolidaysMap(holidaysMap)
        }
    }


    private fun setupCalendar() {
        val days = mutableListOf<String>()
        days.add(""); days.add(""); days.add("")
        for (i in 1..31) {
            days.add(i.toString())
        }

        calendarAdapter = CalendarAdapter(days) { day -> }

        binding.rvCalendarGrid.layoutManager = GridLayoutManager(this, 7)
        binding.rvCalendarGrid.adapter = calendarAdapter
    }
}
