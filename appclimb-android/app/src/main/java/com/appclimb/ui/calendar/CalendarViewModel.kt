package com.appclimb.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appclimb.data.model.GymResponse
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
    val gyms: List<GymResponse> = emptyList(),
    val selectedGymId: Long? = null,
    val schedules: List<SettingScheduleResponse> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadGyms()
    }

    private fun loadGyms() {
        viewModelScope.launch {
            gymRepository.getAllGyms()
                .onSuccess { gyms ->
                    _uiState.value = _uiState.value.copy(gyms = gyms)
                    // 첫 번째 지점 자동 선택
                    gyms.firstOrNull()?.let { selectGym(it.id) }
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
