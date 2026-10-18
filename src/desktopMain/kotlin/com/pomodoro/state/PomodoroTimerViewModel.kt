package com.pomodoro.state

import com.pomodoro.model.PomodoroSession
import com.pomodoro.model.SessionType
import com.pomodoro.model.TimerSettings
import com.pomodoro.model.TimerStatus
import com.pomodoro.storage.LocalStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.awt.Toolkit
import java.awt.TrayIcon

data class TimerUiState(
    val currentType: SessionType = SessionType.FOCUS,
    val status: TimerStatus = TimerStatus.IDLE,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val completedFocusCycles: Int = 0,
    val settings: TimerSettings = TimerSettings(),
    val sessionHistory: List<PomodoroSession> = emptyList()
)

class PomodoroTimerViewModel(
    private val scope: CoroutineScope,
    private val onSendNotification: (title: String, message: String) -> Unit = { _, _ -> }
) {
    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        scope.launch {
            val settings = LocalStorage.loadSettings()
            val sessions = LocalStorage.loadSessions()
            val initialSeconds = settings.focusMinutes * 60
            _uiState.value = _uiState.value.copy(
                settings = settings,
                sessionHistory = sessions,
                remainingSeconds = initialSeconds,
                totalSeconds = initialSeconds
            )
        }
    }

    fun startTimer() {
        if (_uiState.value.status == TimerStatus.RUNNING) return

        _uiState.value = _uiState.value.copy(status = TimerStatus.RUNNING)
        timerJob?.cancel()
        timerJob = scope.launch(Dispatchers.Default) {
            while (isActive && _uiState.value.remainingSeconds > 0) {
                delay(1000L)
                val newSeconds = _uiState.value.remainingSeconds - 1
                _uiState.value = _uiState.value.copy(remainingSeconds = newSeconds)
            }
            if (_uiState.value.remainingSeconds <= 0) {
                onTimerFinished()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(status = TimerStatus.PAUSED)
    }

    fun resetTimer() {
        timerJob?.cancel()
        val currentDuration = when (_uiState.value.currentType) {
            SessionType.FOCUS -> _uiState.value.settings.focusMinutes
            SessionType.SHORT_BREAK -> _uiState.value.settings.shortBreakMinutes
            SessionType.LONG_BREAK -> _uiState.value.settings.longBreakMinutes
        } * 60

        _uiState.value = _uiState.value.copy(
            status = TimerStatus.IDLE,
            remainingSeconds = currentDuration,
            totalSeconds = currentDuration
        )
    }

    fun selectSessionType(type: SessionType) {
        timerJob?.cancel()
        val durationMinutes = when (type) {
            SessionType.FOCUS -> _uiState.value.settings.focusMinutes
            SessionType.SHORT_BREAK -> _uiState.value.settings.shortBreakMinutes
            SessionType.LONG_BREAK -> _uiState.value.settings.longBreakMinutes
        }
        val seconds = durationMinutes * 60
        _uiState.value = _uiState.value.copy(
            currentType = type,
            status = TimerStatus.IDLE,
            remainingSeconds = seconds,
            totalSeconds = seconds
        )
    }

    private fun onTimerFinished() {
        val currentState = _uiState.value
        if (currentState.settings.soundAlertsEnabled) {
            try {
                Toolkit.getDefaultToolkit().beep()
            } catch (_: Exception) {}
        }

        val completedType = currentState.currentType
        var nextCycles = currentState.completedFocusCycles

        if (completedType == SessionType.FOCUS) {
            nextCycles += 1
            val session = PomodoroSession(
                sessionType = SessionType.FOCUS,
                durationMinutes = currentState.settings.focusMinutes
            )
            scope.launch {
                LocalStorage.saveSession(session)
                _uiState.value = _uiState.value.copy(
                    sessionHistory = LocalStorage.loadSessions()
                )
            }

            val isLongBreak = nextCycles % currentState.settings.cyclesBeforeLongBreak == 0
            val nextType = if (isLongBreak) SessionType.LONG_BREAK else SessionType.SHORT_BREAK

            if (currentState.settings.notificationsEnabled) {
                onSendNotification(
                    "انتهت جلسة التركيز! 🍅",
                    if (isLongBreak) "حان وقت استراحة طويلة (${currentState.settings.longBreakMinutes} دقيقة)!"
                    else "حان وقت استراحة قصيرة (${currentState.settings.shortBreakMinutes} دقائق)!"
                )
            }
            selectSessionType(nextType)
        } else {
            if (currentState.settings.notificationsEnabled) {
                onSendNotification(
                    "انتهت الاستراحة! ⚡",
                    "حان وقت العودة للتركيز والإنجاز!"
                )
            }
            selectSessionType(SessionType.FOCUS)
        }
    }

    fun updateSettings(newSettings: TimerSettings) {
        scope.launch {
            LocalStorage.saveSettings(newSettings)
            _uiState.value = _uiState.value.copy(settings = newSettings)
            resetTimer()
        }
    }
}
