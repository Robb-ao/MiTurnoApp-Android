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
import com.example.miturnoapp.data.ShiftTemplateDAO
import com.example.miturnoapp.data.HolidayRepository
import com.example.miturnoapp.databinding.ActivityMainBinding
import com.example.miturnoapp.utils.CalendarEngine
import com.example.miturnoapp.utils.CalendarController
import com.example.miturnoapp.utils.NavigationController
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var calendarController: CalendarController

    // Dynamic Dates
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

    // Selected Day
    private var selectedDay: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupUserHeader()
        setupCalendar()

        // Calendar Controller & Stats
        calendarController = CalendarController(
            context = this,
            calendarAdapter = calendarAdapter,
            holidayRepository = HolidayRepository(this),
            shiftDAO = ShiftTemplateDAO(this),
            updateMonthTitle = { title ->
                binding.layoutMonthNavigator.tvCurrentMonth.text = title
            },
            statsUpdater = { total, extra, rest ->
                binding.layoutStatsCards.tvTotalHours.text = total
                binding.layoutStatsCards.tvExtraHours.text = extra
                binding.layoutStatsCards.tvRestDays.text = rest
            }
        )

        calendarController.loadMonthData(currentYear, currentMonth)
        NavigationController(binding).setupBottomNavigation()
        setupListeners()
    }

    // Edge-to-Edge Margins
    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // User Header & Session
    private fun setupUserHeader() {
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userName = intent.getStringExtra("USER_NAME_EXTRA") ?: sharedPreferences.getString("user_name", "Usuario")
        binding.layoutTopBar.tvUserNameHeader.text = "¡Hola, $userName!"
    }

    // Calendar Setup
    private fun setupCalendar() {
        val days = CalendarEngine().generateDaysForMonth(currentYear, currentMonth)

        // Initialize adapter with days and click listener receiving holiday reason
        calendarAdapter = CalendarAdapter(days) { day, holidayReason ->
            if (day.isNotEmpty()) {
                selectedDay = day
                if (!holidayReason.isNullOrEmpty()) {
                    binding.layoutDayDetail.tvSelectedDateTitle.text = "Día Seleccionado: $day (Festivo: $holidayReason)"
                } else {
                    binding.layoutDayDetail.tvSelectedDateTitle.text = "Día Seleccionado: $day"
                }
            }
        }
        binding.rvCalendarGrid.layoutManager = GridLayoutManager(this, 7)
        binding.rvCalendarGrid.adapter = calendarAdapter
    }

    // Event Listeners & Clicks
    private fun setupListeners() {
        // Shift Assignment (Short Click)
        binding.fabAssignShift.setOnClickListener {
            val finalDays = calendarAdapter.getSelectedDays().ifEmpty {
                if (selectedDay != null) listOf(selectedDay!!) else emptyList()
            }

            if (finalDays.isEmpty()) {
                Toast.makeText(this, "Por favor, selecciona al menos un día", Toast.LENGTH_SHORT).show()
            } else {
                calendarController.showShiftAssignmentDialog(finalDays, currentYear, currentMonth) {
                    calendarController.loadMonthData(currentYear, currentMonth)
                }
            }
        }

        // Shift Management (Long Click)
        binding.fabAssignShift.setOnLongClickListener {
            val intent = Intent(this, ShiftManagerActivity::class.java)
            startActivity(intent)
            true
        }

        // Previous Month Navigation
        binding.layoutMonthNavigator.btnPrevMonth.setOnClickListener {
            selectedDay = null
            if (currentMonth == 1) { currentMonth = 12; currentYear-- } else { currentMonth-- }
            updateCalendarView()
        }

        // Next Month Navigation
        binding.layoutMonthNavigator.btnNextMonth.setOnClickListener {
            selectedDay = null
            if (currentMonth == 12) { currentMonth = 1; currentYear++ } else { currentMonth++ }
            updateCalendarView()
        }
    }

    // Update Month View
    private fun updateCalendarView() {
        val newDays = CalendarEngine().generateDaysForMonth(currentYear, currentMonth)
        calendarAdapter.updateDays(newDays)
        calendarController.loadMonthData(currentYear, currentMonth)
    }
}