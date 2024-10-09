package io.drullar.inventar.utils

import io.github.classgraph.ClassGraph

@Target(AnnotationTarget.CLASS)
annotation class Table

object AnnotationScanner {
    fun getAnnotatedClassesWith(annotationClass: Class<out Annotation>, targetPackage: String? = null): List<Class<*>> {
        val classGraph = ClassGraph().verbose().enableClassInfo().enableAnnotationInfo().apply {
            if (targetPackage != null) this.acceptPackages(targetPackage)
        }
        val scanResult = classGraph.scan()
        return scanResult.getClassesWithAnnotation(annotationClass).loadClasses()
    }
}