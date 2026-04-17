package com.appclimb.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appclimb.data.model.*
import com.appclimb.data.repository.GymRepository
import com.appclimb.data.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class RecordUiState(
    val isLoading: Boolean = false,
    val records: List<ClimbingRecordResponse> = emptyList(),
    val currentMonth: YearMonth = YearMonth.now(),
    val error: String? = null
)

data class AddRecordUiState(
    val isLoading: Boolean = false,
    val gyms: List<GymResponse> = emptyList(),
    val colors: List<DifficultyColorResponse> = emptyList(),
    val selectedGymId: Long? = null,
    val recordDate: String = LocalDate.now().toString(),
    val entries: Map<Long, Pair<Int, Int>> = emptyMap(), // colorId -> (planned, completed)
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState = _uiState.asStateFlow()

    private val _addState = MutableStateFlow(AddRecordUiState())
    val addState = _addState.asStateFlow()

    init {
        loadRecords()
        loadGyms()
    }

    fun loadRecords() {
        val month = _uiState.value.currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            recordRepository.getMyRecords(month)
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, records = it) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun prevMonth() {
        _uiState.value = _uiState.value.copy(currentMonth = _uiState.value.currentMonth.minusMonths(1))
        loadRecords()
    }

    fun nextMonth() {
        _uiState.value = _uiState.value.copy(currentMonth = _uiState.value.currentMonth.plusMonths(1))
        loadRecords()
    }

    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            recordRepository.deleteRecord(recordId).onSuccess { loadRecords() }
        }
    }

    private fun loadGyms() {
        viewModelScope.launch {
            gymRepository.getAllGyms().onSuccess { gyms ->
                _addState.value = _addState.value.copy(gyms = gyms)
                gyms.firstOrNull()?.let { selectGym(it.id) }
            }
        }
    }

    fun selectGym(gymId: Long) {
        _addState.value = _addState.value.copy(selectedGymId = gymId, entries = emptyMap())
        viewModelScope.launch {
            gymRepository.getColors(gymId).onSuccess { colors ->
                _addState.value = _addState.value.copy(colors = colors)
            }
        }
    }

    fun setDate(date: String) {
        _addState.value = _addState.value.copy(recordDate = date)
    }

    fun updateEntry(colorId: Long, planned: Int, completed: Int) {
        val entries = _addState.value.entries.toMutableMap()
        entries[colorId] = Pair(planned, completed)
        _addState.value = _addState.value.copy(entries = entries)
    }

    fun saveRecord() {
        val state = _addState.value
        val gymId = state.selectedGymId ?: return
        viewModelScope.launch {
            _addState.value = state.copy(isLoading = true)
            val request = ClimbingRecordRequest(
                gymId = gymId,
                recordDate = state.recordDate,
                entries = state.entries.map { (colorId, counts) ->
                    RecordEntryRequest(colorId, counts.first, counts.second)
                }.filter { it.plannedCount > 0 || it.completedCount > 0 }
            )
            recordRepository.createRecord(request)
                .onSuccess {
                    _addState.value = AddRecordUiState(isSuccess = true)
                    loadRecords()
                }
                .onFailure { _addState.value = state.copy(isLoading = false, error = it.message) }
        }
    }

    fun resetAddState() {
        _addState.value = AddRecordUiState()
        loadGyms()
    }
}
