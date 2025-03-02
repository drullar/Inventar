package io.drullar.inventar.unit.persistence.utils

import io.drullar.inventar.persistence.configuration.AbstractDatabaseBootstrapper
import io.drullar.inventar.persistence.configuration.DatabaseConfiguration

object TestDatabaseBootstrapper : AbstractDatabaseBootstrapper() {

    override fun getDatabaseConfiguration() = DatabaseConfiguration(
        DATABASE_URL
    )

    private const val DATABASE_URL =
        "jdbc:sqlite:build/temp.db?foreign_keys=on" //"jdbc:h2:mem:test"
}