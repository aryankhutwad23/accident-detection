package com.example.myaccidentapplication.data.network

import com.example.myaccidentapplication.data.model.AccidentEvent
import com.example.myaccidentapplication.data.model.AccidentResponse
import com.example.myaccidentapplication.data.model.AlertRequest
import com.example.myaccidentapplication.data.model.LoginRequest
import com.example.myaccidentapplication.data.model.RegisterRequest
import com.example.myaccidentapplication.data.model.RegisterResponse
import com.example.myaccidentapplication.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AccidentApi {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("accidents/report")
    suspend fun reportAccident(@Body accident: AccidentEvent): Response<AccidentResponse>

    @POST("accidents/{userId}/history")
    suspend fun getAccidentHistory(@Path("userId") userId: String): Response<List<AccidentEvent>>

    @POST("alert")
    suspend fun sendEmergencyAlert(@Body request: AlertRequest): Response<AccidentResponse>
}

