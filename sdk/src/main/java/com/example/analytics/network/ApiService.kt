package com.example.analytics.network

import com.example.analytics.core.Event
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @POST
    suspend fun uploadEvents(@Url url: String, @Body events: List<Event>): Response<Unit>
}
