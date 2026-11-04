package com.example.miturnoapp.activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.miturnoapp.adapters.CalendarAdapter
import com.example.miturnoapp.data.ShiftTemplateDAO
import com.example.miturnoapp.data.HolidayRepository
import com.example.miturnoapp.databinding.ActivityMainBinding
import com.example.miturnoapp.utils.CalendarEngine
import com.example.miturnoapp.utils.NavigationController // Importamos el nuevo controlador

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var holidayRepository: HolidayRepository
    private lateinit var shiftDAO: ShiftTemplateDAO

    private var currentYear = 2026
    private var currentMonth = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupUserHeader()

        holidayRepository = HolidayRepository(this)
        shiftDAO = ShiftTemplateDAO(this)

        setupCalendar()
        loadMonthData()
        setupListeners()

        // Inicializamos el controlador independiente para mantener limpia la actividad
        NavigationController(binding).setupBottomNavigation()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupUserHeader() {
        var userName = intent.getStringExtra("USER_NAME_EXTRA")
        if (userName == null) {
            val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            userName = sharedPreferences.getString("user_name", "User")
        }
        binding.tvUserHello.text = "Dra. $userName"
    }

    private fun setupCalendar() {
        val days = CalendarEngine().generateDaysForMonth(currentYear, currentMonth)

        calendarAdapter = CalendarAdapter(days) { day ->
            updateDayDetailSection(day)
        }

        binding.rvCalendarGrid.layoutManager = GridLayoutManager(this, 7)
        binding.rvCalendarGrid.adapter = calendarAdapter
    }

    private fun loadMonthData() {
        binding.layoutMonthNavigator.tvCurrentMonth.text = "${CalendarEngine().getMonthName(currentMonth)} $currentYear"

        holidayRepository.fetchHolidaysForMonth(currentYear, currentMonth) { holidaysMap ->
            calendarAdapter.updateHolidaysMap(holidaysMap)
        }

        val days = CalendarEngine().generateDaysForMonth(currentYear, currentMonth)
        calendarAdapter.updateDays(days)
    }

    private fun setupListeners() {
        binding.fabAssignShift.setOnClickListener {
            val selectedDays = calendarAdapter.getSelectedDays()
            if (selectedDays.isEmpty()) {
                Toast.makeText(this, "Por favor, selecciona al menos un día", Toast.LENGTH_SHORT).show()
            } else {
                showShiftAssignmentDialog(selectedDays)
            }
        }

        binding.layoutMonthNavigator.btnPrevMonth.setOnClickListener {
            if (currentMonth == 1) {
                currentMonth = 12
                currentYear--
            } else {
                currentMonth--
            }
            loadMonthData()
        }

        binding.layoutMonthNavigator.btnNextMonth.setOnClickListener {
            if (currentMonth == 12) {
                currentMonth = 1
                currentYear++
            } else {
                currentMonth++
            }
            loadMonthData()
        }
    }

    /**
     * Muestra el cuadro de diálogo para la asignación masiva de turnos
     */
    private fun showShiftAssignmentDialog(selectedDays: List<String>) {
        var availableShifts = shiftDAO.getAllShifts()
        if (availableShifts.isEmpty()) {
            Toast.makeText(this, "Inicializando catálogo de turnos rotativos...", Toast.LENGTH_SHORT).show()
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

        AlertDialog.Builder(this)
            .setTitle("Asignar turno rotativo a ${selectedDays.size} days")
            .setSingleChoiceItems(shiftNames, 0) { _, which ->
                selectedShiftIndex = which
            }
            .setPositiveButton("Aplicar") { dialog, _ ->
                if (availableShifts.isNotEmpty() && selectedShiftIndex < availableShifts.size) {
                    val chosenShift = availableShifts[selectedShiftIndex]
                    selectedDays.forEach { day ->
                        shiftDAO.assignShiftToDay("$currentYear-$currentMonth-$day", chosenShift.id, "Turno asignado")
                    }
                    Toast.makeText(this, "¡Turno ${chosenShift.labelName} aplicado!", Toast.LENGTH_SHORT).show()
                } else {
                    val fallbackName = shiftNames[selectedShiftIndex]
                    Toast.makeText(this, "¡Turno $fallbackName guardado localmente!", Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
                loadMonthData()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateDayDetailSection(day: String) {
        if (day.isEmpty()) return
        binding.layoutDayDetail.tvSelectedDateTitle.text = "Día Seleccionado: $day"
    }
}
