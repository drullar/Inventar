package io.drullar.inventar.shared

sealed interface RepositoryResponse<out T> {
    data class Success<T>(val data: T) : RepositoryResponse<T>
    data class Failure(val exception: Throwable) : RepositoryResponse<Nothing>
}

fun <T> response(action: () -> T): RepositoryResponse<T> =
    try {
        RepositoryResponse.Success(action())
    } catch (e: Throwable) {
        RepositoryResponse.Failure(e)
    }

/**
 * Returns the result data if the result is [RepositoryResponse.Success] otherwise returns null
 */
fun <T> RepositoryResponse<T>.getDataOnSuccessOrNull(): T? =
    if (this is RepositoryResponse.Success) data
    else null