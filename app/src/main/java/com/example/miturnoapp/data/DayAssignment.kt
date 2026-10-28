package com.example.miturnoapp.data
//Asignación del día
data class DayAssignment(
    val date: String,
    val assignedShiftId: Int,
    val notesText: String
) {
    companion object {
        const val TABLE_NAME = "day_assignments"
        const val COLUMN_NAME_DATE = "date_key"
        const val COLUMN_NAME_SHIFT_ID = "assigned_shift_id"
        const val COLUMN_NAME_NOTES = "notes_text"

        // Comando SQL relacional vinculando la clave foránea a la tabla de arriba
        const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_NAME_DATE TEXT PRIMARY KEY, " +
                "$COLUMN_NAME_SHIFT_ID INTEGER, " +
                "$COLUMN_NAME_NOTES TEXT, " +
                "FOREIGN KEY($COLUMN_NAME_SHIFT_ID) REFERENCES ${ShiftTemplate.TABLE_NAME}(${ShiftTemplate.COLUMN_NAME_ID}) ON DELETE CASCADE)"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
