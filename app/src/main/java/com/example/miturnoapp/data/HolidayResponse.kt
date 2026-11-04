package com.example.miturnoapp.data

import com.google.gson.annotations.SerializedName

data class HolidayResponse(
    @SerializedName(value = "date") val date: String,
    @SerializedName(value = "localName") val localName: String,
    @SerializedName(value = "name") val name: String
)
