package com.appclimb.data.repository

import com.appclimb.data.api.ApiService
import com.appclimb.data.model.AuthResponse
import com.appclimb.data.model.LoginRequest
import com.appclimb.data.model.RegisterRequest
import com.appclimb.util.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(email: String, password: String): Result<AuthResponse> = runCatching {
        val response = api.login(LoginRequest(email, password))
        tokenManager.saveAuth(response.accessToken, response.userId, response.nickname, response.role)
        response
    }

    suspend fun register(email: String, password: String, nickname: String): Result<AuthResponse> = runCatching {
        val response = api.register(RegisterRequest(email, password, nickname))
        tokenManager.saveAuth(response.accessToken, response.userId, response.nickname, response.role)
        response
    }

    suspend fun logout() {
        tokenManager.clearAuth()
    }

    val token = tokenManager.token
    val nickname = tokenManager.nickname
    val role = tokenManager.role
}
