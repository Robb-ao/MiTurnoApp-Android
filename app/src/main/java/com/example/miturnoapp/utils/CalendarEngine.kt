package com.example.miturnoapp.utils

import java.util.Calendar

class CalendarEngine {

    fun generateDaysForMonth(year: Int, month: Int): List<String> {
        val days = mutableListOf<String>()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // En Java, los meses van de 0 a 11
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val totalDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val spacesBeforeValue = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2

        for (i in 0 until spacesBeforeValue) {
            days.add("")
        }

        for (i in 1..totalDaysInMonth) {
            days.add(i.toString())
        }

        return days
    }

    fun getMonthName(month: Int): String {
        val names = arrayOf(
            "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
            "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
        )
        return names[month - 1]
    }
}

