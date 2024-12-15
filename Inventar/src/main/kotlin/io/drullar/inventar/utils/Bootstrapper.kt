package io.drullar.inventar.utils

import io.drullar.inventar.persistence.configuration.DatabaseBootstrapper
import io.drullar.inventar.persistence.configuration.DatabaseBootstrapperImpl
import kotlinx.coroutines.runBlocking

class Bootstrapper(
    private val persistenceConfiguration: DatabaseBootstrapper = DatabaseBootstrapperImpl
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