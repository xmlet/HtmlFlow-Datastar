package org.xmlet.htmlflow.datastar.expressions.ast

import org.xmlet.htmlflow.datastar.serialization.Serializer

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
    fun render(expression: JsExpr): String = buildString { renderInto(this, expression) }

    /** Appends an expression in one traversal without materializing strings for its subtrees. */
    fun renderInto(
        output: StringBuilder,
        expression: JsExpr,
    ) {
        when (expression) {
            is JsExpr.Source -> output.append(expression.value)
            is JsExpr.Raw -> output.append(expression.value.js)
            is JsExpr.Literal -> output.append(Serializer.serializeValue(expression.value, expression.quoteStyle))
            is JsExpr.Unary -> {
                output.append(expression.operator.symbol)
                operand(output, expression.operand, precedence(expression.operand) < UNARY_PRECEDENCE)
            }

            is JsExpr.Binary -> {
                binaryOperand(output, expression.left, expression.operator, isRightOperand = false)
                output.append(' ').append(expression.operator.symbol).append(' ')
                binaryOperand(output, expression.right, expression.operator, isRightOperand = true)
            }

            is JsExpr.Assignment -> {
                assignmentOperand(output, expression.target, isRightOperand = false)
                output.append(" = ")
                assignmentOperand(output, expression.value, isRightOperand = true)
            }
        }
    }

    /**
     * Renders an operand of [operator], preserving both precedence and equal-precedence
     * associativity.
     */
    private fun binaryOperand(
        output: StringBuilder,
        expression: JsExpr,
        operator: BinaryOperator,
        isRightOperand: Boolean,
    ) {
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
        operand(output, expression, needsParentheses)
    }

    /** Renders an assignment operand while preserving JavaScript's right-associative assignment grouping. */
    private fun assignmentOperand(
        output: StringBuilder,
        expression: JsExpr,
        isRightOperand: Boolean,
    ) {
        operand(
            output,
            expression,
            precedence(expression) < ASSIGNMENT_PRECEDENCE ||
                precedence(expression) == ASSIGNMENT_PRECEDENCE &&
                !isRightOperand,
        )
    }

    /** Renders [expression] and adds parentheses only when [parenthesize] is required by its parent. */
    private fun operand(
        output: StringBuilder,
        expression: JsExpr,
        parenthesize: Boolean,
    ) {
        if (parenthesize) output.append('(')
        renderInto(output, expression)
        if (parenthesize) output.append(')')
    }

    /** Returns the precedence used to determine whether an expression needs parentheses in its parent. */
    private fun precedence(expression: JsExpr): Int =
        when (expression) {
            is JsExpr.Binary -> expression.operator.precedence
            is JsExpr.Assignment -> ASSIGNMENT_PRECEDENCE
            is JsExpr.Unary -> UNARY_PRECEDENCE
            // Raw source is emitted verbatim; callers provide its grouping. Only structural
            // operations participate in the renderer's precedence and associativity rules.
            is JsExpr.Source, is JsExpr.Raw, is JsExpr.Literal -> PRIMARY_PRECEDENCE
        }
}
