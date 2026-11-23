package org.xmlet.htmlflow.datastar.expressions

/**
 * Represents a DataStar expression containing JavaScript code, signals, and DataStar actions
 * to be evaluated within a data-* attribute.
 *
 * @property syntax The JavaScript or DataStar source of this expression.
 */
sealed class DataStarExpression {
    abstract val syntax: String

    override fun toString(): String = syntax
}
