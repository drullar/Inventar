package io.drullar.inventar.persistence

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import io.drullar.inventar.persistence.utils.TestPersistenceConfiguration
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

abstract class AbstractPersistenceTest {
    @BeforeAll
    fun setup() {
        initDatabaseFile()
        TestPersistenceConfiguration.initiateDatabase()
    }

    private fun initDatabaseFile() {
        val file = File("build/temp.db")
        if (file.exists()) file.delete()
        file.createNewFile()
    }

    /*
    Override as needed with cleanup utils
    */
    @AfterEach
    open fun cleanUp(): Unit = Unit
}