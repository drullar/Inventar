package io.drullar.inventar

import androidx.compose.material.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

import io.drullar.inventar.utils.Bootstrapper

fun main() {
//    Bootstrapper().bootstrapApplication()
    application {
        Window(::exitApplication, title = "Splash") {
            Text("some text")
        }
    }
}