package io.drullar.inventar.logging

import java.time.LocalDateTime
import kotlin.reflect.KClass

interface Logger {
    fun info(message: String)
    fun error(message: String)

}

class LoggerImpl(private val source: KClass<*>) : Logger {
    override fun info(message: String) {
        println("[${LogLevel.INFO}] ${source.qualifiedName} - Timestamp: ${LocalDateTime.now()} - $message")
    }

    override fun error(message: String) {
        println("[${LogLevel.ERROR}] ${source.qualifiedName} - Timestamp: ${LocalDateTime.now()} - $message")
    }
}

private enum class LogLevel {
    INFO,
    ERROR
}