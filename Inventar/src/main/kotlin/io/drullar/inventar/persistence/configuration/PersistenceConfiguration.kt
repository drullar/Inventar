package io.drullar.inventar.persistence.configuration

import io.drullar.inventar.utils.TableScanner
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

/**
 * Interface for a singleton PersistenceConfiguration.
 * Used to initiate the database connection and the tables in it
 */
interface DatabaseBootstrapper {

    /**
     * Builds the database tables and any other database required dependencies
     */
    fun initiateDatabase()
}

abstract class AbstractDatabaseBootstrapper : DatabaseBootstrapper {

    override fun initiateDatabase() {
        Database.connect(
            url = getDatabaseConfiguration().databaseUrl
        )
        createTables()
    }

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

object DatabaseBootstrapperImpl : AbstractDatabaseBootstrapper() {

    override fun getDatabaseConfiguration(): DatabaseConfiguration {
        val tmpDir = File(System.getProperty("java.io.tmpdir"))
        val dbFile = File(tmpDir, "temp.db").apply {
            this.createNewFile()
        } // TODO change directory with application files directory
        return DatabaseConfiguration(
            databaseUrl = "jdbc:sqlite:${dbFile.absolutePath}?foreign_keys=on"
        )
    }
}

data class DatabaseConfiguration(
    val databaseUrl: String,
)