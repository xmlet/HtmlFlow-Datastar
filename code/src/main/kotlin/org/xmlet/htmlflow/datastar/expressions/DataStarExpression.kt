package org.xmlet.htmlflow.datastar.expressions

/**
 * Represents a DataStar expression containing JavaScript code, signals, and DataStar actions
 * to be evaluated within a data-* attribute.
 */
sealed class DataStarExpression(
    val syntax: String,
) {
    override fun toString(): String = syntax
}
