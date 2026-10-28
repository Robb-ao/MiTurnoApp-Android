package com.example.miturnoapp.data

import com.google.gson.annotations.SerializedName

data class HolidayResponse(
    @SerializedName("date") val date: String,
    @SerializedName("localName") val localName: String
)
