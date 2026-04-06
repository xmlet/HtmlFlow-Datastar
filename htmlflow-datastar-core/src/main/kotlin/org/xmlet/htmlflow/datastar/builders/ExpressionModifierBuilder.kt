package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierAccumulator

/**
 * A composite builder that combines the functionalities of [ExpressionBuilder] and [ModifierBuilder].
 *
 * [ExpressionModifierBuilder] extends both builders to provide a unified DSL for data attributes that require
 * both expressions and modifiers. It delegates modifier operations to an internal [DefaultModifierBuilder] instance
 * while inheriting all expression-building capabilities from [ExpressionBuilder].
 *
 * This class is useful when you need to:
 * - Build DataStar expressions (signals, actions, operators)
 * - Accumulate modifiers for fine-grained control over data-star behavior
 * - Use both in a single fluent API
 *
 * **Example usage:**
 * ```kotlin
 * +signal1 and action1      // Expression building
 * modifiers {               // Modifier accumulation
 *     // Configure modifiers
 *     case(CaseStyle.PASCAL)
 * }
 * ```
 *
 * @param M the type of [ModifierAccumulator] used to build and accumulate modifiers
 * @param builderFactory a factory function that creates a new instance of the [ModifierAccumulator] used to build modifiers
 */
open class ExpressionModifierBuilder<M : ModifierAccumulator>(
    builderFactory: () -> M,
) : ExpressionBuilder(),
    ModifierBuilder<M> {
    private val modifierBuilder = DefaultModifierBuilder(builderFactory)

    override fun modifiers(block: M.() -> Unit) = modifierBuilder.modifiers(block)

    override fun getModifiers(): String = modifierBuilder.getModifiers()
}
