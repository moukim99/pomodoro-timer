package com.pomodoro.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pomodoro.model.SessionType
import com.pomodoro.model.TimerSettings
import com.pomodoro.model.TimerStatus
import com.pomodoro.state.PomodoroTimerViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimerScreen(viewModel: PomodoroTimerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Timer, 1: History, 2: Settings

    Scaffold(
        topBar = {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("المؤقت ⏱️", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("السجل 📊", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("الإعدادات ⚙️", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> MainTimerContent(uiState, viewModel)
                1 -> SessionHistoryContent(uiState)
                2 -> SettingsContent(uiState.settings) { updated ->
                    viewModel.updateSettings(updated)
                }
            }
        }
    }
}

@Composable
fun MainTimerContent(
    uiState: com.pomodoro.state.TimerUiState,
    viewModel: PomodoroTimerViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Mode Switcher Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SessionType.entries.forEach { type ->
                val isSelected = uiState.currentType == type
                Button(
                    onClick = { viewModel.selectSessionType(type) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = null,
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                ) {
                    Text(type.title, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        // Circular Timer Display
        val progress = if (uiState.totalSeconds > 0) {
            uiState.remainingSeconds.toFloat() / uiState.totalSeconds.toFloat()
        } else 0f
        val animatedProgress by animateFloatAsState(targetValue = progress)

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(260.dp)
                .padding(16.dp)
        ) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 12.dp,
            )
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxSize(),
                color = if (uiState.currentType == SessionType.FOCUS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                strokeWidth = 12.dp,
            )

            val minutes = uiState.remainingSeconds / 60
            val seconds = uiState.remainingSeconds % 60
            val timeText = String.format("%02d:%02d", minutes, seconds)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeText,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.currentType.title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Timer Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (uiState.status) {
                TimerStatus.RUNNING -> {
                    Button(
                        onClick = { viewModel.pauseTimer() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(52.dp).width(130.dp),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Text("إيقاف مؤقت ⏸️", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
                else -> {
                    Button(
                        onClick = { viewModel.startTimer() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(52.dp).width(130.dp),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Text(if (uiState.status == TimerStatus.PAUSED) "استئناف ▶️" else "ابدأ ▶️", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier.height(52.dp).width(110.dp),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text("إعادة 🔄", fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun SessionHistoryContent(uiState: com.pomodoro.state.TimerUiState) {
    val totalFocusMinutes = uiState.sessionHistory
        .filter { it.sessionType == SessionType.FOCUS }
        .sumOf { it.durationMinutes }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("الجلسات المكتملة", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${uiState.sessionHistory.size}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("إجمالي التركيز", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$totalFocusMinutes دقيقة", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        if (uiState.sessionHistory.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد جلسات مسجلة بعد. ابدأ أول جلسة تركيز!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.sessionHistory) { session ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(session.sessionType.title, fontWeight = FontWeight.Bold)
                                Text(dateFormat.format(Date(session.completedAt)), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("${session.durationMinutes} دقيقة", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsContent(
    currentSettings: TimerSettings,
    onSave: (TimerSettings) -> Unit
) {
    var focusMins by remember { mutableStateOf(currentSettings.focusMinutes.toString()) }
    var shortMins by remember { mutableStateOf(currentSettings.shortBreakMinutes.toString()) }
    var longMins by remember { mutableStateOf(currentSettings.longBreakMinutes.toString()) }
    var soundAlerts by remember { mutableStateOf(currentSettings.soundAlertsEnabled) }
    var notifications by remember { mutableStateOf(currentSettings.notificationsEnabled) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("تخصيص أوقات المؤقت (بالدقائق):", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        OutlinedTextField(
            value = focusMins,
            onValueChange = { focusMins = it },
            label = { Text("مدة جلسة التركيز") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = shortMins,
            onValueChange = { shortMins = it },
            label = { Text("مدة الاستراحة القصيرة") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = longMins,
            onValueChange = { longMins = it },
            label = { Text("مدة الاستراحة الطويلة") },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("تشغيل التنبيه الصوتي (Ringtone / Beep)")
            Switch(checked = soundAlerts, onCheckedChange = { soundAlerts = it })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("إشعارات سطح المكتب (Windows Notifications)")
            Switch(checked = notifications, onCheckedChange = { notifications = it })
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val updated = currentSettings.copy(
                    focusMinutes = focusMins.toIntOrNull() ?: 25,
                    shortBreakMinutes = shortMins.toIntOrNull() ?: 5,
                    longBreakMinutes = longMins.toIntOrNull() ?: 15,
                    soundAlertsEnabled = soundAlerts,
                    notificationsEnabled = notifications
                )
                onSave(updated)
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("حفظ التغييرات 💾", fontWeight = FontWeight.Bold)
        }
    }
}
