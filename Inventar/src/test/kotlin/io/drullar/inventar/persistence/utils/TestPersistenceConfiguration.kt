package io.drullar.inventar.persistence.utils

import io.drullar.inventar.persistence.configuration.AbstractPersistenceConfiguration
import io.drullar.inventar.persistence.configuration.DatabaseConfiguration

object TestPersistenceConfiguration : AbstractPersistenceConfiguration() {

    override fun getDatabaseConfiguration() = DatabaseConfiguration(
        DATABASE_URL,
        DATABASE_DRIVER
    )

    private const val DATABASE_URL = "jdbc:sqlite:build/temp.db?foreign_keys=on" //"jdbc:h2:mem:test"
    private const val DATABASE_DRIVER = "org.sqlite.JDBC"//"org.h2.Driver"
}