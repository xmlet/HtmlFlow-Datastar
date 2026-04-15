package org.xmlet.htmlflow.datastar.expressions

/**
 * A [DataStarExpression] is evaluated by a data attribute. It can contain JavaScript, signals and DataStar actions,
 * this means that changes to signals are propagated through other expressions.
 *
 * @property syntax the string expression to be passed to the data-x attribute
 */
open class DataStarExpression internal constructor(
    internal val syntax: String,
)
