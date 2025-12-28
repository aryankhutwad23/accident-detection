package com.example.myaccidentapplication.di

import android.content.Context
import com.example.myaccidentapplication.data.local.UserPreferences
import com.example.myaccidentapplication.data.network.AccidentApi
import com.example.myaccidentapplication.data.network.TwilioApi
import com.example.myaccidentapplication.data.network.TwilioClient
import com.example.myaccidentapplication.data.repository.AccidentRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(private val context: Context) {
    private val baseUrl = "http://10.0.2.2:8080/api/auth/" // Replace with your backend URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val accidentApi: AccidentApi = retrofit.create(AccidentApi::class.java)

    // TODO: Replace with secure storage / config for production
    private val twilioAccountSid = "YOUR_TWILIO_ACCOUNT_SID"
    private val twilioAuthToken = "YOUR_TWILIO_AUTH_TOKEN"
    val twilioFromNumber = "YOUR_TWILIO_PHONE_NUMBER"

    val twilioApi: TwilioApi = TwilioApi.create(
        accountSid = twilioAccountSid,
        authToken = twilioAuthToken
    )

    init {
        TwilioClient.initialize(
            api = twilioApi,
            from = twilioFromNumber,
            sid = twilioAccountSid
        )
    }

    val userPreferences: UserPreferences = UserPreferences(context)

    val accidentRepository: AccidentRepository = AccidentRepository(
        api = accidentApi,
        userPreferences = userPreferences
    )
}

