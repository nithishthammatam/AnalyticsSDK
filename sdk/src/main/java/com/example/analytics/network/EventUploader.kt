package com.example.analytics.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class EventUploader {
    private val apiService: ApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val headerInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Analytics-SDK-Version", "1.0.0")
                .build()
            chain.proceed(request)
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummy.com/") // Retrofit requires a base URL even when using @Url
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    suspend fun uploadBatch(endpoint: String, events: List<com.example.analytics.core.Event>): Boolean {
        return try {
            val response = apiService.uploadEvents(endpoint, events)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
