package io.drullar.inventar.utils

import io.drullar.inventar.persistence.Relation
import io.github.classgraph.ClassGraph
import org.jetbrains.exposed.sql.Table

object TableScanner {
    /**
     * Returns a list of all classes annotated with the [scanAnnotation] and are instances of [Table]
     */
    fun scanForTables() =
        ClassGraph().enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages(PERSISTENCE_PKG_PATH)
            .scan()
            .getClassesWithAnnotation(scanAnnotation)
            .loadClasses()
            .filter { it.superclass == Table::class.java }
            .map { it.getField("INSTANCE").get(null) as Table }
            .toSet()

    private val scanAnnotation = Relation::class.java
    private const val PERSISTENCE_PKG_PATH = "io.drullar.inventar.persistence"
}