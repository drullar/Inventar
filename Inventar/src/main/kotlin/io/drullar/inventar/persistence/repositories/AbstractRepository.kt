package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.result
import io.drullar.inventar.shared.ISortBy
import io.drullar.inventar.shared.Page
import io.drullar.inventar.shared.PagedRequest
import io.drullar.inventar.shared.SortingOrder
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

abstract class AbstractRepository<T : Table, R, C, ID, S : ISortBy>(val table: T) :
    CRUDRepository<T, R, C, ID, S>, PagingAndSearchRepository<S, R> {

    /**
     * Perform an [action] within a [transaction] block.
     */
    protected fun <P> withTransaction(action: (transaction: Transaction) -> P) =
        transaction {
//  enable logging for debugging only
//            addLogger(StdOutSqlLogger)
            action(this)
        }

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
        pageRequest: PagedRequest<S>
    ): Result<Page<R>> {
        val total = getCount().getOrThrow()
        val items = withTransaction {
            table.selectAll().limit(
                pageRequest.pageSize,
                ((pageRequest.page - 1) * pageRequest.pageSize).toLong()
            )
                .orderBy(buildOrderByExpression(pageRequest.sortBy) to if (pageRequest.order == SortingOrder.ASCENDING) SortOrder.ASC else SortOrder.DESC)
                .map { row -> transformResultRowToModel(row) }
        }

        return result {
            Page(
                items = items,
                totalItems = total,
                itemsPerPage = pageRequest.pageSize,
                isLastPage = pageRequest.page * pageRequest.pageSize >= total,
                pageNumber = pageRequest.page
            )
        }
    }

    override fun search(searchQuery: String, pageRequest: PagedRequest<S>): Result<Page<R>> {
        throw NotImplementedError()
    }

    protected abstract fun buildOrderByExpression(sortBy: S): Expression<*>
    protected abstract fun transformResultRowToModel(row: ResultRow): R
}