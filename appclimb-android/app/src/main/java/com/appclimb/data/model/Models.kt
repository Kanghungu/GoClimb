package com.appclimb.data.model

import com.google.gson.annotations.SerializedName

// ──────────────── Auth ────────────────

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val nickname: String
)

data class AuthResponse(
    val accessToken: String,
    val tokenType: String,
    val userId: Long,
    val nickname: String,
    val role: String
)

// ──────────────── Gym ────────────────

// 즐겨찾기/선택용 간소화 모델
data class GymSummary(val id: Long, val name: String)

data class GymResponse(
    val id: Long,
    val name: String,
    val address: String?,
    val description: String?,
    val createdAt: String?
)

// ──────────────── Favorite ────────────────

data class FavoriteResponse(
    val gymId: Long,
    val gymName: String,
    val gymAddress: String?
)

// ──────────────── Setting Schedule ────────────────

data class SettingScheduleResponse(
    val id: Long,
    val gymId: Long,
    val sectorId: Long?,
    val sectorName: String?,
    val settingDate: String,
    val description: String?
)

// ──────────────── Climbing Record ────────────────

data class ClimbingRecordResponse(
    val id: Long,
    val gymId: Long,
    val gymName: String?,
    val recordDate: String,
    val entries: List<RecordEntryResponse>?
)

data class RecordEntryResponse(
    val id: Long,
    val colorId: Long,
    val colorName: String?,
    val colorHex: String?,
    val plannedCount: Int,
    val completedCount: Int
)

data class ClimbingRecordRequest(
    val gymId: Long,
    val recordDate: String,
    val entries: List<RecordEntryRequest>
)

data class RecordEntryRequest(
    val colorId: Long,
    val plannedCount: Int,
    val completedCount: Int
)

// ──────────────── Event ────────────────

data class EventResponse(
    val id: Long,
    val gymId: Long,
    val gymName: String?,
    val title: String,
    val description: String?,
    val startDate: String,
    val endDate: String?
)

// ──────────────── FCM ────────────────

data class FcmTokenRequest(
    val token: String,
    val deviceType: String = "ANDROID"
)

// ──────────────── Record Update ────────────────

data class RecordUpdateRequest(
    val entries: List<RecordEntryRequest>
)

// ──────────────── Difficulty Color ────────────────

data class DifficultyColorResponse(
    val id: Long,
    val gymId: Long,
    val colorName: String,
    val colorHex: String,
    val levelOrder: Int
)
