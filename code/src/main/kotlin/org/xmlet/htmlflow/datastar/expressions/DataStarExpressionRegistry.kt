package org.xmlet.htmlflow.datastar.expressions

import org.xmlet.htmlflow.datastar.expressions.ast.BinaryOperator
import org.xmlet.htmlflow.datastar.expressions.ast.ExpressionRef
import org.xmlet.htmlflow.datastar.expressions.ast.ExpressionRegistry
import org.xmlet.htmlflow.datastar.expressions.ast.UnaryOperator
import java.util.IdentityHashMap

/**
 * Adapts the string-backed DataStar expression types to the generic structural expression registry.
 *
 * The identity map only associates an existing DataStar value with its AST reference. It is not
 * used to find or remove operands: the generic registry consumes its own direct statement slots.
 */
internal class DataStarExpressionRegistry {
    private val registry = ExpressionRegistry()
    private val references = IdentityHashMap<DataStarExpression, ExpressionRef>()

    /** Registers an explicit statement that must remain in the final output when it is reused. */
    fun explicit(expression: DataStarExpression) {
        registry.explicit(reference(expression))
    }

    /** Registers an action as a provisional statement while preserving its existing DataStar type. */
    fun action(action: DataStarAction): DataStarAction {
        references[action] = registry.implicit(reference(action))
        return action
    }

    /** Creates and registers a negated expression. */
    fun not(expression: DataStarExpression): DataStarExpressionOp = operation(registry.unary(UnaryOperator.NOT, reference(expression)))

    /** Creates and registers a logical conjunction. */
    fun and(
        left: DataStarExpression,
        right: DataStarExpression,
    ): DataStarExpressionOp = operation(registry.binary(reference(left), BinaryOperator.AND, reference(right)))

    /** Creates and registers a logical conjunction with raw JavaScript on the left. */
    fun and(
        left: String,
        right: DataStarExpression,
    ): DataStarExpressionOp = operation(registry.binary(registry.source(left), BinaryOperator.AND, reference(right)))

    /** Creates and registers a logical disjunction. */
    fun or(
        left: DataStarExpression,
        right: DataStarExpression,
    ): DataStarExpressionOp = operation(registry.binary(reference(left), BinaryOperator.OR, reference(right)))

    /** Creates and registers a logical disjunction with raw JavaScript on the left. */
    fun or(
        left: String,
        right: DataStarExpression,
    ): DataStarExpressionOp = operation(registry.binary(registry.source(left), BinaryOperator.OR, reference(right)))

    /** Creates and registers an equality comparison. */
    fun equal(
        left: DataStarExpression,
        right: DataStarExpression,
    ): DataStarExpressionOp = operation(registry.binary(reference(left), BinaryOperator.EQUAL, reference(right)))

    /** Creates and registers an equality comparison against a Kotlin value. */
    fun equal(
        left: DataStarExpression,
        right: Any?,
    ): DataStarExpressionOp = operation(registry.binary(reference(left), BinaryOperator.EQUAL, registry.literalOrSource(right)))

    /** Creates and registers an assignment to a Kotlin value. */
    fun assign(
        target: DataStarExpression,
        value: Any?,
    ): DataStarExpressionOp = operation(registry.assignment(reference(target), registry.literalOrSource(value)))

    /** Creates and registers an assignment from another DataStar expression. */
    fun assign(
        target: DataStarExpression,
        value: DataStarExpression,
    ): DataStarExpressionOp = operation(registry.assignment(reference(target), reference(value)))

    /** Renders the final ordered sequence of statements. */
    fun render(): String = registry.render()

    private fun reference(expression: DataStarExpression): ExpressionRef = references[expression] ?: registry.source(expression.syntax)

    private fun operation(reference: ExpressionRef): DataStarExpressionOp =
        DataStarExpressionOp(registry.render(reference)).also { references[it] = reference }
}
