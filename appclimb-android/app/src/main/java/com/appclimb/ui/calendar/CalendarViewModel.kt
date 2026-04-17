package com.appclimb.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appclimb.data.model.GymSummary
import com.appclimb.data.model.SettingScheduleResponse
import com.appclimb.data.repository.GymRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class CalendarUiState(
    val isLoading: Boolean = false,
    val currentMonth: YearMonth = YearMonth.now(),
    val favoriteGyms: List<GymSummary> = emptyList(),   // 즐겨찾기 지점만
    val selectedGymId: Long? = null,
    val schedules: List<SettingScheduleResponse> = emptyList(),
    val noFavorites: Boolean = false,   // 즐겨찾기가 없을 때 안내
    val error: String? = null
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadFavoriteGyms()
    }

    fun loadFavoriteGyms() {
        viewModelScope.launch {
            gymRepository.getMyFavorites()
                .onSuccess { favorites ->
                    val summaries = favorites.map { GymSummary(it.gymId, it.gymName) }
                    if (summaries.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            favoriteGyms = emptyList(),
                            noFavorites = true
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            favoriteGyms = summaries,
                            noFavorites = false
                        )
                        // 첫 번째 즐겨찾기 지점 자동 선택
                        summaries.firstOrNull()?.let { selectGym(it.id) }
                    }
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(error = it.message)
                }
        }
    }

    fun selectGym(gymId: Long) {
        _uiState.value = _uiState.value.copy(selectedGymId = gymId)
        loadSchedules()
    }

    fun prevMonth() {
        _uiState.value = _uiState.value.copy(
            currentMonth = _uiState.value.currentMonth.minusMonths(1)
        )
        loadSchedules()
    }

    fun nextMonth() {
        _uiState.value = _uiState.value.copy(
            currentMonth = _uiState.value.currentMonth.plusMonths(1)
        )
        loadSchedules()
    }

    private fun loadSchedules() {
        val gymId = _uiState.value.selectedGymId ?: return
        val month = _uiState.value.currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            gymRepository.getSchedules(gymId, month)
                .onSuccess { schedules ->
                    _uiState.value = _uiState.value.copy(isLoading = false, schedules = schedules)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                }
        }
    }
}
