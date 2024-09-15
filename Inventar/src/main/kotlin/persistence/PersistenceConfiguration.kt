package org.example.persistence

import org.example.persistence.model.Product
import org.example.utils.PropertiesProvider
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object PersistenceConfiguration {
    val dbConnectionConfig =
        Database.connect(
            url = PropertiesProvider.getProperty("database.connection.url"),
            driver = PropertiesProvider.getProperty("database.connection.driver")
        )
    
    fun initiateDatabase() {

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Product)
        }
    }
}