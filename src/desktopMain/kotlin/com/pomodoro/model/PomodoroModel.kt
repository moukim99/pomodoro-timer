package com.pomodoro.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
enum class SessionType(val defaultMinutes: Int, val title: String) {
    FOCUS(25, "جلسة تركيز"),
    SHORT_BREAK(5, "استراحة قصيرة"),
    LONG_BREAK(15, "استراحة طويلة")
}

@Serializable
enum class TimerStatus {
    IDLE,
    RUNNING,
    PAUSED
}

@Serializable
data class PomodoroSession(
    val id: String = UUID.randomUUID().toString(),
    val sessionType: SessionType,
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val taskLabel: String? = null
)

@Serializable
data class TimerSettings(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val cyclesBeforeLongBreak: Int = 4,
    val soundAlertsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true
)
