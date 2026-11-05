package com.example.miturnoapp.data

import com.google.gson.annotations.SerializedName

data class HolidayResponse(
    @SerializedName("date") val date: String,
    @SerializedName("name") val name: String,
    @SerializedName("localName") val localName: String?,
    @SerializedName("countryCode") val countryCode: String,
    @SerializedName("nationalHoliday") val nationalHoliday: Boolean,
    @SerializedName("subdivisionCodes") val subdivisionCodes: List<String>?
)