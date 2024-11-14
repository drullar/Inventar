package io.drullar.inventar.ui

/**
 * Annotation used to give some sort of a identification to a UI element
 */
@Retention(AnnotationRetention.SOURCE)
@Target(allowedTargets = [AnnotationTarget.EXPRESSION])
annotation class Descriptor(val description: String)