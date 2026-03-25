package pt.isel.datastar.expressions

/**
 * Equal to the JavaScript ! operator, used to negate an expression.
 */
operator fun DataStarExpression.not(): DataStarExpression = DataStarExpression("!$this")

/**
 * A [DataStarExpression] is evaluated by a data attribute. It can contain JavaScript, signals and DataStar actions,
 * this means that changes to signals are propagated through other expressions.
 *
 * @property expression the string expression to be passed to the data-x attribute
 */
open class DataStarExpression internal constructor(
    val expression: String,
) {
    override fun toString(): String = expression
}
