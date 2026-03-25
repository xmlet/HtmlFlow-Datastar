package pt.isel.datastar.builders

import pt.isel.datastar.Signal
import pt.isel.datastar.expressions.ActionType
import pt.isel.datastar.expressions.DataStarAction
import pt.isel.datastar.expressions.DataStarAction.Companion.addApostrophe
import pt.isel.datastar.expressions.DataStarAction.Companion.convertFuncToPath
import pt.isel.datastar.expressions.DataStarExpression
import kotlin.reflect.KFunction

/**
 * A [ExpressionBuilder] is the class where the lambdas received on data attributes will get applied
 * creating DataStarExpressions. It enables the infix functions for DataStarExpressions and action creating functions.
 *
 * @property builderExpression saves the created expressions.
 */
open class ExpressionBuilder {
    private val builderExpression = StringBuilder()

    private fun appendExpression(value: String) {
        if (builderExpression.isNotEmpty()) {
            builderExpression.append("; ")
        }
        builderExpression.append(value)
    }

    operator fun DataStarExpression.unaryPlus() {
        appendExpression(this.toString())
    }

    operator fun String.unaryPlus() {
        appendExpression(this)
    }

    // gets substituted by a String.unaryPlus call by the plugin with the transpiled JS string
    operator fun Function<*>.unaryPlus() {
        appendExpression(this.toString())
    }

    fun getExpression() = builderExpression.toString()

    fun get(
        func: KFunction<*>,
        options: String? = null,
    ) = DataStarAction(ActionType.GET, convertFuncToPath(func), options)

    fun get(
        path: String,
        options: String? = null,
    ) = DataStarAction(ActionType.GET, addApostrophe(path), options)

    fun peek(callable: () -> String) = DataStarAction(ActionType.PEEK, callable)

    fun peek(js: String) = DataStarAction(ActionType.PEEK, js)

    fun setAll(
        value: Any,
        filter: String? = null,
    ) = DataStarAction(ActionType.SET_ALL, value, filter ?: "")

    fun toggleAll(filter: String? = null) = DataStarAction(ActionType.TOGGLE_ALL, filter ?: "")

    fun post(
        func: KFunction<*>,
        options: String? = null,
    ) = DataStarAction(ActionType.POST, convertFuncToPath(func), options)

    fun post(
        path: String,
        options: String? = null,
    ) = DataStarAction(ActionType.POST, addApostrophe(path), options)

    fun put(func: KFunction<*>) = DataStarAction(ActionType.PUT, convertFuncToPath(func))

    fun put(path: String) = DataStarAction(ActionType.PUT, addApostrophe(path))

    fun delete(func: KFunction<*>) = DataStarAction(ActionType.DELETE, convertFuncToPath(func))

    fun delete(path: String) = DataStarAction(ActionType.DELETE, addApostrophe(path))

    fun patch(func: KFunction<*>) = DataStarAction(ActionType.PATCH, convertFuncToPath(func))

    fun patch(path: String) = DataStarAction(ActionType.PATCH, addApostrophe(path))

    /**
     * Equal to the JavaScript && operator, used to chain multiple expressions together.
     */
    infix fun DataStarExpression.and(action: DataStarAction): DataStarExpression = DataStarExpression("$this && $action")

    /**
     * Equal to the JavaScript ! operator, used to negate an expression.
     */
    infix fun DataStarExpression.or(expression: DataStarExpression): DataStarExpression = DataStarExpression("$this || $expression")

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
}
