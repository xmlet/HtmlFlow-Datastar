package pt.isel.datastar.expressions

import pt.isel.datastar.Signal

/**
 * Equal to the JavaScript && operator, used to chain multiple expressions together.
 */
infix fun DataStarExpression.and(action: DataStarAction): DataStarExpression = DataStarExpression("$this && $action")

/**
 * Equal to the JavaScript || operator, used to provide an alternative expression if the previous one is not applicable.
 */
infix fun Signal<*>.or(action: DataStarAction): DataStarExpression = DataStarExpression("$this || $action")

/**
 * Equal to the JavaScript ! operator, used to negate an expression.
 */
infix fun DataStarExpression.or(action: DataStarAction): DataStarExpression = DataStarExpression("$this || $action")

/**
 * Equal to the JavaScript ! operator, used to negate a signal or expression.
 */
operator fun Signal<*>.not(): DataStarExpression = DataStarExpression("!$this")

/**
 * Equal to the JavaScript == operator, used to compare two expressions.
 */
infix fun DataStarExpression.equals(expression: DataStarExpression) = DataStarExpression("$this == $expression")

/**
 * Equal to the assignment operator in JavaScript, used to assign the signal value to the new value.
 */
infix fun <T> Signal<T>.setValue(value: T): DataStarExpression = DataStarExpression("$this = $value")

/**
 * Equal to the assignment operator in JavaScript, used to assign the passed expression to the precious.
 */
infix fun DataStarExpression.setValue(expression: DataStarExpression): DataStarExpression = DataStarExpression("$this = $expression")

/**
 * Used to add multiple statements in the same expression.
 */
infix fun DataStarExpression.semiColon(expression: DataStarExpression) = DataStarExpression("$this; $expression")

open class DataStarExpression internal constructor(
    val expression: String,
) {
    override fun toString(): String = expression
}
