package com.appclimb.ui.gym

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appclimb.data.model.FavoriteResponse
import com.appclimb.data.model.GymResponse
import com.appclimb.data.repository.GymRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GymUiState(
    val isLoading: Boolean = false,
    val gyms: List<GymResponse> = emptyList(),
    val favorites: Set<Long> = emptySet(),
    val error: String? = null
)

@HiltViewModel
class GymViewModel @Inject constructor(
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GymUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val gymsResult = gymRepository.getAllGyms()
            val favoritesResult = gymRepository.getMyFavorites()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                gyms = gymsResult.getOrDefault(emptyList()),
                favorites = favoritesResult.getOrDefault(emptyList()).map { it.gymId }.toSet(),
                error = gymsResult.exceptionOrNull()?.message
            )
        }
    }

    fun toggleFavorite(gymId: Long) {
        viewModelScope.launch {
            val isFav = gymId in _uiState.value.favorites
            if (isFav) {
                gymRepository.removeFavorite(gymId)
                _uiState.value = _uiState.value.copy(
                    favorites = _uiState.value.favorites - gymId
                )
            } else {
                gymRepository.addFavorite(gymId)
                _uiState.value = _uiState.value.copy(
                    favorites = _uiState.value.favorites + gymId
                )
            }
        }
    }
}
