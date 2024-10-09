package io.drullar.inventar.persistence.configuration

import io.drullar.inventar.persistence.model.Products
import io.drullar.inventar.utils.AnnotationScanner
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.reflect.full.isSuperclassOf

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
        transaction {
            addLogger(StdOutSqlLogger) // Remove logger if not required
            val databaseTables =
                AnnotationScanner.getAnnotatedClassesWith(Target::class.java, PERSISTENCE_CODE_PKG)
                    .filter { it.isAssignableFrom(Table::class.java) }.map { it as Table }
            databaseTables.forEach { table ->
                SchemaUtils.create(table)
            }
        }
    }

    companion object {
        protected const val PERSISTENCE_CODE_PKG = "io.drullar.inventar.persistence"
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