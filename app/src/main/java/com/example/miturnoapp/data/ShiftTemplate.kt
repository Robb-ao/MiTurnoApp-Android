package com.example.miturnoapp.data
 //Plantilla de turnos
data class ShiftTemplate(
    val id: Int,
    val labelName: String,
    val startTime: String,
    val endTime: String,
    val colorCode: String
) {
    companion object {
        const val TABLE_NAME = "shift_templates"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_LABEL = "label_name"
        const val COLUMN_NAME_START = "start_time"
        const val COLUMN_NAME_END = "end_time"
        const val COLUMN_NAME_COLOR = "color_code"


        const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_NAME_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME_LABEL TEXT, " +
                "$COLUMN_NAME_START TEXT, " +
                "$COLUMN_NAME_END TEXT, " +
                "$COLUMN_NAME_COLOR TEXT)"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
