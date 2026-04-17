package com.appclimb.ui.gym

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appclimb.data.model.GymResponse
import com.appclimb.data.repository.GymRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GymUiState(
    val isLoading: Boolean = false,
    val allGyms: List<GymResponse> = emptyList(),      // 전체 목록
    val filteredGyms: List<GymResponse> = emptyList(), // 검색 결과
    val favorites: Set<Long> = emptySet(),
    val searchQuery: String = "",
    val showFavoritesOnly: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GymViewModel @Inject constructor(
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GymUiState())
    val uiState = _uiState.asStateFlow()

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val gymsResult = gymRepository.getAllGyms()
            val favoritesResult = gymRepository.getMyFavorites()

            val gyms = gymsResult.getOrDefault(emptyList())
            val favIds = favoritesResult.getOrDefault(emptyList()).map { it.gymId }.toSet()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                allGyms = gyms,
                favorites = favIds,
                error = gymsResult.exceptionOrNull()?.message
            )
            applyFilter()
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilter()
    }

    fun toggleFavoritesOnly() {
        _uiState.value = _uiState.value.copy(
            showFavoritesOnly = !_uiState.value.showFavoritesOnly
        )
        applyFilter()
    }

    private fun applyFilter() {
        val state = _uiState.value
        var list = state.allGyms
        if (state.showFavoritesOnly) {
            list = list.filter { it.id in state.favorites }
        }
        if (state.searchQuery.isNotBlank()) {
            list = list.filter {
                it.name.contains(state.searchQuery, ignoreCase = true) ||
                it.address?.contains(state.searchQuery, ignoreCase = true) == true
            }
        }
        _uiState.value = state.copy(filteredGyms = list)
    }

    fun toggleFavorite(gymId: Long) {
        viewModelScope.launch {
            val isFav = gymId in _uiState.value.favorites
            if (isFav) {
                gymRepository.removeFavorite(gymId)
                _uiState.value = _uiState.value.copy(favorites = _uiState.value.favorites - gymId)
            } else {
                gymRepository.addFavorite(gymId)
                _uiState.value = _uiState.value.copy(favorites = _uiState.value.favorites + gymId)
            }
            applyFilter()
        }
    }
}
