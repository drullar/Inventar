package io.drullar.inventar.persistence.model

import org.jetbrains.exposed.sql.Table


object Categories : Table() {
    val name = varchar("category_name", NAME_MAX_LENGTH)
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(name)
}
