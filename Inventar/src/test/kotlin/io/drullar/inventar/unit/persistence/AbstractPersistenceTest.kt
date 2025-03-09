package io.drullar.inventar.unit.persistence

import io.drullar.inventar.unit.persistence.utils.TestDatabaseBootstrapper
import org.junit.Before
import java.io.File


abstract class AbstractPersistenceTest {

    @Before
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