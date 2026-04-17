package com.appclimb.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appclimb.data.model.ClimbingRecordResponse
import com.appclimb.data.repository.RecordRepository
import com.appclimb.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ProfileStats(
    val totalSessions: Int = 0,        // 이번달 방문 횟수
    val totalCompleted: Int = 0,       // 이번달 총 완등 수
    val totalPlanned: Int = 0,         // 이번달 총 시도 수
    val completionRate: Int = 0        // 완등률 %
)

data class ProfileUiState(
    val nickname: String = "",
    val role: String = "",
    val isLoadingRecords: Boolean = false,
    val recentRecords: List<ClimbingRecordResponse> = emptyList(),
    val stats: ProfileStats = ProfileStats(),
    val currentMonth: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _recordState = MutableStateFlow<Pair<Boolean, List<ClimbingRecordResponse>>>(
        Pair(false, emptyList())
    )

    val uiState = combine(
        tokenManager.nickname,
        tokenManager.role,
        _recordState
    ) { nickname, role, (isLoading, records) ->
        val stats = calcStats(records)
        ProfileUiState(
            nickname = nickname ?: "사용자",
            role = when (role) {
                "ADMIN" -> "웹 관리자"
                "MANAGER" -> "지점 관리자"
                else -> "일반 회원"
            },
            isLoadingRecords = isLoading,
            recentRecords = records,
            stats = stats,
            currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy년 M월"))
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    init {
        loadRecentRecords()
    }

    fun loadRecentRecords() {
        val month = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        viewModelScope.launch {
            _recordState.value = Pair(true, _recordState.value.second)
            recordRepository.getMyRecords(month)
                .onSuccess { records ->
                    _recordState.value = Pair(false, records.sortedByDescending { it.recordDate })
                }
                .onFailure {
                    _recordState.value = Pair(false, emptyList())
                }
        }
    }

    private fun calcStats(records: List<ClimbingRecordResponse>): ProfileStats {
        val totalSessions = records.size
        val totalCompleted = records.sumOf { r -> r.entries?.sumOf { it.completedCount } ?: 0 }
        val totalPlanned = records.sumOf { r -> r.entries?.sumOf { it.plannedCount } ?: 0 }
        val rate = if (totalPlanned > 0) (totalCompleted * 100 / totalPlanned) else 0
        return ProfileStats(totalSessions, totalCompleted, totalPlanned, rate)
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearAuth()
            onComplete()
        }
    }
}
