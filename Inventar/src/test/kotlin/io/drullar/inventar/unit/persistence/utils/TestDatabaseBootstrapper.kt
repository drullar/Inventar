package io.drullar.inventar.unit.persistence.utils

import io.drullar.inventar.utils.bootstrap.AbstractDatabaseBootstrapper
import io.drullar.inventar.utils.bootstrap.DatabaseConfiguration

object TestDatabaseBootstrapper : AbstractDatabaseBootstrapper() {

    override fun getDatabaseConfiguration() = DatabaseConfiguration(
        DATABASE_URL
    )

    private const val DATABASE_URL =
        "jdbc:sqlite:build/temp.db?foreign_keys=on" //"jdbc:h2:mem:test"
}