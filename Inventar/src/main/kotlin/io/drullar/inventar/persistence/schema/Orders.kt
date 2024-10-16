package io.drullar.inventar.persistence.schema

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

internal object Orders : Table(name = "orders") {
    val id = uuid(name = "id")
    val creationDate = date(name = "creation_date")
}