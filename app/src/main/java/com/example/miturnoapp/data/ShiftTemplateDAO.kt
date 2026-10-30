package com.example.miturnoapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.miturnoapp.utils.DatabaseManager

class ShiftTemplateDAO(context: Context) {

    // Abre la conexión con bd usando el Manager
    private val dbManager = DatabaseManager(context)

    // Inserta un nuevo turno en bd
    fun insertShift(label: String, start: String, end: String, color: String): Long {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put(ShiftTemplate.COLUMN_NAME_LABEL, label)
            put(ShiftTemplate.COLUMN_NAME_START, start)
            put(ShiftTemplate.COLUMN_NAME_END, end)
            put(ShiftTemplate.COLUMN_NAME_COLOR, color)
        }
        val id = db.insert(ShiftTemplate.TABLE_NAME, null, values)
        db.close()
        return id
    }

    // Devuelve la lista completa de turnos creados
    fun getAllShifts(): List<ShiftTemplate> {
        val shiftList = mutableListOf<ShiftTemplate>()
        val db = dbManager.readableDatabase

        val cursor: Cursor = db.rawQuery("SELECT * FROM ${ShiftTemplate.TABLE_NAME}", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(ShiftTemplate.COLUMN_NAME_ID))
                val label = cursor.getString(cursor.getColumnIndexOrThrow(ShiftTemplate.COLUMN_NAME_LABEL))
                val start = cursor.getString(cursor.getColumnIndexOrThrow(ShiftTemplate.COLUMN_NAME_START))
                val end = cursor.getString(cursor.getColumnIndexOrThrow(ShiftTemplate.COLUMN_NAME_END))
                val color = cursor.getString(cursor.getColumnIndexOrThrow(ShiftTemplate.COLUMN_NAME_COLOR))

                shiftList.add(ShiftTemplate(id, label, start, end, color))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return shiftList
    }

    // Guarda el turno en una fecha especifica
    fun assignShiftToDay(date: String, shiftId: Int, notes: String): Long {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put(DayAssignment.COLUMN_NAME_DATE, date)
            put(DayAssignment.COLUMN_NAME_SHIFT_ID, shiftId)
            put(DayAssignment.COLUMN_NAME_NOTES, notes)
        }
        val id = db.insertWithOnConflict(DayAssignment.TABLE_NAME, null, values, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return id
    }
}
