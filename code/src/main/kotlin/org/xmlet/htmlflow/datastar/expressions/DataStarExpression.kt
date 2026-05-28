package org.xmlet.htmlflow.datastar.expressions

/**
 * Represents a DataStar expression containing JavaScript code, signals, and DataStar actions
 * to be evaluated within data-* attribute.
 *
 * @property syntax The raw string representation of the expression to be evaluated
 *                  and processed by the DataStar framework.
 */
sealed class DataStarExpression(
    val syntax: String,
)
