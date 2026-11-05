package com.example.miturnoapp.data

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/v4/Holidays/{countryCode}/{year}")
    suspend fun getPublicHolidays(
        @Path("countryCode") countryCode: String,
        @Path("year") year: Int
    ): List<HolidayResponse>
}