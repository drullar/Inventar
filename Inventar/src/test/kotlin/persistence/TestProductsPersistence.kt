package persistence

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import org.example.persistence.model.Product
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import persistence.utils.TestPersistenceConfiguration
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestProductsPersistence {

    @BeforeAll
    fun setup() {
        TestPersistenceConfiguration.initiateDatabase()
    }

    @Test
    fun create() {
        val id = transaction {
            Product.new {
                name = "NewProduct"
                sellingPrice = 10.0
                providerPrice = 8.0
                inStockQuantity = 20
            }.id.value
        }

        assertThat(id).isNotNull()
        transaction {
            with(Product.findById(id)) {
                assertThat(this).isNotNull()
                assertThat(this!!.name).isEqualTo("NewProduct")
                assertThat(this.sellingPrice).isEqualTo(10.0)
                assertThat(this.providerPrice).isEqualTo(8.0)
                assertThat(this.inStockQuantity).isEqualTo(20)
            }
        }
    }
}