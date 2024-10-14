package persistence

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import persistence.utils.TestPersistenceConfiguration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

abstract class AbstractPersistenceTest {
    @BeforeAll
    fun setup() {
        TestPersistenceConfiguration.initiateDatabase()
    }

    @AfterEach
    /*
    Override as needed with cleanup utils
     */
    open fun cleanUp(): Unit = Unit
}