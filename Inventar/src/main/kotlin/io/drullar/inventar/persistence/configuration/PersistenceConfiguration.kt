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
    fun setDatabaseConnection(databaseConfiguration: DatabaseConfiguration): Database

    /**
     * Builds the database tables and any other database required dependencies
     */
    fun initiateDatabase()
}

abstract class AbstractPersistenceConfiguration : IPersistenceConfiguration {
    override fun initiateDatabase() {
        setDatabaseConnection(getDatabaseConfiguration())
        createTables()
    }

    override fun setDatabaseConnection(databaseConfiguration: DatabaseConfiguration): Database =
        Database.connect(
            url = databaseConfiguration.databaseUrl,
            driver = databaseConfiguration.databaseDriver
        )

    abstract fun getDatabaseConfiguration(): DatabaseConfiguration

    private fun createTables() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(*databaseTables.toTypedArray())
        }
    }

    companion object {
        private val databaseTables by lazy {
            TableScanner.scanForTables()
        }
    }
}

object PersistenceConfigurationImpl : AbstractPersistenceConfiguration() {

    override fun getDatabaseConfiguration() = DatabaseConfiguration(
        DATABASE_URL,
        DATABASE_DRIVER
    )

    private const val DATABASE_URL = "jdbc:sqlite:file::memory:" //"jdbc:h2:mem:test"
    private const val DATABASE_DRIVER = "java.sql.Drive"//"org.h2.Driver"
}

data class DatabaseConfiguration(
    val databaseUrl: String,
    val databaseDriver: String
)