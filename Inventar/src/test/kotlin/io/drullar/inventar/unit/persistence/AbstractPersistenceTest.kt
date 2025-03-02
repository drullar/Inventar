package io.drullar.inventar.unit.persistence

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import io.drullar.inventar.unit.persistence.utils.TestDatabaseBootstrapper
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

abstract class AbstractPersistenceTest {
    @BeforeAll
    fun setup() {
        initDatabaseFile()
        TestDatabaseBootstrapper.initiateDatabase()
    }

    private fun initDatabaseFile() {
        val file = File("build/temp.db")
        if (file.exists()) file.delete()
        file.createNewFile()
    }
}