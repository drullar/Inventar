package io.drullar.inventar.persistence.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 *  Repository interface for persisting single Model object
 */
interface PersistenceRepository<T : Table, D, ID> {
    /**
     * Save a new entity based on the provided [model]
     */
    fun save(model: D): D

    /**
     * Update an entity if it exists. If an entity with the provided [id] doesn't exist, no action is taken
     */
    fun update(id: ID, model: D)

    /**
     * Return an instance of [D] if the record with [id] exists, otherwise null is returned
     */
    fun getById(id: ID): D?

    /**
     * Deletes a record with the provided [id], if such record exists, otherwise not action is performed
     */
    fun deleteById(id: ID)

    /**
     * Delete all
     */
    fun deleteAll()

    /**
     * Returns all persisted elements
     */

    fun getAll(): List<D>
}

/**
 * Data repository abstract class
 * [T] - Persistence Table static object
 * [D] - DTO persistence object type
 * [ID] - ID type
 */
abstract class AbstractPersistenceRepository<T : Table, D, ID>(val table: T) :
    PersistenceRepository<T, D, ID> {

    /**
     * Perform an [action] within a [transaction] block
     */
    protected fun <R> withTransaction(action: (transaction: Transaction) -> R) = transaction {
        action.invoke(this)
    }

    override fun deleteAll() {
        withTransaction {
            table.deleteAll()
        }
    }

    override fun getAll(): List<D> = withTransaction {
        table.selectAll().map { transformResultRowToModel(it) }
    }

    protected abstract fun transformResultRowToModel(row: ResultRow): D
}