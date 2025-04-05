package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.SortingOrder
import io.drullar.inventar.result
import io.drullar.inventar.shared.ISortBy
import io.drullar.inventar.shared.Page
import io.drullar.inventar.shared.PagedRequest
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

abstract class AbstractRepository<T : Table, R, C, ID, S : ISortBy>(val table: T) :
    CRUDRepository<T, R, C, ID, S>, PagingAndSearchRepository<S, R> {

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
        pagedRequest: PagedRequest<S>
    ): Result<Page<R>> {
        val total = getCount().getOrThrow()
        val items = withTransaction {
            table.selectAll().limit(
                pagedRequest.pageSize,
                ((pagedRequest.page - 1) * pagedRequest.pageSize).toLong()
            )
                .orderBy(buildOrderByExpression(pagedRequest.sortBy) to if (pagedRequest.order == SortingOrder.ASCENDING) SortOrder.ASC else SortOrder.DESC)
                .map { row -> transformResultRowToModel(row) }
        }

        return result {
            Page(
                items = items,
                totalItems = total,
                itemsPerPage = pagedRequest.page,
                isLastPage = pagedRequest.page * pagedRequest.pageSize >= total,
                pageNumber = pagedRequest.page
            )
        }
    }

    override fun search(searchQuery: String, pageRequest: PagedRequest<S>): Result<Page<R>> {
        throw NotImplementedError()
    }

    protected abstract fun buildOrderByExpression(sortBy: S): Expression<*>
    protected abstract fun transformResultRowToModel(row: ResultRow): R
}