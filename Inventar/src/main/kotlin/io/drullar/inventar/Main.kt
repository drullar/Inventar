package io.drullar.inventar.io.drullar.inventar.compose

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.drullar.inventar.App
import io.drullar.inventar.utils.Bootstrapper

fun main() {
    Bootstrapper().bootstrapApplication()
//    application {
//        Window(onCloseRequest = ::exitApplication) {
//            App()
//        }
//        Tray(icon = TrayIcon, menu = {
//            Item("SomeText", onClick = {
//                println("clicked")
//            })
//        })
//    }
}