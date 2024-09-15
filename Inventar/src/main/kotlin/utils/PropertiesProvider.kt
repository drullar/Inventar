package org.example.utils

import java.util.Properties

object PropertiesProvider {
    private const val PROPERTIES_FILE_NAME = "application.properties"
    private val propertiesFile = this::class.java.classLoader.getResourceAsStream(PROPERTIES_FILE_NAME)
    private val properties = Properties().apply { load(propertiesFile) }

    fun getProperty(key: String) = properties.getProperty(key)
    fun setProperty(key: String, value: String) = properties.setProperty(key, value)
}