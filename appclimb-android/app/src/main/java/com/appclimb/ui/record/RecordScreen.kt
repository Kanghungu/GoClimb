package com.appclimb.ui.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appclimb.data.model.ClimbingRecordResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(viewModel: RecordViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💪 운동 기록") },
                actions = {
                    IconButton(onClick = { viewModel.prevMonth() }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "이전 달")
                    }
                    Text(
                        text = uiState.currentMonth.format(DateTimeFormatter.ofPattern("yyyy.MM")),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "다음 달")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.resetAddState()
                showAddSheet = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "기록 추가")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.records.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("이번 달 운동 기록이 없습니다.", color = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.height(8.dp))
                        Text("+ 버튼을 눌러 기록을 추가하세요.",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.records) { record ->
                            RecordCard(
                                record = record,
                                onDelete = { viewModel.deleteRecord(record.id) },
                                onEdit = {
                                    viewModel.startEdit(record)
                                    showAddSheet = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        AddRecordBottomSheet(
            viewModel = viewModel,
            onDismiss = { showAddSheet = false; viewModel.resetAddState() }
        )
    }
}

@Composable
fun RecordCard(
    record: ClimbingRecordResponse,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(record.recordDate, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    record.gymName?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "수정", tint = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "삭제", tint = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            record.entries?.takeIf { it.isNotEmpty() }?.let { entries ->
                Spacer(Modifier.height(12.dp))
                entries.forEach { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val color = parseHexColor(entry.colorHex) ?: MaterialTheme.colorScheme.primary
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
                        Spacer(Modifier.width(8.dp))
                        Text(entry.colorName ?: "색깔",
                            style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))

                        // 진행 바
                        if (entry.plannedCount > 0) {
                            Box(
                                modifier = Modifier.width(80.dp).height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                val ratio = (entry.completedCount.toFloat() / entry.plannedCount).coerceIn(0f, 1f)
                                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(ratio).background(color))
                            }
                            Spacer(Modifier.width(6.dp))
                        }
                        Text("${entry.completedCount}/${entry.plannedCount}",
                            style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("기록 삭제") },
            text = { Text("이 운동 기록을 삭제할까요?") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("취소") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordBottomSheet(viewModel: RecordViewModel, onDismiss: () -> Unit) {
    val state by viewModel.addState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val isEditing = state.editingRecordId != null

    LaunchedEffect(state.isSuccess) { if (state.isSuccess) onDismiss() }

    // 날짜 피커
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = runCatching {
                LocalDate.parse(state.recordDate)
                    .toEpochDay() * 86400000L
            }.getOrDefault(System.currentTimeMillis())
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.of("UTC"))
                            .toLocalDate()
                        viewModel.setDate(date.toString())
                    }
                    showDatePicker = false
                }) { Text("확인") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("취소") } }
        ) { DatePicker(state = datePickerState) }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                if (isEditing) "운동 기록 수정" else "운동 기록 추가",
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))

            // 지점 선택 (새 기록일 때만)
            if (!isEditing && state.favoriteGyms.isNotEmpty()) {
                var gymExpanded by remember { mutableStateOf(false) }
                val selectedGymName = state.favoriteGyms.find { it.id == state.selectedGymId }?.name ?: "지점 선택"
                ExposedDropdownMenuBox(expanded = gymExpanded, onExpandedChange = { gymExpanded = it }) {
                    OutlinedTextField(
                        value = selectedGymName, onValueChange = {}, readOnly = true,
                        label = { Text("즐겨찾기 지점") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(gymExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = gymExpanded, onDismissRequest = { gymExpanded = false }) {
                        state.favoriteGyms.forEach { gym ->
                            DropdownMenuItem(
                                text = { Text(gym.name) },
                                onClick = { viewModel.selectGym(gym.id); gymExpanded = false }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            } else if (!isEditing && !state.isLoading) {
                Text("즐겨찾기한 지점이 없습니다.\n지점 탭에서 ♡를 눌러 추가하세요.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.height(12.dp))
            }

            // 날짜 선택 (새 기록일 때만)
            if (!isEditing) {
                OutlinedTextField(
                    value = state.recordDate,
                    onValueChange = {},
                    label = { Text("날짜") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "날짜 선택")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
            } else {
                // 수정 모드: 날짜/지점 표시만
                Text("${state.recordDate} 기록 수정",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.height(16.dp))
            }

            // 난이도별 기록 입력
            if (state.colors.isNotEmpty()) {
                Text("난이도별 기록", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                state.colors.forEach { color ->
                    val entry = state.entries[color.id] ?: Pair(0, 0)
                    val hexColor = parseHexColor(color.colorHex) ?: MaterialTheme.colorScheme.primary
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(hexColor))
                        Spacer(Modifier.width(8.dp))
                        Text(color.colorName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                        NumberInput(label = "목표", value = entry.first,
                            onChange = { viewModel.updateEntry(color.id, it, entry.second) })
                        Spacer(Modifier.width(8.dp))
                        NumberInput(label = "완료", value = entry.second,
                            onChange = { viewModel.updateEntry(color.id, entry.first, it) })
                    }
                }
            }

            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { viewModel.saveRecord() },
                enabled = !state.isLoading && (isEditing || state.selectedGymId != null),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text(if (isEditing) "수정 완료" else "저장", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun NumberInput(label: String, value: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.width(4.dp))
        IconButton(onClick = { if (value > 0) onChange(value - 1) }, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Remove, contentDescription = "감소", modifier = Modifier.size(16.dp))
        }
        Text(value.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold,
            modifier = Modifier.widthIn(min = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        IconButton(onClick = { onChange(value + 1) }, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Add, contentDescription = "증가", modifier = Modifier.size(16.dp))
        }
    }
}

private fun parseHexColor(raw: String?): Color? {
    if (raw.isNullOrBlank()) return null
    val normalized = if (raw.startsWith("#")) raw else "#$raw"
    return try { Color(android.graphics.Color.parseColor(normalized)) } catch (_: Exception) { null }
}
