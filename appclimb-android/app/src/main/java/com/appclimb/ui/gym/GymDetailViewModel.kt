package com.appclimb.ui.gym

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appclimb.data.model.DifficultyColorResponse
import com.appclimb.data.model.GymResponse
import com.appclimb.data.model.SettingScheduleResponse
import com.appclimb.data.repository.GymRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class GymDetailUiState(
    val isLoading: Boolean = true,
    val gym: GymResponse? = null,
    val colors: List<DifficultyColorResponse> = emptyList(),
    val recentSchedules: List<SettingScheduleResponse> = emptyList(),
    val isFavorite: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GymDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gymRepository: GymRepository
) : ViewModel() {

    private val gymId: Long = checkNotNull(savedStateHandle["gymId"])

    private val _uiState = MutableStateFlow(GymDetailUiState())
    val uiState: StateFlow<GymDetailUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val gym = gymRepository.getGym(gymId).getOrThrow()
                val colors = gymRepository.getColors(gymId).getOrElse { emptyList() }
                val month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                val schedules = gymRepository.getSchedules(gymId, month)
                    .getOrElse { emptyList() }
                    .sortedByDescending { it.settingDate }
                    .take(5)
                val favorites = gymRepository.getMyFavorites().getOrElse { emptyList() }
                val isFav = favorites.any { it.gymId == gymId }

                _uiState.value = GymDetailUiState(
                    isLoading = false,
                    gym = gym,
                    colors = colors,
                    recentSchedules = schedules,
                    isFavorite = isFav
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "오류가 발생했습니다"
                )
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentFav = _uiState.value.isFavorite
            if (currentFav) {
                gymRepository.removeFavorite(gymId)
            } else {
                gymRepository.addFavorite(gymId)
            }
            _uiState.value = _uiState.value.copy(isFavorite = !currentFav)
        }
    }
}
