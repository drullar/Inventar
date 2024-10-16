package io.drullar.inventar.persistence.schema

import io.drullar.inventar.persistence.Relation
import org.jetbrains.exposed.sql.Table

@Relation
object Categories : Table() {
    val name = varchar("category_name", NAME_MAX_LENGTH)
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(name)
}
