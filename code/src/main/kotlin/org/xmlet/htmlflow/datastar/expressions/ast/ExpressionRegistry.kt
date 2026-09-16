package org.xmlet.htmlflow.datastar.expressions.ast

/**
 * Opaque reference to an AST expression created for one [ExpressionRegistry].
 *
 * [statementIndex] identifies an implicit top-level statement that may be consumed when the
 * expression is subsequently composed into a larger expression.
 */
internal class ExpressionRef(
    internal val expression: JsExpr,
    internal val statementIndex: Int? = null,
)

/**
 * Builds and renders the ordered expression program for one output expression.
 *
 * It tracks provisional statements so callers can register values as standalone expressions and
 * later compose them into larger expressions. Composition consumes only direct implicit statement
 * slots; explicit statements are retained.
 */

private const val JAVASCRIPT_STATEMENT_SEPARATOR = "; "

internal class ExpressionRegistry {
    private val statements = mutableListOf<JsExpr?>()

    /** Registers an expression as a provisional standalone statement and returns a consumable reference. */
    fun implicit(expression: ExpressionRef): ExpressionRef = register(expression.expression, true)

    /** Registers an explicit standalone statement that remains in the final sequence when reused as an operand. */
    fun explicit(expression: ExpressionRef) {
        register(expression.expression, false)
    }

    /** Creates a reference to existing DataStar or raw JavaScript source without registering it as a statement. */
    fun source(value: String): ExpressionRef = ExpressionRef(JsExpr.Source(value))

    /**
     * Creates a safely rendered JavaScript literal for primitive Kotlin values.
     *
     * Other values retain the established DSL behavior and are used as already-rendered source.
     */
    fun literalOrSource(value: Any?): ExpressionRef =
        if (value == null || value is String || value is Number || value is Boolean) {
            ExpressionRef(JsExpr.Literal(value))
        } else {
            source(value.toString())
        }

    /** Composes and implicitly registers a unary expression after consuming its provisional operand statement. */
    fun unary(
        operator: UnaryOperator,
        operand: ExpressionRef,
    ): ExpressionRef {
        consume(operand)
        return register(JsExpr.Unary(operator, operand.expression), true)
    }

    /** Composes and implicitly registers a binary expression after consuming its provisional operand statements. */
    fun binary(
        left: ExpressionRef,
        operator: BinaryOperator,
        right: ExpressionRef,
    ): ExpressionRef {
        consume(left)
        consume(right)
        return register(JsExpr.Binary(left.expression, operator, right.expression), true)
    }

    /** Composes and implicitly registers an assignment after consuming its provisional operand statements. */
    fun assignment(
        target: ExpressionRef,
        value: ExpressionRef,
    ): ExpressionRef {
        consume(target)
        consume(value)
        return register(JsExpr.Assignment(target.expression, value.expression), true)
    }

    /** Renders the remaining top-level statements in original registration order, separated by "; ". */
    fun render(): String = statements.filterNotNull().joinToString(separator = JAVASCRIPT_STATEMENT_SEPARATOR) { JsRenderer.render(it) }

    /** Renders one expression reference without registering it as a top-level statement. */
    fun render(expression: ExpressionRef): String = JsRenderer.render(expression.expression)

    /** Adds a statement slot and returns a reference that can optionally remove only that slot later. */
    private fun register(
        expression: JsExpr,
        implicit: Boolean,
    ): ExpressionRef {
        statements += expression
        return ExpressionRef(expression, statements.lastIndex.takeIf { implicit })
    }

    /** Removes the direct provisional statement represented by [expression], if it has one. */
    private fun consume(expression: ExpressionRef) {
        expression.statementIndex?.let { statements[it] = null }
    }
}
