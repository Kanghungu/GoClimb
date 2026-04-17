package com.appclimb.data.repository

import com.appclimb.data.api.ApiService
import com.appclimb.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GymRepository @Inject constructor(private val api: ApiService) {

    suspend fun getAllGyms(): Result<List<GymResponse>> = runCatching { api.getAllGyms() }

    suspend fun getGym(gymId: Long): Result<GymResponse> = runCatching { api.getGym(gymId) }

    suspend fun getMyFavorites(): Result<List<FavoriteResponse>> = runCatching { api.getMyFavorites() }

    suspend fun addFavorite(gymId: Long): Result<Unit> = runCatching { api.addFavorite(gymId) }

    suspend fun removeFavorite(gymId: Long): Result<Unit> = runCatching { api.removeFavorite(gymId) }

    suspend fun getSchedules(gymId: Long, month: String? = null): Result<List<SettingScheduleResponse>> =
        runCatching { api.getSchedules(gymId, month) }

    suspend fun getColors(gymId: Long): Result<List<DifficultyColorResponse>> =
        runCatching { api.getColors(gymId) }
}
