package io.drullar.inventar.unit.persistence.utils

import io.drullar.inventar.utils.bootstrap.AbstractDatabaseBootstrapper
import java.io.File

class TestDatabaseBootstrapper(private val dbFile: File) : AbstractDatabaseBootstrapper() {

    private val databaseUrl =
        "jdbc:sqlite:${dbFile.absolutePath}?foreign_keys=on"

    override val databaseConfiguration: DatabaseConfiguration
        get() = DatabaseConfiguration(databaseUrl)

    override fun bootstrap() {
        if (dbFile.exists()) {
            dbFile.delete()
            dbFile.createNewFile()
        }
        super.bootstrap()
    }
}