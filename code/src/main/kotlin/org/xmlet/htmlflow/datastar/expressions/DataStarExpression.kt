package org.xmlet.htmlflow.datastar.expressions

/**
 * Represents a DataStar expression containing JavaScript code, signals, and DataStar actions
 * to be evaluated within data-* attributes.
 *
 * @property syntax The raw string representation of the expression to be evaluated
 *                  and processed by the DataStar framework.
 */
open class DataStarExpression internal constructor(
    val syntax: String,
)
