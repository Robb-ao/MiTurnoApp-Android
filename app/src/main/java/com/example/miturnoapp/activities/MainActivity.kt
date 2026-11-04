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
import com.example.miturnoapp.utils.CalendarController
import com.example.miturnoapp.utils.NavigationController
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var calendarController: CalendarController

    // Variables de fecha actual dinámicas
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupUserHeader()
        setupCalendar()

        // Inicialización del controlador
        calendarController = CalendarController(
            this, calendarAdapter, HolidayRepository(this), ShiftTemplateDAO(this)
        ) { title ->
            binding.layoutMonthNavigator.tvCurrentMonth.text = title
        }

        calendarController.loadMonthData(currentYear, currentMonth)
        NavigationController(binding).setupBottomNavigation()
        setupListeners()
    }

    // Configuración de márgenes de pantalla
    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Carga de cabecera con sesión de usuario
    private fun setupUserHeader() {
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userName = intent.getStringExtra("USER_NAME_EXTRA") ?: sharedPreferences.getString("user_name", "User")
        binding.tvUserHello.text = "Dra. $userName"
    }

    // Inicialización de la rejilla de días
    private fun setupCalendar() {
        val days = CalendarEngine().generateDaysForMonth(currentYear, currentMonth)

        calendarAdapter = CalendarAdapter(days) { day ->
            if (day.isNotEmpty()) {
                binding.layoutDayDetail.tvSelectedDateTitle.text = "Día Seleccionado: $day"
            }
        }
        binding.rvCalendarGrid.layoutManager = GridLayoutManager(this, 7)
        binding.rvCalendarGrid.adapter = calendarAdapter
    }

    // Configuración de eventos de clic
    private fun setupListeners() {
        binding.fabAssignShift.setOnClickListener {
            val selectedDays = calendarAdapter.getSelectedDays()
            if (selectedDays.isEmpty()) {
                Toast.makeText(this, "Por favor, selecciona al menos un día", Toast.LENGTH_SHORT).show()
            } else {
                // Almacenamiento correcto pasando los estados del mes
                calendarController.showShiftAssignmentDialog(selectedDays, currentYear, currentMonth) {
                    calendarController.loadMonthData(currentYear, currentMonth)
                }
            }
        }

        binding.layoutMonthNavigator.btnPrevMonth.setOnClickListener {
            if (currentMonth == 1) { currentMonth = 12; currentYear-- } else { currentMonth-- }
            updateCalendarView()
        }

        binding.layoutMonthNavigator.btnNextMonth.setOnClickListener {
            if (currentMonth == 12) { currentMonth = 1; currentYear++ } else { currentMonth++ }
            updateCalendarView()
        }
    }

    // Actualización de vistas en navegación de meses
    private fun updateCalendarView() {
        val newDays = CalendarEngine().generateDaysForMonth(currentYear, currentMonth)
        calendarAdapter.updateDays(newDays)
        calendarController.loadMonthData(currentYear, currentMonth)
    }
}
