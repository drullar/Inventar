package io.drullar.inventar.persistence.repositories

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 *  Repository interface for persisting single Model object
 */
interface PersistenceRepository<T : Table, D, ID> {
    /**
     * Save a new entity based on the provided [payload]
     */
    fun save(payload: D): ID

    /**
     * Update an entity if it exists. If an entity with the provided [id] doesn't exist, no action is taken
     */
    fun update(id: ID, payload: D)

    /**
     * Return an instance of [D] if the record with [id] exists, otherwise null is returned
     */
    fun findById(id: ID): D?

    /**
     * Deletes a record with the provided [id], if such record exists, otherwise not action is performed
     */
    fun deleteById(id: ID)

    /**
     * Delete all
     */
    fun deleteAll()

    /**
     * Returns a list of all Ids
     */
    fun findALlIds(): List<ID>
}

/**
 * Data repository abstract class
 * [T] - Persistence Table static object
 * [D] - DAO persistence object type
 * [ID] - ID type
 */
abstract class AbstractPersistenceRepository<T : Table, D, ID>(val table: T) : PersistenceRepository<T, D, ID> {

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

    /**
     * Save and get Model object
     */
    fun saveAndRetrieveModel(payload: D): D = findById(save(payload))!!
}