package com.appclimb.ui.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appclimb.data.model.EventResponse
import com.appclimb.data.model.GymSummary
import com.appclimb.data.repository.EventRepository
import com.appclimb.data.repository.GymRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventUiState(
    val isLoading: Boolean = false,
    val favoriteGyms: List<GymSummary> = emptyList(),
    val selectedGymId: Long? = null,
    val events: List<EventResponse> = emptyList(),
    val noFavorites: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EventViewModel @Inject constructor(
    private val gymRepository: GymRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventUiState())
    val uiState = _uiState.asStateFlow()

    init { loadFavoriteGyms() }

    fun loadFavoriteGyms() {
        viewModelScope.launch {
            gymRepository.getMyFavorites()
                .onSuccess { favorites ->
                    val summaries = favorites.map { GymSummary(it.gymId, it.gymName) }
                    if (summaries.isEmpty()) {
                        _uiState.value = _uiState.value.copy(noFavorites = true, isLoading = false)
                    } else {
                        _uiState.value = _uiState.value.copy(favoriteGyms = summaries, noFavorites = false)
                        summaries.first().let { selectGym(it.id) }
                    }
                }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message, isLoading = false) }
        }
    }

    fun selectGym(gymId: Long) {
        _uiState.value = _uiState.value.copy(selectedGymId = gymId, isLoading = true)
        viewModelScope.launch {
            eventRepository.getEvents(gymId)
                .onSuccess { events ->
                    _uiState.value = _uiState.value.copy(
                        events = events.sortedBy { it.startDate },
                        isLoading = false
                    )
                }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
        }
    }
}
