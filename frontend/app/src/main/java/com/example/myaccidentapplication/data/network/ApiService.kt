package com.example.myaccidentapplication.data.network

import com.example.myaccidentapplication.data.model.AccidentResponse
import com.example.myaccidentapplication.data.model.AlertRequest
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.POST

interface ApiService {

    // GET request - Fetch accident details
    @GET("yourEndpoint")
    fun getAccidentDetails(
        @Query("location") location: String,
        @Query("date") date: String
    ): Call<AccidentResponse>

    // Example POST request (optional)
    @POST("sendAlert")
    fun sendAlert(
        @Query("message") message: String
    ): Call<AccidentResponse>
}
