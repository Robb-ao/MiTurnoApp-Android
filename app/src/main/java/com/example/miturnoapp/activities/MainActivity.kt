package com.example.miturnoapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.miturnoapp.adapters.CalendarAdapter
import com.example.miturnoapp.data.HolidayRepository
import com.example.miturnoapp.databinding.ActivityMainBinding
import com.example.miturnoapp.utils.CalendarEngine
import com.example.miturnoapp.utils.ShiftAssignmentDialog

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
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

        holidayRepository = HolidayRepository(this)

        loadMonthData()

        binding.btnPreviousMonth.setOnClickListener {
            if (currentMonth == 1) {
                currentMonth = 12
                currentYear--
            } else {
                currentMonth--
            }
            loadMonthData()
        }

        binding.btnNextMonth.setOnClickListener {
            if (currentMonth == 12) {
                currentMonth = 1
                currentYear++
            } else {
                currentMonth++
            }
            loadMonthData()
        }

        // Click corto: Asigna turno
        binding.fabAssignShift.setOnClickListener {
            val selected = calendarAdapter.getSelectedDays()
            if (selected.isEmpty()) {
                Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show()
            } else {
                ShiftAssignmentDialog(this).show(selected) {
                    // Accion al terminar
                }
            }
        }

        // Click largo: Abre administrador de turnos
        binding.fabAssignShift.setOnLongClickListener {
            val intent = Intent(this, ShiftManagerActivity::class.java)
            startActivity(intent)
            true
        }
    }

    private fun loadMonthData() {
        binding.tvCurrentMonth.text = "${CalendarEngine().getMonthName(currentMonth)} $currentYear"

        val days = CalendarEngine().generateDaysForMonth(currentYear, currentMonth)

        calendarAdapter = CalendarAdapter(days) { day -> }
        binding.rvCalendarGrid.layoutManager = GridLayoutManager(this, 7)
        binding.rvCalendarGrid.adapter = calendarAdapter

        holidayRepository.fetchHolidaysForMonth(currentYear, currentMonth) { monthHolidaysMap ->
            calendarAdapter.updateHolidaysMap(monthHolidaysMap)
        }
    }
}
