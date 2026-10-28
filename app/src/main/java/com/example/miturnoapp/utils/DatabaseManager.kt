package com.example.miturnoapp.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.miturnoapp.data.DayAssignment
import com.example.miturnoapp.data.ShiftTemplate

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Llama a la creación de las dos tablas
        db.execSQL(ShiftTemplate.SQL_CREATE_TABLE)
        db.execSQL(DayAssignment.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onDestroy(db)
        onCreate(db)
    }

    fun onDestroy(db: SQLiteDatabase) {
        // Borramos primero la tabla del calendario porque depende de la de turnos
        db.execSQL(DayAssignment.SQL_DROP_TABLE)
        db.execSQL(ShiftTemplate.SQL_DROP_TABLE)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "MiTurnoApp.db"
    }
}

