package com.pomodoro.storage

import com.pomodoro.model.PomodoroSession
import com.pomodoro.model.TimerSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object LocalStorage {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    private val dataDirectory: File by lazy {
        val appData = System.getenv("APPDATA") ?: System.getProperty("user.home")
        val dir = File(appData, "PomodoroTimer")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }

    private val settingsFile: File get() = File(dataDirectory, "settings.json")
    private val sessionsFile: File get() = File(dataDirectory, "sessions.json")

    suspend fun loadSettings(): TimerSettings = withContext(Dispatchers.IO) {
        try {
            if (settingsFile.exists()) {
                val content = settingsFile.readText()
                json.decodeFromString<TimerSettings>(content)
            } else {
                TimerSettings().also { saveSettings(it) }
            }
        } catch (e: Exception) {
            TimerSettings()
        }
    }

    suspend fun saveSettings(settings: TimerSettings) = withContext(Dispatchers.IO) {
        try {
            settingsFile.writeText(json.encodeToString(settings))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loadSessions(): List<PomodoroSession> = withContext(Dispatchers.IO) {
        try {
            if (sessionsFile.exists()) {
                val content = sessionsFile.readText()
                json.decodeFromString<List<PomodoroSession>>(content)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveSession(session: PomodoroSession) = withContext(Dispatchers.IO) {
        try {
            val current = loadSessions().toMutableList()
            current.add(0, session)
            sessionsFile.writeText(json.encodeToString(current))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
