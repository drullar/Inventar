package persistence.utils

import io.drullar.inventar.persistence.configuration.AbstractPersistenceConfiguration
import org.jetbrains.exposed.sql.Database

object TestPersistenceConfiguration : AbstractPersistenceConfiguration() {
    override fun setDatabaseConnection(): Database =
        Database.connect(
            url = DATABASE_URL,
            driver = DATABASE_DRIVER
        )

    private const val DATABASE_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
    private const val DATABASE_DRIVER = "org.h2.Driver"
}