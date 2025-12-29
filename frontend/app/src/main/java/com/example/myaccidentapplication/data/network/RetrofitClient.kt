package com.example.myaccidentapplication.data.network

import com.example.myaccidentapplication.data.model.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Response

// Base URL for local server in emulator
private const val BASE_URL = "http://10.0.2.2:8080/api/"

private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

// ---------- Auth Client ----------
object AuthRetrofitClient {
    val api: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}

// ---------- Alert Client (Twilio SMS) ----------
object AlertRetrofitClient {
    val api: AlertApi by lazy {
        retrofit.create(AlertApi::class.java)
    }
}

// ---------- AUTH API ----------
interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse>
}

// ---------- ALERT / TWILIO API ----------
interface AlertApi {
    @POST("alert/send")
    suspend fun sendEmergencyAlert(
        @Query("userId") userId: Int,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?        // ❌ Fixed missing type
    ): Response<ApiResponse>
}
