package com.example.caretaker.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

interface ApiService {
    @POST("caretaker/register")
    suspend fun register(@Body payload: CaretakerRegisterRequest): CaretakerLoginResponse

    @POST("caretaker/login")
    suspend fun login(@Body payload: CaretakerLoginRequest): CaretakerLoginResponse

    @GET("sos/alerts/{caregiver_phone}")
    suspend fun getSosAlerts(@Path("caregiver_phone") phone: String): SosAlertsResponse
}

object NetworkClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    fun createService(baseUrl: String): ApiService {
        val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(formattedUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
