package com.example.myaccidentapplication.data.network

import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface TwilioApi {

    @FormUrlEncoded
    @POST("Accounts/{accountSid}/Messages.json")
    suspend fun sendSms(
        @Path("accountSid") accountSid: String,
        @Field("To") to: String,
        @Field("From") from: String,
        @Field("Body") body: String
    ): Response<String>

    companion object {
        fun create(
            accountSid: String,
            authToken: String
        ): TwilioApi {
            val authHeader = Credentials.basic(accountSid, authToken)

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val request = chain.request()
                        .newBuilder()
                        .addHeader("Authorization", authHeader)
                        .build()
                    chain.proceed(request)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.twilio.com/2010-04-01/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(TwilioApi::class.java)
        }
    }
}

object TwilioClient {
    private lateinit var api: TwilioApi
    private lateinit var fromNumber: String
    private lateinit var accountSid: String

    fun initialize(api: TwilioApi, from: String, sid: String) {
        this.api = api
        this.fromNumber = from
        this.accountSid = sid
    }

    suspend fun sendAlertMessage(toNumber: String, message: String): Response<String> {
        if (!::api.isInitialized) {
            throw IllegalStateException("TwilioClient is not initialized")
        }
        return api.sendSms(
            accountSid = accountSid,
            to = toNumber,
            from = fromNumber,
            body = message
        )
    }
}


