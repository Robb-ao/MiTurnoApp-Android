package com.example.miturnoapp.data

import retrofit3.http.GET
import retrofit3.http.Path

interface HolidayApiService {
    // Definimos la sub-ruta web dinámica usando la versión 3 de Retrofit que pusimos en tu Gradle
    @GET("api/v3/PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): List<HolidayResponse>
}
