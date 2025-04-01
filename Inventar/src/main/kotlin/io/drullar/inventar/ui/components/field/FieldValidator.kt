package io.drullar.inventar.ui.components.field

import io.drullar.inventar.ui.provider.getText
import java.math.BigDecimal
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

class IsNotEmpty<T> : FieldValidator<T> {
    override fun validate(value: T): Boolean = value.toString().isNotBlank()
    override fun validationErrorMessage(): String = getText("warning.validation.isEmpty")
}
