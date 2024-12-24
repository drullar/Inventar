package io.drullar.inventar

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.drullar.inventar.ui.App
import io.drullar.inventar.utils.Bootstrapper
import java.awt.Dimension

fun main() {
    Bootstrapper().bootstrapApplication()
    application {
        Window(
            ::exitApplication,
            title = "Inventar",
            state = rememberWindowState(
                placement = WindowPlacement.Maximized
            )
        ) {
            window.minimumSize = Dimension(800, 600)
            App()
        }
    }
}