package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.expressions.DataStarAction
import org.xmlet.htmlflow.datastar.expressions.DataStarExpression
import org.xmlet.htmlflow.datastar.expressions.DataStarExpressionOp
import org.xmlet.htmlflow.datastar.expressions.Signal
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

interface ExpressionScope {
    operator fun Signal<*>.unaryPlus()

    operator fun String.unaryPlus()

    /**
     * Frontend actions
     */
    fun peek(callable: () -> String): DataStarAction

    fun peek(js: String): DataStarAction

    fun setAll(
        value: Any,
        filter: String? = null,
    ): DataStarAction

    fun toggleAll(filter: String? = null): DataStarAction

    /**
     * Backend actions
     */
    fun get(
        func: KFunction<*>,
        options: String? = null,
    ): DataStarAction

    fun get(
        path: String,
        options: String? = null,
    ): DataStarAction

    fun post(
        func: KFunction<*>,
        options: String? = null,
    ): DataStarAction

    fun post(
        path: String,
        options: String? = null,
    ): DataStarAction

    fun put(func: KFunction<*>): DataStarAction

    fun put(path: String): DataStarAction

    fun delete(func: KFunction<*>): DataStarAction

    fun delete(path: String): DataStarAction

    fun patch(func: KFunction<*>): DataStarAction

    fun patch(path: String): DataStarAction

    /**
     * JavaScript operators
     */
    operator fun <T> Signal<T>.not(): DataStarExpressionOp

    infix fun DataStarExpression.and(expression: DataStarExpression): DataStarExpressionOp

    infix fun String.and(expression: DataStarExpression): DataStarExpressionOp

    infix fun DataStarExpression.or(expression: DataStarExpression): DataStarExpressionOp

    infix fun String.or(expression: DataStarExpression): DataStarExpressionOp

    infix fun DataStarExpression.eq(expression: DataStarExpression): DataStarExpressionOp

    infix fun <T> Signal<T>.eq(value: T): DataStarExpressionOp

    fun <T> Signal<T>.setValue(value: T): DataStarExpressionOp

    fun Signal<*>.setValue(expression: DataStarExpression): DataStarExpressionOp

    /**
     * Accessing properties of signals
     */
    fun <T, V> Signal<T>.on(prop: KProperty1<T, V>): Signal<V>
}
