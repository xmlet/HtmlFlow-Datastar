package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.expressions.ActionOptions
import org.xmlet.htmlflow.datastar.expressions.ActionType
import org.xmlet.htmlflow.datastar.expressions.DataStarAction
import org.xmlet.htmlflow.datastar.expressions.DataStarAction.Companion.addApostrophe
import org.xmlet.htmlflow.datastar.expressions.DataStarAction.Companion.convertFuncToPath
import org.xmlet.htmlflow.datastar.expressions.DataStarExpression
import org.xmlet.htmlflow.datastar.expressions.DataStarExpressionOp
import org.xmlet.htmlflow.datastar.expressions.Signal
import org.xmlet.htmlflow.datastar.expressions.SignalPatchFilter
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

/**
 * A DSL builder for constructing DataStar expressions by combining signals, actions,
 * and operators.
 *
 * Multiple expressions are accumulated and separated by semicolons in the final output.
 *
 * **Example usage:**
 * ```kotlin
 * +signal1
 * "confirm(\"Are you sure\")" and delete(::someRow)
 * signal2 and action2
 * signal.setValue(newValue)
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

    private fun buildSignalPatchFilter(filterBuilder: SignalPatchFilter.() -> Unit): String =
        SignalPatchFilter()
            .apply(filterBuilder)
            .toString()
            .takeUnless { it == "{}" }
            .orEmpty()

    operator fun Signal<*>.unaryPlus() {
        appendExpression(this)
    }

    operator fun String.unaryPlus() {
        appendExpression(DataStarExpressionOp(this))
    }

    fun getExpression() = builderExpression.joinToString("; ") { it.syntax }

    fun get(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.GET, convertFuncToPath(func), options)
        appendExpression(action)
        return action
    }

    fun get(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
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
        filterBuilder: SignalPatchFilter.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.SET_ALL, value, buildSignalPatchFilter(filterBuilder))
        appendExpression(action)
        return action
    }

    fun toggleAll(filterBuilder: SignalPatchFilter.() -> Unit = {}): DataStarAction {
        val action = DataStarAction(ActionType.TOGGLE_ALL, buildSignalPatchFilter(filterBuilder))
        appendExpression(action)
        return action
    }

    fun post(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.POST, convertFuncToPath(func), options)
        appendExpression(action)
        return action
    }

    fun post(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.POST, addApostrophe(path), options)
        appendExpression(action)
        return action
    }

    fun put(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.PUT, convertFuncToPath(func), options)
        appendExpression(action)
        return action
    }

    fun put(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.PUT, addApostrophe(path), options)
        appendExpression(action)
        return action
    }

    fun delete(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.DELETE, convertFuncToPath(func), options)
        appendExpression(action)
        return action
    }

    fun delete(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.DELETE, addApostrophe(path), options)
        appendExpression(action)
        return action
    }

    fun patch(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.PATCH, convertFuncToPath(func), options)
        appendExpression(action)
        return action
    }

    fun patch(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.PATCH, addApostrophe(path), options)
        appendExpression(action)
        return action
    }

    /**
     * Equal to the JavaScript ! operator, used to negate an expression.
     */
    operator fun <T> Signal<T>.not(): DataStarExpressionOp {
        val result = DataStarExpressionOp("!${this.syntax}")
        appendExpression(result)
        return result
    }

    /**
     * Equal to the JavaScript && operator, used to chain multiple expressions together.
     */
    infix fun DataStarExpression.and(expression: DataStarExpression): DataStarExpressionOp {
        val result = DataStarExpressionOp("${this.syntax} && ${expression.syntax}")
        removeIfPresent(this, expression)
        appendExpression(result)
        return result
    }

    /**
     * Equal to the JavaScript && operator, used to chain multiple expressions together.
     */
    infix fun String.and(expression: DataStarExpression): DataStarExpressionOp {
        removeIfPresent(expression)
        val result = DataStarExpressionOp("$this && ${expression.syntax}")
        appendExpression(result)
        return result
    }

    /**
     * Equal to the JavaScript || operator, used to chain multiple expressions together.
     */
    infix fun DataStarExpression.or(expression: DataStarExpression): DataStarExpressionOp {
        val result = DataStarExpressionOp("${this.syntax} || ${expression.syntax}")
        removeIfPresent(this, expression)
        appendExpression(result)
        return result
    }

    /**
     * Equal to the JavaScript || operator, used to chain multiple expressions together.
     */
    infix fun String.or(expression: DataStarExpression): DataStarExpressionOp {
        val result = DataStarExpressionOp("$this || ${expression.syntax}")
        removeIfPresent(expression)
        appendExpression(result)
        return result
    }

    /**
     * Equal to the JavaScript == operator, used to compare two expressions.
     */
    infix fun DataStarExpression.eq(expression: DataStarExpression): DataStarExpressionOp {
        val result = DataStarExpressionOp("${this.syntax} == ${expression.syntax}")
        removeIfPresent(this, expression)
        appendExpression(result)
        return result
    }

    /**
     * Equal to the JavaScript == operator, used to compare two expressions.
     */
    infix fun <T> Signal<T>.eq(value: T): DataStarExpressionOp {
        val result = DataStarExpressionOp("${this.syntax} == $value")
        removeIfPresent(this)
        appendExpression(result)
        return result
    }

    /**
     * Equal to the assignment operator in JavaScript, used to assign the signal value to the new value.
     */
    fun <T> Signal<T>.setValue(value: T): DataStarExpressionOp {
        val result = DataStarExpressionOp("${this.syntax} = $value")
        appendExpression(result)
        return result
    }

    /**
     * Equal to the assignment operator in JavaScript, used to assign the passed expression to another expression.
     */
    fun Signal<*>.setValue(expression: DataStarExpression): DataStarExpressionOp {
        removeIfPresent(expression)
        val result = DataStarExpressionOp("${this.syntax} = ${expression.syntax}")
        appendExpression(result)
        return result
    }

    fun <T, V> Signal<T>.on(prop: KProperty1<T, V>): Signal<V> {
        val expr = "${this.name}.${prop.name}"
        return Signal(expr)
    }
}
