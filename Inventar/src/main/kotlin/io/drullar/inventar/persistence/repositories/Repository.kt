package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.SortingOrder
import io.drullar.inventar.result
import io.drullar.inventar.shared.Page
import kotlin.Result
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 *  Repository interface for persisting single Model object
 *  [T] - [Table] that this repository serves
 *  [ID] - type of the ID for the given entity
 *  [C] - Creation dto model
 *  [R] - result model
 *  [S] - Allowed sort by
 */
interface Repository<T : Table, R, C, ID, S> {
    /**
     * Save a new entity based on the provided [dto]
     */
    fun save(dto: C): Result<R?>

    /**
     * Update an entity if it exists. If an entity with the provided [id] doesn't exist, no action is taken.
     * Return updated instance of the model.
     */
    fun update(id: ID, dto: C): Result<R>

    /**
     * Return an instance of [C] if the record with [id] exists, otherwise null is returned
     */
    fun getById(id: ID): Result<R>

    /**
     * Deletes a record with the provided [id], if such record exists, otherwise not action is performed
     */
    fun deleteById(id: ID): Result<Unit>

    /**
     * Delete all
     */
    fun deleteAll(): Result<Unit>

    /**
     * Returns all persisted elements
     */

    fun getAll(): Result<List<R>>

    /**
     * Returns the total amount of items
     */
    fun getCount(): Result<Long>

    /**
     * Returns paginated items
     */
    fun getPaged(page: Int, itemsPerPage: Int, sortBy: S, order: SortingOrder): Result<Page<R>>
}


abstract class AbstractRepository<T : Table, R, C, ID, S>(val table: T) :
    Repository<T, R, C, ID, S> {

    /**
     * Perform an [action] within a [transaction] block.
     */
    protected fun <P> withTransaction(action: (transaction: Transaction) -> P) =
        transaction { action(this) }

    override fun deleteAll(): Result<Unit> = result {
        withTransaction() {
            table.deleteAll()
        }
    }

    override fun getAll(): Result<List<R>> = result {
        withTransaction {
            table.selectAll().map { query -> transformResultRowToModel(query) }
        }
    }

    override fun getCount(): Result<Long> = result {
        withTransaction {
            table.selectAll().count()
        }
    }

    override fun getPaged(
        page: Int,
        itemsPerPage: Int,
        sortBy: S,
        order: SortingOrder
    ): Result<Page<R>> {
        val total = getCount().getOrThrow()
        val items = withTransaction {
            table.selectAll().limit(itemsPerPage, (((page - 1) * itemsPerPage)).toLong())
                .orderBy(buildOrderByExpression(sortBy) to if (order == SortingOrder.ASCENDING) SortOrder.ASC else SortOrder.DESC)
                .map { row -> transformResultRowToModel(row) }
        }

        return result {
            Page(
                items = items,
                totalItems = total,
                itemsPerPage = itemsPerPage,
                isLastPage = page * itemsPerPage >= total,
                pageNumber = page
            )
        }
    }

    protected abstract fun buildOrderByExpression(sortBy: S): Expression<*>
    protected abstract fun transformResultRowToModel(row: ResultRow): R
}