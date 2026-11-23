package org.xmlet.htmlflow.datastar.expressions.ast

import org.xmlet.htmlflow.datastar.expressions.DataStarAction
import org.xmlet.htmlflow.datastar.expressions.DataStarExpression
import org.xmlet.htmlflow.datastar.expressions.DataStarExpressionOp
import org.xmlet.htmlflow.datastar.serialization.QuoteStyle
import org.xmlet.htmlflow.datastar.serialization.RawJS
import java.util.IdentityHashMap

private const val JAVASCRIPT_STATEMENT_SEPARATOR = "; "

/**
 * Builds and renders the ordered expression program for one output expression.
 *
 * The program sits on top of the structural [JsExpr] AST. It keeps the top-level statements in
 * registration order and composes operations structurally, rendering only when the attribute value
 * is requested.
 *
 * Statements registered through `explicit` always remain in the output. Statements registered
 * through `register` or produced by `not`, `and`, `or`, `equal`, and `assign` are provisional: they
 * occupy a removable slot that is consumed when the expression is composed into a larger
 * operation.
 */
internal class DataStarExpressionRegistry {
    private val statements = mutableListOf<JsExpr?>()
    private val slots = IdentityHashMap<DataStarExpression, Int>()

    /** Registers an explicit statement that remains in the final output when reused as an operand. */
    fun explicit(expression: DataStarExpression) {
        statements += node(expression)
    }

    /** Registers raw JavaScript as an explicit statement without quoting or escaping its source. */
    fun explicit(expression: RawJS) {
        statements += JsExpr.Raw(expression)
    }

    /** Registers an action as a provisional statement. */
    fun register(action: DataStarAction) {
        slots[action] = append(node(action))
    }

    /** Creates and registers a negated expression. */
    fun not(expression: DataStarExpression): DataStarExpressionOp =
        operation(expression) { JsExpr.Unary(UnaryOperator.NOT, node(expression)) }

    /** Creates and registers a logical conjunction. */
    fun and(
        left: DataStarExpression,
        right: DataStarExpression,
    ): DataStarExpressionOp = operation(left, right) { JsExpr.Binary(node(left), BinaryOperator.AND, node(right)) }

    /** Creates and registers a logical conjunction with raw JavaScript on the left. */
    fun and(
        left: RawJS,
        right: DataStarExpression,
    ): DataStarExpressionOp = operation(right) { JsExpr.Binary(JsExpr.Raw(left), BinaryOperator.AND, node(right)) }

    /** Creates and registers a logical disjunction. */
    fun or(
        left: DataStarExpression,
        right: DataStarExpression,
    ): DataStarExpressionOp = operation(left, right) { JsExpr.Binary(node(left), BinaryOperator.OR, node(right)) }

    /** Creates and registers a logical disjunction with raw JavaScript on the left. */
    fun or(
        left: RawJS,
        right: DataStarExpression,
    ): DataStarExpressionOp = operation(right) { JsExpr.Binary(JsExpr.Raw(left), BinaryOperator.OR, node(right)) }

    /** Creates and registers an equality comparison. */
    fun equal(
        left: DataStarExpression,
        right: DataStarExpression,
    ): DataStarExpressionOp = operation(left, right) { JsExpr.Binary(node(left), BinaryOperator.EQUAL, node(right)) }

    /** Creates and registers an equality comparison against a Kotlin value. */
    fun equal(
        left: DataStarExpression,
        right: Any?,
    ): DataStarExpressionOp = operation(left) { JsExpr.Binary(node(left), BinaryOperator.EQUAL, literal(right)) }

    /** Creates and registers an assignment to a Kotlin value. */
    fun assign(
        target: DataStarExpression,
        value: Any?,
        quoteStyle: QuoteStyle = QuoteStyle.SINGLE,
    ): DataStarExpressionOp = operation(target) { JsExpr.Assignment(node(target), literal(value, quoteStyle)) }

    /** Creates and registers an assignment from another DataStar expression. */
    fun assign(
        target: DataStarExpression,
        value: DataStarExpression,
    ): DataStarExpressionOp = operation(target, value) { JsExpr.Assignment(node(target), node(value)) }

    /** Renders the remaining top-level statements in original registration order, separated by "; ". */
    fun render(): String =
        statements
            .filterNotNull()
            .joinToString(separator = JAVASCRIPT_STATEMENT_SEPARATOR) { JsRenderer.render(it) }

    /**
     * Returns the AST node for [expression].
     *
     * Composed operations already carry their node, so grouping survives composition and reuse
     * across programs. Signals, actions, and other expressions are treated as primary sources.
     */
    private fun node(expression: DataStarExpression): JsExpr =
        (expression as? DataStarExpressionOp)?.node ?: JsExpr.Source(expression.syntax)

    /**
     * Creates a literal rendered by the shared serializer.
     *
     * A [RawJS] value is treated as an opaque expression rather than a literal, even when it is
     * boxed as [Any].
     */
    private fun literal(
        value: Any?,
        quoteStyle: QuoteStyle = QuoteStyle.SINGLE,
    ): JsExpr = if (value is RawJS) JsExpr.Raw(value) else JsExpr.Literal(value, quoteStyle)

    /** Composes and registers an operation after consuming the provisional statements used as its operands. */
    private fun operation(
        vararg operands: DataStarExpression,
        build: () -> JsExpr,
    ): DataStarExpressionOp {
        operands.forEach { consume(it) }
        val result = DataStarExpressionOp(build())
        slots[result] = append(result.node)
        return result
    }

    /** Appends a statement node and returns its slot index. */
    private fun append(node: JsExpr): Int {
        statements += node
        return statements.lastIndex
    }

    /** Removes the provisional statement represented by [expression], if it has one. */
    private fun consume(expression: DataStarExpression) {
        slots.remove(expression)?.let { statements[it] = null }
    }
}
