package com.example.miturnoapp.data

import retrofit2.http.GET
import retrofit2.http.Path

interface HolidayApiService {
    // Definimos la sub-ruta web dinámica usando Retrofit
    @GET("api/v3/PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): List<HolidayResponse>
}

