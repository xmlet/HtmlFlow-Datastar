package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierAccumulator

/**
 * A composite builder combining [ExpressionBuilder] and [ModifierBuilder] for building
 * both DataStar expressions and modifiers in a single fluent API.
 *
 * **Example usage:**
 * ```kotlin
 * signal1 and action1
 * modifiers {
 *     case(CaseStyle.PASCAL)
 * }
 * ```
 * @param M the type of [ModifierAccumulator] used to accumulate modifiers
 * @param builderFactory the [ModifierBuilder] instance used to build modifiers alongside expressions
 */
class ExpressionModifierBuilder<M : ModifierAccumulator>(
    builderFactory: () -> M,
    private val expressionScope: DefaultExpressionScope = DefaultExpressionScope(),
    private val modifiersScope: DefaultModifiersScope<M> = DefaultModifiersScope(builderFactory),
) : ExpressionScope by expressionScope,
    ModifiersScope<M> by modifiersScope {
    data class BuildResult(
        val expression: String,
        val modifiers: String,
    )

    fun build() = BuildResult(expressionScope.build(), modifiersScope.build())
}
