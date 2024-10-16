package io.drullar.inventar.persistence.configuration

import io.drullar.inventar.utils.TableScanner
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Interface for a singleton PersistenceConfiguration.
 * Used to initiate the database connection and the tables in it
 */
interface IPersistenceConfiguration {
    /**
     * Each implementation should run [Database.connect] in order to set up the database connection
     */
    fun setDatabaseConnection(): Database

    /**
     * Builds the database tables and any other database required dependencies
     */
    fun initiateDatabase()
}

abstract class AbstractPersistenceConfiguration : IPersistenceConfiguration {
    override fun initiateDatabase() {
        setDatabaseConnection()
        createTables()
    }

    private fun createTables() {
        transaction {
            addLogger(StdOutSqlLogger)
            databaseTables.forEach { table ->
                SchemaUtils.create(table)
            }
        }
    }

    companion object {
        private val databaseTables by lazy {
            TableScanner.scanForTables()
        }
    }
}

object PersistenceConfigurationImpl : AbstractPersistenceConfiguration() {
    override fun setDatabaseConnection(): Database =
        Database.connect(
            url = DATABASE_URL,
            driver = DATABASE_DRIVER
        )

    private const val DATABASE_URL = "jdbc:h2:mem:test"
    private const val DATABASE_DRIVER = "org.h2.Driver"
}