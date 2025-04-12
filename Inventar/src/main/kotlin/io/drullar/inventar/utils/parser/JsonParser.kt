package io.drullar.inventar.utils.parser

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

object JsonParser {
    private val objectMapper = ObjectMapper()
    fun <T> read(file: File, type: Class<T>): T = objectMapper.readValue(file, type)
    fun <T> write(file: File, obj: T): Unit = objectMapper.writeValue(file, obj)
}