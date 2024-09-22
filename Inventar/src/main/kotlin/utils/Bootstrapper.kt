package org.example.utils

import org.example.persistence.configuration.IPersistenceConfiguration
import org.example.persistence.configuration.PersistenceConfigurationImpl

class Bootstrapper(
    private val persistenceConfiguration: IPersistenceConfiguration = PersistenceConfigurationImpl
) {
    fun bootstrapApplication() {
        bootstrapDatabase()
    }

    private fun bootstrapDatabase() {
        persistenceConfiguration.initiateDatabase()
    }
}