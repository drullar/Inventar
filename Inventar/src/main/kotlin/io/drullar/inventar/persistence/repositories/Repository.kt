package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.shared.Page
import io.drullar.inventar.shared.RepositoryResponse
import io.drullar.inventar.shared.getOrThrow
import io.drullar.inventar.shared.response
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 *  Repository interface for persisting single Model object
 *  [T] - [Table] that this repository serves
 *  [ID] - type of the ID for the given entity
 *  [C] - Creation DTO
 *  [R] -
 */
interface Repository<T : Table, R, C, ID> {
    /**
     * Save a new entity based on the provided [dto]
     */
    fun save(dto: C): RepositoryResponse<R>

    /**
     * Update an entity if it exists. If an entity with the provided [id] doesn't exist, no action is taken.
     * Return updated instance of the model.
     */
    fun update(id: ID, dto: C): RepositoryResponse<R>

    /**
     * Return an instance of [C] if the record with [id] exists, otherwise null is returned
     */
    fun getById(id: ID): RepositoryResponse<R>

    /**
     * Deletes a record with the provided [id], if such record exists, otherwise not action is performed
     */
    fun deleteById(id: ID): RepositoryResponse<Unit>

    /**
     * Delete all
     */
    fun deleteAll(): RepositoryResponse<Unit>

    /**
     * Returns all persisted elements
     */

    fun getAll(): RepositoryResponse<List<R>>

    /**
     * Returns the total amount of items
     */
    fun getCount(): RepositoryResponse<Long>

    fun getAllPaged(page: Int, itemsPerPage: Int): RepositoryResponse<Page<R>>
}


abstract class AbstractRepository<T : Table, R, C, ID>(val table: T) : Repository<T, R, C, ID> {

    /**
     * Perform an [action] within a [transaction] block.
     */
    protected fun <P> withTransaction(
        action: (transaction: Transaction) -> P
    ) = transaction { action(this) }

    override fun deleteAll(): RepositoryResponse<Unit> = response {
        withTransaction() {
            table.deleteAll()
        }
    }

    override fun getAll(): RepositoryResponse<List<R>> = response {
        withTransaction {
            table.selectAll().map { query -> transformResultRowToModel(query) }
        }
    }

    override fun getCount(): RepositoryResponse<Long> = response {
        withTransaction {
            table.selectAll().count()
        }
    }

    override fun getAllPaged(page: Int, itemsPerPage: Int): RepositoryResponse<Page<R>> {
        val total = getCount().getOrThrow()
        val items = withTransaction {
            table.selectAll().limit(itemsPerPage, ((page - 1) * itemsPerPage).toLong())
                .map { row -> transformResultRowToModel(row) }
        }

        return response {
            Page(
                items = items,
                totalItems = total,
                itemsPerPage = itemsPerPage,
                isLastPage = page * itemsPerPage >= total,
                pageNumber = page
            )
        }
    }

    abstract fun transformResultRowToModel(row: ResultRow): R
}