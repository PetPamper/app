package com.android.PetPamper.location

import com.android.PetPamper.location.LocationResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimAPI {
    @GET("search")
    fun getCoordinates(
        @Query("q") query: String,
        @Query("format") format: String = "json"
    ): Call<List<LocationResult>>
}
