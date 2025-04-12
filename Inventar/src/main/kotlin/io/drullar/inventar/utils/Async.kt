package io.drullar.inventar.utils

import kotlin.concurrent.thread

fun runAsync(block: () -> Unit) {
    thread {
        block()
    }
}