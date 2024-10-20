package io.drullar.inventar.persistence

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.drullar.inventar.persistence.schema.Categories
import io.drullar.inventar.persistence.schema.Products
import io.drullar.inventar.persistence.schema.associative.ProductCategoriesAssociation
import io.drullar.inventar.persistence.schema.associative.ProductCategoriesAssociation.categoryName
import io.drullar.inventar.persistence.schema.associative.ProductCategoriesAssociation.productId
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test

class TestProductCategoriesAssociation : AbstractPersistenceTest() {

    private val mapping = ProductCategoriesAssociation
    private val categories = Categories
    private val products = Products

    @Test
    fun create() {
        transaction {
            categories.insert {
                it[name] = "cat"
            }

            products.insert {
                it[name] = "something"
            }

            mapping.insert {
                it[categoryName] = "cat"
                it[productId] = 1
            }

            val allMappings = mapping.selectAll()
            assertThat(allMappings.count()).isEqualTo(1)
            val row = allMappings.first()
            assertThat(row[categoryName]).isEqualTo("cat")
            assertThat(row[productId]).isEqualTo(1)
        }
    }
}