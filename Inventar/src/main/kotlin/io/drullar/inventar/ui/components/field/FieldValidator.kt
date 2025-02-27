package io.drullar.inventar.ui.components.field

import javax.naming.directory.InvalidAttributesException

/**
 * Validator to be used in onValueChange
 */
interface FieldValidator<T> {
    fun validationErrorMessage(): String

    /**
     * Returns whether or not the passed value is valid
     */
    fun validate(value: T): Boolean

    companion object {
        /**
         * Returns a list of validators which have determined the value as invalid
         */
        fun <T> validate(
            value: T,
            validators: Iterable<FieldValidator<T>>
        ): List<FieldValidator<T>> = validators.filter { !it.validate(value) }
    }
}

class NotNegativeNumber<T : Number> : FieldValidator<T> {
    override fun validate(value: T): Boolean = when (value) {
        is Int -> value >= 0
        is Long -> value >= 0
        is Double -> value >= 0
        is Float -> value >= 0
        is Byte -> value >= 0
        is Short -> value >= 0
        else -> throw InvalidAttributesException("Unsupported type ${value::class.java}")
    }

    override fun validationErrorMessage(): String = "The provided value can not be negative."
}

class IsNotEmpty<T> : FieldValidator<T> {
    override fun validate(value: T): Boolean = value.toString().isNotBlank()
    override fun validationErrorMessage(): String = "The provided value should not be empty."
}
