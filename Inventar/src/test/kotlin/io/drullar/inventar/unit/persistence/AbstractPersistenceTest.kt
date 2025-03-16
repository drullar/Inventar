package io.drullar.inventar.unit.persistence

import io.drullar.inventar.unit.persistence.utils.TestDatabaseBootstrapper
import org.junit.Before
import java.io.File


abstract class AbstractPersistenceTest {

    @Before
    fun setup() {
        TestDatabaseBootstrapper(File("build/temp.db")).bootstrap()
    }
}