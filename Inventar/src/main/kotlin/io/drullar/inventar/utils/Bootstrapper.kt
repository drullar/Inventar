package io.drullar.inventar.utils

import io.drullar.inventar.persistence.configuration.IPersistenceConfiguration
import io.drullar.inventar.persistence.configuration.PersistenceConfigurationImpl
import kotlinx.coroutines.runBlocking

class Bootstrapper(
    private val persistenceConfiguration: IPersistenceConfiguration = PersistenceConfigurationImpl
) {
    fun bootstrapApplication() {
        bootstrapDatabase()
    }

    private fun bootstrapDatabase() {
        runBlocking {
            persistenceConfiguration.initiateDatabase()
        }
    }
}