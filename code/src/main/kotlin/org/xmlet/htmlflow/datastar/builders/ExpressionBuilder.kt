package org.xmlet.htmlflow.datastar.builders

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
class ExpressionBuilder(
    private val expressionScope: DefaultExpressionScope = DefaultExpressionScope(),
) : ExpressionScope by expressionScope {
    fun build(): String = expressionScope.build()
}
