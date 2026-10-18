package com.pomodoro

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.pomodoro.state.PomodoroTimerViewModel
import com.pomodoro.ui.PomodoroTheme
import com.pomodoro.ui.TimerScreen
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

fun createTomatoIcon(): BufferedImage {
    val img = BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
    val g: Graphics2D = img.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    
    // Tomato body (Red)
    g.color = Color(229, 57, 53)
    g.fillOval(3, 6, 26, 24)

    // Leaf/Stem (Green)
    g.color = Color(67, 160, 71)
    g.fillPolygon(
        intArrayOf(16, 12, 16, 20),
        intArrayOf(1, 6, 8, 6),
        4
    )
    g.dispose()
    return img
}

fun main() = application {
    val coroutineScope = rememberCoroutineScope()
    var isWindowVisible by remember { mutableStateOf(true) }
    val trayState = rememberTrayState()
    val tomatoIcon = remember { createTomatoIcon() }
    val iconPainter = remember { BitmapPainter(tomatoIcon.toComposeImageBitmap()) }

    val viewModel = remember {
        PomodoroTimerViewModel(
            scope = coroutineScope,
            onSendNotification = { title, message ->
                trayState.sendNotification(
                    Notification(
                        title = title,
                        message = message,
                        type = Notification.Type.Info
                    )
                )
            }
        )
    }

    val windowState = rememberWindowState(
        size = DpSize(440.dp, 620.dp),
        position = WindowPosition(Alignment.Center)
    )

    // System Tray integration
    Tray(
        icon = iconPainter,
        state = trayState,
        tooltip = "مؤقت بومودورو المكتبي",
        onAction = { isWindowVisible = true },
        menu = {
            Item("فتح المؤقت", onClick = { isWindowVisible = true })
            Item("ابدأ / استئناف", onClick = { viewModel.startTimer() })
            Item("إيقاف مؤقت", onClick = { viewModel.pauseTimer() })
            Item("إعادة ضبط", onClick = { viewModel.resetTimer() })
            Separator()
            Item("خروج", onClick = ::exitApplication)
        }
    )

    if (isWindowVisible) {
        Window(
            onCloseRequest = {
                // Minimize to tray instead of hard exit if desired
                isWindowVisible = false
            },
            title = "مؤقت بومودورو - Pomodoro Timer",
            state = windowState,
            icon = iconPainter,
            resizable = false
        ) {
            PomodoroTheme {
                TimerScreen(viewModel = viewModel)
            }
        }
    }
}
