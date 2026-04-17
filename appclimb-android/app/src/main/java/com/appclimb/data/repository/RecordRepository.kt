package com.appclimb.data.repository

import com.appclimb.data.api.ApiService
import com.appclimb.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepository @Inject constructor(private val api: ApiService) {

    suspend fun getMyRecords(month: String? = null): Result<List<ClimbingRecordResponse>> =
        runCatching { api.getMyRecords(month) }

    suspend fun createRecord(request: ClimbingRecordRequest): Result<ClimbingRecordResponse> =
        runCatching { api.createRecord(request) }

    suspend fun deleteRecord(recordId: Long): Result<Unit> =
        runCatching { api.deleteRecord(recordId) }
}
