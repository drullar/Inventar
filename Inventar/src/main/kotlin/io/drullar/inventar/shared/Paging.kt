package io.drullar.inventar.shared


data class Page<T>(
    val pageNumber: Int,
    val itemsPerPage: Int,
    val items: List<T>,
    val totalItems: Long,
    val isLastPage: Boolean
)

data class PagedRequest<S : ISortBy>(
    val page: Int,
    val pageSize: Int,
    val order: SortingOrder,
    val sortBy: S
)

interface ISortBy