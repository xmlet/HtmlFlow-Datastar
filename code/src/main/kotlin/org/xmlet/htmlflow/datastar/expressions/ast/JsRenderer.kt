package org.xmlet.htmlflow.datastar.expressions.ast

import org.xmlet.htmlflow.datastar.expressions.JavaScriptSerialization
import org.xmlet.htmlflow.datastar.expressions.QuoteStyle

/**
 * Converts the structural [JsExpr] tree into JavaScript at the attribute-output boundary.
 *
 * This is the only AST component that decides whether parentheses are required. It derives them
 * from the parent operator's precedence, associativity, and operand position.
 */
internal object JsRenderer {
    private const val ASSIGNMENT_PRECEDENCE = 3
    private const val UNARY_PRECEDENCE = 14
    private const val PRIMARY_PRECEDENCE = 20

    /** Renders [expression] into its canonical JavaScript representation. */
    fun render(expression: JsExpr): String =
        when (expression) {
            is JsExpr.Source -> expression.value
            is JsExpr.Literal -> literal(expression.value)
            is JsExpr.Unary -> expression.operator.symbol + unaryOperand(expression.operand)
            is JsExpr.Binary ->
                "${binaryOperand(expression.left, expression.operator, isRightOperand = false)} " +
                    "${expression.operator.symbol} " +
                    binaryOperand(expression.right, expression.operator, isRightOperand = true)

            is JsExpr.Assignment ->
                "${assignmentOperand(expression.target, isRightOperand = false)} = " +
                    assignmentOperand(expression.value, isRightOperand = true)
        }

    /** Serializes the primitive values accepted by [JsExpr.Literal]. */
    private fun literal(value: Any?): String =
        when (value) {
            null -> "null"
            is String -> JavaScriptSerialization.stringLiteral(value, QuoteStyle.DOUBLE)
            is Number, is Boolean -> value.toString()
            else -> error("Unsupported JavaScript literal value: ${value::class.qualifiedName}")
        }

    /** Parenthesizes a unary operand only when it binds less tightly than the unary operator. */
    private fun unaryOperand(expression: JsExpr): String = operand(expression, precedence(expression) < UNARY_PRECEDENCE)

    /**
     * Renders an operand of [operator], preserving both precedence and equal-precedence
     * associativity.
     */
    private fun binaryOperand(
        expression: JsExpr,
        operator: BinaryOperator,
        isRightOperand: Boolean,
    ): String {
        val childPrecedence = precedence(expression)
        val needsParentheses =
            childPrecedence < operator.precedence ||
                childPrecedence == operator.precedence &&
                (
                    operator.associativity == Associativity.NONE ||
                        operator.associativity == Associativity.LEFT &&
                        isRightOperand ||
                        operator.associativity == Associativity.RIGHT &&
                        !isRightOperand
                )
        return operand(expression, needsParentheses)
    }

    /** Renders an assignment operand while preserving JavaScript's right-associative assignment grouping. */
    private fun assignmentOperand(
        expression: JsExpr,
        isRightOperand: Boolean,
    ): String =
        operand(
            expression,
            precedence(expression) < ASSIGNMENT_PRECEDENCE ||
                precedence(expression) == ASSIGNMENT_PRECEDENCE &&
                !isRightOperand,
        )

    /** Renders [expression] and adds parentheses only when [parenthesize] is required by its parent. */
    private fun operand(
        expression: JsExpr,
        parenthesize: Boolean,
    ): String = render(expression).let { if (parenthesize) "($it)" else it }

    /** Returns the precedence used to determine whether an expression needs parentheses in its parent. */
    private fun precedence(expression: JsExpr): Int =
        when (expression) {
            is JsExpr.Binary -> expression.operator.precedence
            is JsExpr.Assignment -> ASSIGNMENT_PRECEDENCE
            is JsExpr.Unary -> UNARY_PRECEDENCE
            is JsExpr.Source, is JsExpr.Literal -> PRIMARY_PRECEDENCE
        }
}
