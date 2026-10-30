package com.example.miturnoapp.activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.miturnoapp.ApiService
import com.example.miturnoapp.adapters.CalendarAdapter
import com.example.miturnoapp.data.PublicHoliday
import com.example.miturnoapp.data.ShiftTemplateDAO
import com.example.miturnoapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    // Declaramos ViewBinding
    private lateinit var binding: ActivityMainBinding
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Activamos e inflamos la vista
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var userName = intent.getStringExtra("USER_NAME_EXTRA")

        // Auto-login
        if (userName == null) {
            val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            userName = sharedPreferences.getString("user_name", "User")
        }

        // Pintamos el saludo
        binding.tvUserHello.text = "Hello, $userName!"

        // Inicializa el calendario
        setupCalendar()

        // Carga festivos desde internet
        fetchHolidays()

        // Click en el botón flotante para asignar turnos
        binding.fabAssignShift.setOnClickListener {
            val selected = calendarAdapter.getSelectedDays()
            if (selected.isEmpty()) {
                Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show()
            } else {
                // Instancia base de datos
                val shiftDAO = ShiftTemplateDAO(this)
                val availableShifts = shiftDAO.getAllShifts()

                // Mapea nombres de turnos para el menu
                val shiftNames = availableShifts.map { it.labelName }.toTypedArray()
                var selectedShiftIndex = 0

                // Muestra Alertdialog
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Assign Shift to ${selected.size} days")
                    .setSingleChoiceItems(shiftNames, 0) { _, which ->
                        selectedShiftIndex = which
                    }
                    .setPositiveButton("Apply") { dialog, _ ->
                        val chosenShift = availableShifts[selectedShiftIndex]

                        // Guarda cada dia seleccionado en bd
                        selected.forEach { day ->
                            shiftDAO.assignShiftToDay("2026-10-$day", chosenShift.id, "Turno asignado")
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

    private fun setupCalendar() {
        // Lista de días del mes
        val days = mutableListOf<String>()
        days.add(""); days.add(""); days.add("")
        for (i in 1..31) {
            days.add(i.toString())
        }

        // Configura el adaptador
        calendarAdapter = CalendarAdapter(days) { day -> }

        // Enlaza el RecyclerView en malla de 7 columnas
        binding.rvCalendarGrid.layoutManager = GridLayoutManager(this, 7)
        binding.rvCalendarGrid.adapter = calendarAdapter
    }

    // Consulta los dias festivos
    private fun fetchHolidays() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nagerholidays.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.getHolidays("ES", 2026).enqueue(object : Callback<List<PublicHoliday>> {
            override fun onResponse(call: Call<List<PublicHoliday>>, response: Response<List<PublicHoliday>>) {
                if (response.isSuccessful) {
                    val holidays = response.body() ?: emptyList()

                    // Filtra días
                    val octoberHolidays = holidays.filter { it.date.startsWith("2026-10") }
                        .map { it.date.split("-").last().toInt().toString() }

                    calendarAdapter.updateHolidays(octoberHolidays)

                    // Mensaje para verificar si internet respondio con exito
                    Toast.makeText(this@MainActivity, "Festivos Octubre: ${octoberHolidays.size}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "Error en el servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PublicHoliday>>, t: Throwable) {
                // Alerta si falla la conexion o el internet
                Toast.makeText(this@MainActivity, "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
