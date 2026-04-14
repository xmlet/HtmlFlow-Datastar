package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.Signal
import org.xmlet.htmlflow.datastar.expressions.ActionType
import org.xmlet.htmlflow.datastar.expressions.DataStarAction
import org.xmlet.htmlflow.datastar.expressions.DataStarAction.Companion.addApostrophe
import org.xmlet.htmlflow.datastar.expressions.DataStarAction.Companion.convertFuncToPath
import org.xmlet.htmlflow.datastar.expressions.DataStarExpression
import kotlin.reflect.KFunction

/**
 * A DSL builder for constructing DataStar expressions within lambda blocks passed to data attributes.
 *
 * [ExpressionBuilder] provides a fluent API for building complex DataStar expressions by combining:
 * - **Expressions**: DataStar expressions (signals, conditions, etc.) added with the unary plus operator (`+`)
 * - **Actions**: HTTP actions (GET, POST, PUT, DELETE, PATCH) and data manipulation actions (PEEK, SET_ALL, TOGGLE_ALL)
 * - **Operators**: Infix operators for chaining expressions (`and`, `or`, `equals`) and assignment (`setValue`)
 *
 * Multiple expressions are accumulated and separated by semicolons in the final string representation.
 *
 * **Example usage:**
 * ```kotlin
 * +(signal1 and action1)
 * +(expression1 or expression2)
 * +(signal.setValue(newValue))
 * ```
 */
open class ExpressionBuilder {
    private val builderExpression = mutableListOf<DataStarExpression>()

    private fun appendExpression(expression: DataStarExpression) {
        builderExpression.addLast(expression)
    }

    private fun removeIfPresent(vararg expressions: DataStarExpression) {
        expressions.forEach { expression ->
            if (expression !is Signal<*>) {
                val lastIndex = builderExpression.lastIndexOf(expression)
                if (lastIndex != -1) {
                    builderExpression.removeAt(lastIndex)
                }
            }
        }
    }

    operator fun Signal<*>.unaryPlus() {
        appendExpression(this)
    }

    operator fun String.unaryPlus() {
        appendExpression(DataStarExpression(this))
    }

    // gets substituted by a String.unaryPlus call by the plugin with the transpiled JS string
    operator fun Function<*>.unaryPlus() {
        appendExpression(DataStarExpression(this.toString()))
    }

    fun getExpression() = builderExpression.joinToString("; ")

    fun get(
        func: KFunction<*>,
        options: String? = null,
    ): DataStarAction {
        val action = DataStarAction(ActionType.GET, convertFuncToPath(func), options)
        appendExpression(action)
        return action
    }

    fun get(
        path: String,
        options: String? = null,
    ): DataStarAction {
        val action = DataStarAction(ActionType.GET, addApostrophe(path), options)
        appendExpression(action)
        return action
    }

    fun peek(callable: () -> String): DataStarAction {
        val action = DataStarAction(ActionType.PEEK, callable)
        appendExpression(action)
        return action
    }

    fun peek(js: String): DataStarAction {
        val action = DataStarAction(ActionType.PEEK, js)
        appendExpression(action)
        return action
    }

    fun setAll(
        value: Any,
        filter: String? = null,
    ): DataStarAction {
        val action = DataStarAction(ActionType.SET_ALL, value, filter ?: "")
        appendExpression(action)
        return action
    }

    fun toggleAll(filter: String? = null): DataStarAction {
        val action = DataStarAction(ActionType.TOGGLE_ALL, filter ?: "")
        appendExpression(action)
        return action
    }

    fun post(
        func: KFunction<*>,
        options: String? = null,
    ): DataStarAction {
        val action = DataStarAction(ActionType.POST, convertFuncToPath(func), options)
        appendExpression(action)
        return action
    }

    fun post(
        path: String,
        options: String? = null,
    ): DataStarAction {
        val action = DataStarAction(ActionType.POST, addApostrophe(path), options)
        appendExpression(action)
        return action
    }

    fun put(func: KFunction<*>): DataStarAction {
        val action = DataStarAction(ActionType.PUT, convertFuncToPath(func))
        appendExpression(action)
        return action
    }

    fun put(path: String): DataStarAction {
        val action = DataStarAction(ActionType.PUT, addApostrophe(path))
        appendExpression(action)
        return action
    }

    fun delete(func: KFunction<*>): DataStarAction {
        val action = DataStarAction(ActionType.DELETE, convertFuncToPath(func))
        appendExpression(action)
        return action
    }

    fun delete(path: String): DataStarAction {
        val action = DataStarAction(ActionType.DELETE, addApostrophe(path))
        appendExpression(action)
        return action
    }

    fun patch(func: KFunction<*>): DataStarAction {
        val action = DataStarAction(ActionType.PATCH, convertFuncToPath(func))
        appendExpression(action)
        return action
    }

    fun patch(path: String): DataStarAction {
        val action = DataStarAction(ActionType.PATCH, addApostrophe(path))
        appendExpression(action)
        return action
    }

    /**
     * Equal to the JavaScript ! operator, used to negate an expression.
     */
    operator fun DataStarExpression.not(): DataStarExpression {
        val result = DataStarExpression("!$this")
        appendExpression(result)
        return result
    }

    /**
     * Equal to the JavaScript && operator, used to chain multiple expressions together.
     */
    infix fun DataStarExpression.and(expression: DataStarExpression): DataStarExpression {
        val result = DataStarExpression("$this && $expression")
        removeIfPresent(this, expression)
        appendExpression(result)
        return result
    }

    /**
     * Equal to the JavaScript && operator, used to chain multiple expressions together.
     */
    infix fun String.and(expression: DataStarExpression): DataStarExpression {
        removeIfPresent(expression)
        val result = DataStarExpression("$this && $expression")
        appendExpression(result)
        return result
    }

    /**
     * Equal to the JavaScript || operator, used to chain multiple expressions together.
     */
    infix fun DataStarExpression.or(expression: DataStarExpression): DataStarExpression {
        val result = DataStarExpression("$this || $expression")
        removeIfPresent(this, expression)
        appendExpression(result)
        return result
    }

    /**
     * Equal to the JavaScript || operator, used to chain multiple expressions together.
     */
    infix fun String.or(expression: DataStarExpression): DataStarExpression {
        val result = DataStarExpression("$this || $expression")
        removeIfPresent(expression)
        appendExpression(result)
        return result
    }

    /**
     * Equal to the JavaScript == operator, used to compare two expressions.
     */
    infix fun DataStarExpression.eq(expression: DataStarExpression): DataStarExpression {
        val result = DataStarExpression("$this == $expression")
        removeIfPresent(this, expression)
        appendExpression(result)
        return result
    }

    /**
     * Equal to the JavaScript == operator, used to compare two expressions.
     */
    infix fun <T> Signal<T>.eq(value: T): DataStarExpression {
        val valueExpression = DataStarExpression(value.toString())
        val result = DataStarExpression("$this == $valueExpression")
        removeIfPresent(this, valueExpression)
        appendExpression(result)
        return result
    }

    /**
     * Equal to the assignment operator in JavaScript, used to assign the signal value to the new value.
     */
    fun <T> Signal<T>.setValue(value: T): DataStarExpression {
        val result = DataStarExpression("$this = $value")
        appendExpression(result)
        return result
    }

    /**
     * Equal to the assignment operator in JavaScript, used to assign the passed expression to another expression.
     */
    fun Signal<*>.setValue(expression: DataStarExpression): DataStarExpression {
        removeIfPresent(expression)
        val result = DataStarExpression("$this = $expression")
        appendExpression(result)
        return result
    }
}
