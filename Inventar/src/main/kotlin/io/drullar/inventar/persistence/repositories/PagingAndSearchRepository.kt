package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.shared.ISortBy
import io.drullar.inventar.shared.Page
import io.drullar.inventar.shared.PagedRequest

interface PagingAndSearchRepository<S : ISortBy, R> {
    /**
     * Returns paginated items
     */
    fun getPaged(pageRequest: PagedRequest<S>): Result<Page<R>>

    /**
     * Perform search based on the provided [searchQuery] and [pageRequest] and return a [Page] [Result]
     */
    fun search(searchQuery: String, pageRequest: PagedRequest<S>): Result<Page<R>>
}