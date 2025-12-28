package com.example.myaccidentapplication.data.network

import com.example.myaccidentapplication.data.model.AccidentResponse
import com.example.myaccidentapplication.data.model.RegisterRequest
import com.example.myaccidentapplication.data.model.LoginRequest
import com.example.myaccidentapplication.data.model.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

// RetrofitClient for Auth
object AuthRetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/api/auth/"

    val api: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}

interface AuthApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AccidentResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AccidentResponse>
}