package persistence

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.drullar.inventar.persistence.model.Products
import io.drullar.inventar.persistence.model.Products.inStockQuantity
import io.drullar.inventar.persistence.model.Products.name
import io.drullar.inventar.persistence.model.Products.providerPrice
import io.drullar.inventar.persistence.model.Products.sellingPrice
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import kotlin.test.Test

class TestProductsPersistence : AbstractPersistenceTest() {

    override fun cleanUp() {
        transaction {
            Products.deleteAll()
        }
    }

    @Test
    fun create() {
        val id = transaction {
            Products.insert {
                it[sellingPrice] = 10.0
                it[providerPrice] = 8.0
                it[inStockQuantity] = 20
                it[name] = "New Product"
            }.getOrNull(Column<Int>(Products, "id", IntegerColumnType()))
        }

        assertThat(id).isNotNull()
        transaction {
            val result = Products.selectAll().where { Products.id.eq(id!!) }
            assertThat(result.count()).isEqualTo(1)
            with(result.first()) {
                assertThat(this[name]).isEqualTo("New Product")
                assertThat(this[sellingPrice]).isEqualTo(10.0)
                assertThat(this[providerPrice]).isEqualTo(8.0)
                assertThat(this[inStockQuantity]).isEqualTo(20)
            }
        }
    }

    @Test
    fun autoIncrementedId() {
        val firstId = transaction {
            Products.insert {
                it[sellingPrice] = 10.0
                it[providerPrice] = 8.0
                it[inStockQuantity] = 20
                it[name] = "New Product"
            }.getOrNull(Column<Int>(Products, "id", IntegerColumnType()))
        }
        val secondId = transaction {
            Products.insert {
                it[sellingPrice] = 10.0
                it[providerPrice] = 8.0
                it[inStockQuantity] = 20
                it[name] = "New Product"
            }.getOrNull(Column<Int>(Products, "id", IntegerColumnType()))
        }

        assertThat(firstId).isNotNull()
        assertThat(secondId).isNotNull()
        assertThat(firstId).isEqualTo(1)
        assertThat(secondId).isEqualTo(2)
    }
}