package com.appclimb.data.repository

import com.appclimb.data.api.ApiService
import com.appclimb.data.model.EventResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(private val api: ApiService) {
    suspend fun getEvents(gymId: Long): Result<List<EventResponse>> =
        runCatching { api.getEvents(gymId) }
}
