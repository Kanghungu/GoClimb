package com.appclimb.ui.event

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appclimb.data.model.EventResponse
import com.appclimb.ui.calendar.GymSelector
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(viewModel: EventViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val today = LocalDate.now()

    Scaffold(
        topBar = { TopAppBar(title = { Text("🎉 이벤트") }) }
    ) { padding ->

        if (uiState.noFavorites) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, null,
                        modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.height(12.dp))
                    Text("즐겨찾기한 지점이 없습니다.", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    Text("지점 탭에서 ♡를 눌러 즐겨찾기를 추가하세요.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp))
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.favoriteGyms.isNotEmpty()) {
                item {
                    GymSelector(
                        gyms = uiState.favoriteGyms.map { it.id to it.name },
                        selectedGymId = uiState.selectedGymId,
                        onGymSelect = { viewModel.selectGym(it) }
                    )
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.events.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Event, null,
                                modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.height(8.dp))
                            Text("진행 중인 이벤트가 없습니다.",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                // 진행중 / 예정 / 종료로 분류
                val ongoing = uiState.events.filter {
                    val start = LocalDate.parse(it.startDate)
                    val end = it.endDate?.let { e -> LocalDate.parse(e) }
                    start <= today && (end == null || end >= today)
                }
                val upcoming = uiState.events.filter {
                    LocalDate.parse(it.startDate) > today
                }
                val past = uiState.events.filter {
                    val end = it.endDate?.let { e -> LocalDate.parse(e) }
                    end != null && end < today
                }

                if (ongoing.isNotEmpty()) {
                    item { SectionLabel("진행중", MaterialTheme.colorScheme.primary) }
                    items(ongoing) { EventCard(it, EventStatus.ONGOING) }
                }
                if (upcoming.isNotEmpty()) {
                    item { SectionLabel("예정", MaterialTheme.colorScheme.secondary) }
                    items(upcoming) { EventCard(it, EventStatus.UPCOMING) }
                }
                if (past.isNotEmpty()) {
                    item { SectionLabel("종료", MaterialTheme.colorScheme.outline) }
                    items(past) { EventCard(it, EventStatus.PAST) }
                }
            }
        }
    }
}

enum class EventStatus { ONGOING, UPCOMING, PAST }

@Composable
private fun SectionLabel(text: String, color: androidx.compose.ui.graphics.Color) {
    Text(text, style = MaterialTheme.typography.labelMedium,
        color = color, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
private fun EventCard(event: EventResponse, status: EventStatus) {
    val fmt = DateTimeFormatter.ofPattern("MM.dd")
    val dateText = buildString {
        append(LocalDate.parse(event.startDate).format(fmt))
        event.endDate?.let { append(" ~ ${LocalDate.parse(it).format(fmt)}") }
    }

    val containerColor = when (status) {
        EventStatus.ONGOING -> MaterialTheme.colorScheme.primaryContainer
        EventStatus.UPCOMING -> MaterialTheme.colorScheme.surfaceVariant
        EventStatus.PAST -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(if (status == EventStatus.ONGOING) 3.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(event.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f))

                Surface(
                    color = when (status) {
                        EventStatus.ONGOING -> MaterialTheme.colorScheme.primary
                        EventStatus.UPCOMING -> MaterialTheme.colorScheme.secondary
                        EventStatus.PAST -> MaterialTheme.colorScheme.outline
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when (status) {
                            EventStatus.ONGOING -> "진행중"
                            EventStatus.UPCOMING -> "예정"
                            EventStatus.PAST -> "종료"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(dateText, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary)

            event.description?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
