package com.appclimb.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appclimb.data.model.ClimbingRecordResponse
import com.appclimb.data.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ColorStat(
    val colorId: Long,
    val colorName: String,
    val colorHex: String,
    val totalPlanned: Int,
    val totalCompleted: Int
)

data class MonthlyStat(
    val month: String,       // "04월"
    val totalCompleted: Int
)

data class StatsUiState(
    val isLoading: Boolean = false,
    val colorStats: List<ColorStat> = emptyList(),
    val monthlyStats: List<MonthlyStat> = emptyList(),
    val totalSessions: Int = 0,
    val totalCompleted: Int = 0,
    val bestColor: String? = null,
    val error: String? = null
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState = _uiState.asStateFlow()

    init { loadStats() }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // 최근 6개월 기록 수집
            val allRecords = mutableListOf<ClimbingRecordResponse>()
            val monthlyStats = mutableListOf<MonthlyStat>()
            val now = YearMonth.now()

            for (i in 5 downTo 0) {
                val ym = now.minusMonths(i.toLong())
                val monthStr = ym.format(DateTimeFormatter.ofPattern("yyyy-MM"))
                recordRepository.getMyRecords(monthStr).onSuccess { records ->
                    allRecords.addAll(records)
                    val completed = records.sumOf { r -> r.entries?.sumOf { it.completedCount } ?: 0 }
                    monthlyStats.add(MonthlyStat(
                        month = ym.format(DateTimeFormatter.ofPattern("MM월")),
                        totalCompleted = completed
                    ))
                }
            }

            // 난이도별 누적 통계
            val colorMap = mutableMapOf<Long, ColorStat>()
            allRecords.forEach { record ->
                record.entries?.forEach { entry ->
                    val existing = colorMap[entry.colorId]
                    colorMap[entry.colorId] = ColorStat(
                        colorId = entry.colorId,
                        colorName = entry.colorName ?: "알 수 없음",
                        colorHex = entry.colorHex ?: "#888888",
                        totalPlanned = (existing?.totalPlanned ?: 0) + entry.plannedCount,
                        totalCompleted = (existing?.totalCompleted ?: 0) + entry.completedCount
                    )
                }
            }

            val colorStats = colorMap.values.sortedByDescending { it.totalCompleted }
            val totalCompleted = colorStats.sumOf { it.totalCompleted }
            val bestColor = colorStats.firstOrNull()?.colorName

            _uiState.value = StatsUiState(
                isLoading = false,
                colorStats = colorStats,
                monthlyStats = monthlyStats,
                totalSessions = allRecords.size,
                totalCompleted = totalCompleted,
                bestColor = bestColor
            )
        }
    }
}
