package io.drullar.inventar.persistence

import io.drullar.inventar.persistence.repositories.ProductPersistenceRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import io.drullar.inventar.persistence.utils.TestPersistenceConfiguration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

abstract class AbstractPersistenceTest {
    @BeforeAll
    fun setup() {
        TestPersistenceConfiguration.initiateDatabase()
    }

    internal val productsRepository = ProductPersistenceRepository

    @AfterEach
    /*
    Override as needed with cleanup utils
     */
    open fun cleanUp(): Unit = Unit
}