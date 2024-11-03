package io.drullar.inventar

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.drullar.inventar.ui.App

fun main() {
//    Bootstrapper().bootstrapApplication()
    application {
        Window(::exitApplication, title = "Splash") {
            App()
        }
    }
}