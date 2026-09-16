package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.expressions.ActionOptions
import org.xmlet.htmlflow.datastar.expressions.ActionType
import org.xmlet.htmlflow.datastar.expressions.DataStarAction
import org.xmlet.htmlflow.datastar.expressions.DataStarAction.Companion.addApostrophe
import org.xmlet.htmlflow.datastar.expressions.DataStarAction.Companion.convertFuncToPath
import org.xmlet.htmlflow.datastar.expressions.DataStarExpression
import org.xmlet.htmlflow.datastar.expressions.DataStarExpressionOp
import org.xmlet.htmlflow.datastar.expressions.DataStarExpressionRegistry
import org.xmlet.htmlflow.datastar.expressions.Signal
import org.xmlet.htmlflow.datastar.expressions.SignalPatchFilter
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

/**
 * A DSL builder for constructing DataStar expressions by combining signals, actions,
 * and operators.
 *
 * Multiple expressions are accumulated as AST nodes and separated by semicolons in the final output.
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

    override operator fun Signal<*>.unaryPlus() {
        registry.explicit(this)
    }

    override operator fun String.unaryPlus() {
        registry.explicit(DataStarExpressionOp(this))
    }

    override fun getExpression(): String = registry.render()

    /**
     * Frontend actions
     */
    override fun peek(callable: () -> String): DataStarAction = registry.action(DataStarAction(ActionType.PEEK, callable))

    override fun peek(js: String): DataStarAction = registry.action(DataStarAction(ActionType.PEEK, js))

    override fun setAll(
        value: Any,
        filterBuilder: SignalPatchFilter.() -> Unit,
    ): DataStarAction =
        registry.action(DataStarAction(ActionType.SET_ALL, value, SignalPatchFilter().applySignalPatchFilter(filterBuilder)))

    override fun toggleAll(filterBuilder: SignalPatchFilter.() -> Unit): DataStarAction =
        registry.action(DataStarAction(ActionType.TOGGLE_ALL, SignalPatchFilter().applySignalPatchFilter(filterBuilder)))

    /**
     * Backend actions
     */
    override fun get(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        return registry.action(DataStarAction(ActionType.GET, convertFuncToPath(func), options))
    }

    override fun get(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        return registry.action(DataStarAction(ActionType.GET, addApostrophe(path), options))
    }

    override fun post(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        return registry.action(DataStarAction(ActionType.POST, convertFuncToPath(func), options))
    }

    override fun post(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        return registry.action(DataStarAction(ActionType.POST, addApostrophe(path), options))
    }

    override fun put(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        return registry.action(DataStarAction(ActionType.PUT, convertFuncToPath(func), options))
    }

    override fun put(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        return registry.action(DataStarAction(ActionType.PUT, addApostrophe(path), options))
    }

    override fun delete(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        return registry.action(DataStarAction(ActionType.DELETE, convertFuncToPath(func), options))
    }

    override fun delete(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        return registry.action(DataStarAction(ActionType.DELETE, addApostrophe(path), options))
    }

    override fun patch(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        return registry.action(DataStarAction(ActionType.PATCH, convertFuncToPath(func), options))
    }

    override fun patch(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        return registry.action(DataStarAction(ActionType.PATCH, addApostrophe(path), options))
    }

    /**
     * JavaScript operators
     */

    override operator fun DataStarExpression.not(): DataStarExpressionOp = registry.not(this)

    override infix fun DataStarExpression.and(expression: DataStarExpression): DataStarExpressionOp = registry.and(this, expression)

    override infix fun String.and(expression: DataStarExpression): DataStarExpressionOp = registry.and(this, expression)

    override infix fun DataStarExpression.or(expression: DataStarExpression): DataStarExpressionOp = registry.or(this, expression)

    override infix fun String.or(expression: DataStarExpression): DataStarExpressionOp = registry.or(this, expression)

    override infix fun DataStarExpression.eq(expression: DataStarExpression): DataStarExpressionOp = registry.equal(this, expression)

    override infix fun <T> Signal<T>.eq(value: T): DataStarExpressionOp = registry.equal(this, value)

    override fun <T> Signal<T>.setValue(value: T): DataStarExpressionOp = registry.assign(this, value)

    override fun Signal<*>.setValue(expression: DataStarExpression): DataStarExpressionOp = registry.assign(this, expression)

    override fun <T, V> Signal<T>.on(prop: KProperty1<T, V>): Signal<V> = Signal("${this.name}.${prop.name}")
}
