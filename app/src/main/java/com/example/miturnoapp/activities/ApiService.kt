package com.example.miturnoapp

import com.example.miturnoapp.data.PublicHoliday
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/v4/Holidays/{countryCode}/{year}")
    fun getHolidays(
        @Path("countryCode") countryCode: String,
        @Path("year") year: Int
    ): Call<List<PublicHoliday>>
}
