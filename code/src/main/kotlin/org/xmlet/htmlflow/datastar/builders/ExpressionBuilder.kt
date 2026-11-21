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
import org.xmlet.htmlflow.datastar.expressions.ast.DataStarExpressionRegistry
import org.xmlet.htmlflow.datastar.serialization.QuoteStyle
import org.xmlet.htmlflow.datastar.serialization.asRawJS
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

/**
 * A DSL builder for constructing DataStar expressions by combining signals, actions,
 * and operators.
 *
 * Expressions are accumulated as ordered statements and separated by semicolons in the final
 * output. Composed operations retain their AST so grouping is preserved until rendering.
 *
 * **Example usage:**
 * ```kotlin
 * +signal1
 * "confirm(\"Are you sure\")" and delete(::someRow)
 * signal2 and action2
 * signal.setValue(newValue)
 * ```
 */
class ExpressionBuilder : ExpressionScope {
    private val registry = DataStarExpressionRegistry()

    private fun SignalPatchFilter.applySignalPatchFilter(filterBuilder: SignalPatchFilter.() -> Unit): String =
        apply(filterBuilder)
            .toString()
            .takeUnless { it == "{}" }
            .orEmpty()

    private fun renderOptions(optionsBuilder: ActionOptions.() -> Unit): String = ActionOptions().apply(optionsBuilder).toString()

    override operator fun Signal<*>.unaryPlus() {
        registry.explicit(this)
    }

    override operator fun String.unaryPlus() {
        registry.explicit(this.asRawJS())
    }

    override fun getExpression(): String = registry.render()

    /**
     * Frontend actions
     */
    override fun peek(callable: () -> String): DataStarAction {
        val action = DataStarAction(ActionType.PEEK, callable)
        registry.register(action)
        return action
    }

    override fun peek(js: String): DataStarAction {
        val action = DataStarAction(ActionType.PEEK, js)
        registry.register(action)
        return action
    }

    override fun setAll(
        value: Any,
        filterBuilder: SignalPatchFilter.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.SET_ALL, value, SignalPatchFilter().applySignalPatchFilter(filterBuilder))
        registry.register(action)
        return action
    }

    override fun toggleAll(filterBuilder: SignalPatchFilter.() -> Unit): DataStarAction {
        val action = DataStarAction(ActionType.TOGGLE_ALL, SignalPatchFilter().applySignalPatchFilter(filterBuilder))
        registry.register(action)
        return action
    }

    /**
     * Backend actions
     */
    override fun get(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.GET, convertFuncToPath(func), renderOptions(optionsBuilder))
        registry.register(action)
        return action
    }

    override fun get(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.GET, addApostrophe(path), renderOptions(optionsBuilder))
        registry.register(action)
        return action
    }

    override fun post(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.POST, convertFuncToPath(func), renderOptions(optionsBuilder))
        registry.register(action)
        return action
    }

    override fun post(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.POST, addApostrophe(path), renderOptions(optionsBuilder))
        registry.register(action)
        return action
    }

    override fun put(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.PUT, convertFuncToPath(func), renderOptions(optionsBuilder))
        registry.register(action)
        return action
    }

    override fun put(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.PUT, addApostrophe(path), renderOptions(optionsBuilder))
        registry.register(action)
        return action
    }

    override fun delete(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.DELETE, convertFuncToPath(func), renderOptions(optionsBuilder))
        registry.register(action)
        return action
    }

    override fun delete(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.DELETE, addApostrophe(path), renderOptions(optionsBuilder))
        registry.register(action)
        return action
    }

    override fun patch(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.PATCH, convertFuncToPath(func), renderOptions(optionsBuilder))
        registry.register(action)
        return action
    }

    override fun patch(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.PATCH, addApostrophe(path), renderOptions(optionsBuilder))
        registry.register(action)
        return action
    }

    /**
     * JavaScript operators
     */

    override operator fun <T> Signal<T>.not(): DataStarExpressionOp = registry.not(this)

    override operator fun DataStarExpressionOp.not(): DataStarExpressionOp = registry.not(this)

    override infix fun DataStarExpression.and(expression: DataStarExpression): DataStarExpressionOp = registry.and(this, expression)

    override infix fun String.and(expression: DataStarExpression): DataStarExpressionOp = registry.and(this.asRawJS(), expression)

    override infix fun DataStarExpression.or(expression: DataStarExpression): DataStarExpressionOp = registry.or(this, expression)

    override infix fun String.or(expression: DataStarExpression): DataStarExpressionOp = registry.or(this.asRawJS(), expression)

    override infix fun DataStarExpression.eq(expression: DataStarExpression): DataStarExpressionOp = registry.equal(this, expression)

    override infix fun <T> Signal<T>.eq(value: T): DataStarExpressionOp = registry.equal(this, value)

    override fun <T> Signal<T>.setValue(
        value: T,
        quoteStyle: QuoteStyle,
    ): DataStarExpressionOp = registry.assign(this, value, quoteStyle)

    override fun Signal<*>.setValue(expression: DataStarExpression): DataStarExpressionOp = registry.assign(this, expression)

    override fun <T, V> Signal<T>.on(prop: KProperty1<T, V>): Signal<V> = Signal("${this.name}.${prop.name}")
}
