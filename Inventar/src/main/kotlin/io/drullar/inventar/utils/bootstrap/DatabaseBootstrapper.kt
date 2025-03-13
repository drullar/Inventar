package io.drullar.inventar.utils.bootstrap

import io.drullar.inventar.utils.TableScanner
import io.drullar.inventar.utils.file.DatabaseFile
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface DatabaseBootstrapper : Bootstrapper

abstract class AbstractDatabaseBootstrapper : DatabaseBootstrapper {

    private val databaseTables by lazy {
        TableScanner.scanForTables()
    }

    override fun bootstrap() {
        initiateDatabase()
    }

    protected abstract val databaseConfiguration: DatabaseConfiguration

    private fun initiateDatabase() {
        Database.connect(url = databaseConfiguration.databaseUrl)
        createMissingTables()
        resolveSchemaChanges()
    }

    private fun createMissingTables() {
        transaction {
            SchemaUtils.create(*databaseTables.toTypedArray())
        }
    }

    private fun resolveSchemaChanges() {
        transaction {
            val changes = SchemaUtils.checkMappingConsistence(*databaseTables.toTypedArray())
            if (changes.isNotEmpty()) {
                execInBatch(changes)
                commit()
            }
        }
    }

    protected data class DatabaseConfiguration(
        val databaseUrl: String,
    )
}

class DatabaseBootstrapperImpl(private val databaseFile: DatabaseFile) :
    AbstractDatabaseBootstrapper() {

    override val databaseConfiguration: DatabaseConfiguration
        get() = DatabaseConfiguration(
            databaseUrl = "jdbc:sqlite:${databaseFile.getAbsolutePath()}?foreign_keys=on"
        )
}