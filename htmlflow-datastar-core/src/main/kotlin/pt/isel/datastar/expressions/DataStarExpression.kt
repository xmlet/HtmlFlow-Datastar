package pt.isel.datastar.expressions

import pt.isel.datastar.Signal

class DataStarExpression private constructor(
    val expression: String,
) {
    /**
     * Equal to the JavaScript && operator, used to chain multiple actions together.
     */
    infix fun Signal<*>.and(action: DataStarAction): DataStarExpression = DataStarExpression("$this && $action")

    infix fun DataStarExpression.and(action: DataStarAction): DataStarExpression = DataStarExpression("$this && $action")

    /**
     * Equal to the JavaScript || operator, used to provide an alternative action if the previous one is not applicable.
     */
    infix fun Signal<*>.or(action: DataStarAction): DataStarExpression = DataStarExpression("$this || $action")

    infix fun DataStarExpression.or(action: DataStarAction): DataStarExpression = DataStarExpression("$this || $action")

    /**
     * Equal to the JavaScript ! operator, used to negate a signal or expression.
     */
    operator fun Signal<*>.not(): DataStarExpression = DataStarExpression("!$this")

    /**
     * Equal to the assignment operator in JavaScript, used to assign the signal value to the new value.
     */
    infix fun <T> Signal<T>.setValue(value: T): DataStarExpression = DataStarExpression("$this = $value")

    override fun toString(): String = expression
}
