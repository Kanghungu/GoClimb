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
    val favoriteGyms: List<GymSummary> = emptyList(),
    val colors: List<DifficultyColorResponse> = emptyList(),
    val selectedGymId: Long? = null,
    val recordDate: String = LocalDate.now().toString(),
    val entries: Map<Long, Pair<Int, Int>> = emptyMap(), // colorId -> (planned, completed)
    val editingRecordId: Long? = null,    // null = 새 기록, not null = 수정 중
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
        loadFavoriteGyms()
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

    // 수정 모드로 바텀시트 열기
    fun startEdit(record: ClimbingRecordResponse) {
        viewModelScope.launch {
            // 해당 기록의 색깔 목록 로드
            gymRepository.getColors(record.gymId).onSuccess { colors ->
                val entries = record.entries?.associate { entry ->
                    entry.colorId to Pair(entry.plannedCount, entry.completedCount)
                } ?: emptyMap()

                _addState.value = AddRecordUiState(
                    favoriteGyms = _addState.value.favoriteGyms,
                    colors = colors.sortedBy { it.levelOrder },
                    selectedGymId = record.gymId,
                    recordDate = record.recordDate,
                    entries = entries,
                    editingRecordId = record.id
                )
            }
        }
    }

    private fun loadFavoriteGyms() {
        viewModelScope.launch {
            gymRepository.getMyFavorites().onSuccess { favorites ->
                val summaries = favorites.map { GymSummary(it.gymId, it.gymName) }
                _addState.value = _addState.value.copy(favoriteGyms = summaries)
                summaries.firstOrNull()?.let { selectGym(it.id) }
            }
        }
    }

    fun selectGym(gymId: Long) {
        _addState.value = _addState.value.copy(selectedGymId = gymId, entries = emptyMap())
        viewModelScope.launch {
            gymRepository.getColors(gymId).onSuccess { colors ->
                _addState.value = _addState.value.copy(colors = colors.sortedBy { it.levelOrder })
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
        viewModelScope.launch {
            _addState.value = state.copy(isLoading = true)
            val filteredEntries = state.entries.map { (colorId, counts) ->
                RecordEntryRequest(colorId, counts.first, counts.second)
            }.filter { it.plannedCount > 0 || it.completedCount > 0 }

            if (state.editingRecordId != null) {
                // 수정 모드
                val request = RecordUpdateRequest(entries = filteredEntries)
                recordRepository.updateRecord(state.editingRecordId, request)
                    .onSuccess {
                        _addState.value = AddRecordUiState(isSuccess = true)
                        loadRecords()
                    }
                    .onFailure { _addState.value = state.copy(isLoading = false, error = it.message) }
            } else {
                // 새 기록
                val gymId = state.selectedGymId ?: return@launch
                val request = ClimbingRecordRequest(
                    gymId = gymId,
                    recordDate = state.recordDate,
                    entries = filteredEntries
                )
                recordRepository.createRecord(request)
                    .onSuccess {
                        _addState.value = AddRecordUiState(isSuccess = true)
                        loadRecords()
                    }
                    .onFailure { _addState.value = state.copy(isLoading = false, error = it.message) }
            }
        }
    }

    fun resetAddState() {
        _addState.value = AddRecordUiState()
        loadFavoriteGyms()
    }
}
