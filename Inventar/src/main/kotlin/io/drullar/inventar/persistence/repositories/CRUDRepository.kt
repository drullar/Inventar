package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.shared.ISortBy
import kotlin.Result
import org.jetbrains.exposed.sql.*

/**
 *  Repository interface for persisting single Model object
 *  [T] - [Table] that this repository serves
 *
 *  [ID] - type of the ID for the given entity
 *
 *  [C] - Creation dto model
 *
 *  [R] - result model
 *
 *  [S] - Allowed sort by
 */
interface CRUDRepository<T : Table, R, C, ID, S : ISortBy> {
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
}


