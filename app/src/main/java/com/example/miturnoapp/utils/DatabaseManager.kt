package com.example.miturnoapp.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.miturnoapp.data.DayAssignment
import com.example.miturnoapp.data.ShiftTemplate

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Configuración inicial de base de datos
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        // Activa el soporte nativo de claves foráneas en SQLite
        db.setForeignKeyConstraintsEnabled(true)
    }

    // Creación de esquemas nativos
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ShiftTemplate.SQL_CREATE_TABLE)
        db.execSQL(DayAssignment.SQL_CREATE_TABLE)
    }

    // Actualización de versión de base de datos
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        clearTables(db)
        onCreate(db)
    }

    // Eliminación segura de tablas existentes
    private fun clearTables(db: SQLiteDatabase) {
        db.execSQL(DayAssignment.SQL_DROP_TABLE)
        db.execSQL(ShiftTemplate.SQL_DROP_TABLE)
    }

    // Constantes de configuración global
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "MiTurnoApp.db"
    }
}
