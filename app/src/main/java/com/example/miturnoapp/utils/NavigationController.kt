package com.example.miturnoapp.utils

import android.view.View
import com.example.miturnoapp.databinding.ActivityMainBinding

class NavigationController(private val binding: ActivityMainBinding) {

    fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            // Verificamos si la pestaña pulsada es la de calendario
            val isCalendarSelected = item.itemId == com.example.miturnoapp.R.id.nav_calendar

            // Si es calendario se muestra (VISIBLE), si no, se oculta (GONE)
            val calendarVisibility = if (isCalendarSelected) View.VISIBLE else View.GONE

            binding.layoutMonthNavigator.root.visibility = calendarVisibility
            binding.layoutStatsCards.root.visibility = calendarVisibility
            binding.rvCalendarGrid.visibility = calendarVisibility

            // El bloque inferior de detalles se mantiene visible en ambos modos
            binding.layoutDayDetail.root.visibility = View.VISIBLE
            true
        }
    }
}
