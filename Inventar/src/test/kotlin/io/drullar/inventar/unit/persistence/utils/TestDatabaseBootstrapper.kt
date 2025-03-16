package io.drullar.inventar.unit.persistence.utils

import io.drullar.inventar.utils.bootstrap.AbstractDatabaseBootstrapper

object TestDatabaseBootstrapper : AbstractDatabaseBootstrapper() {

    private const val DATABASE_URL =
        "jdbc:sqlite:build/temp.db?foreign_keys=on" //"jdbc:h2:mem:test"

    override val databaseConfiguration: DatabaseConfiguration
        get() = DatabaseConfiguration(DATABASE_URL)
}