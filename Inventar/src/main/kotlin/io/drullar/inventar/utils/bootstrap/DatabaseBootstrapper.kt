package io.drullar.inventar.utils.bootstrap

import io.drullar.inventar.persistence.repositories.impl.OrderRepository.withTransaction
import io.drullar.inventar.utils.TableScanner
import io.drullar.inventar.utils.file.DatabaseFile
import jdk.internal.org.jline.utils.ExecHelper.exec
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
        generateMissingTables()
        resolveSchemaChanges()
    }

    private fun generateMissingTables() {
        transaction {
            val allTables = databaseTables.toTypedArray()
            SchemaUtils.create(*allTables)
        }
    }

    private fun resolveSchemaChanges() {
        transaction {
            val tables = databaseTables.toTypedArray()
            val updateSchemaStatements = SchemaUtils.addMissingColumnsStatements(*tables)
            if (updateSchemaStatements.isNotEmpty()) execInBatch(updateSchemaStatements)

            val changes = SchemaUtils.checkMappingConsistence(*tables)
            if (changes.isNotEmpty()) execInBatch(changes)
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