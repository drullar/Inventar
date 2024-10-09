package io.drullar.inventar.utils

import io.drullar.inventar.persistence.configuration.IPersistenceConfiguration
import io.drullar.inventar.persistence.configuration.PersistenceConfigurationImpl

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