package io.drullar.inventar.persistence

sealed class DatabaseException(message: String) : Exception(message) {
    class NoSuchElementFoundException(message: String) :
        DatabaseException(message)

    class PersistenceException(message: String) :
        DatabaseException(message)

    class InvalidOperationException(message: String) :
        DatabaseException(message)
}