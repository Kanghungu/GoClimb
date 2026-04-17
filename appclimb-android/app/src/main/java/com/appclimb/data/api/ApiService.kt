package com.appclimb.data.api

import com.appclimb.data.model.*
import retrofit2.http.*

interface ApiService {

    // ──────────── Auth ────────────
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    // ──────────── Gym ────────────
    @GET("gyms")
    suspend fun getAllGyms(): List<GymResponse>

    @GET("gyms/{gymId}")
    suspend fun getGym(@Path("gymId") gymId: Long): GymResponse

    // ──────────── Favorite ────────────
    @GET("favorites")
    suspend fun getMyFavorites(): List<FavoriteResponse>

    @POST("favorites/{gymId}")
    suspend fun addFavorite(@Path("gymId") gymId: Long)

    @DELETE("favorites/{gymId}")
    suspend fun removeFavorite(@Path("gymId") gymId: Long)

    // ──────────── Setting Schedule ────────────
    @GET("gyms/{gymId}/schedules")
    suspend fun getSchedules(
        @Path("gymId") gymId: Long,
        @Query("month") month: String? = null
    ): List<SettingScheduleResponse>

    // ──────────── Climbing Record ────────────
    @GET("records")
    suspend fun getMyRecords(
        @Query("month") month: String? = null
    ): List<ClimbingRecordResponse>

    @POST("records")
    suspend fun createRecord(@Body request: ClimbingRecordRequest): ClimbingRecordResponse

    @DELETE("records/{recordId}")
    suspend fun deleteRecord(@Path("recordId") recordId: Long)

    // ──────────── Difficulty Color ────────────
    @GET("gyms/{gymId}/colors")
    suspend fun getColors(@Path("gymId") gymId: Long): List<DifficultyColorResponse>
}
