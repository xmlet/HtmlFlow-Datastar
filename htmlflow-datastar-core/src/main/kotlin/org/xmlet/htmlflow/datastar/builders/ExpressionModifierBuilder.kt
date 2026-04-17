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
 *
 * @param M the type of [ModifierAccumulator] used to accumulate modifiers
 * @param builderFactory factory function that creates a new [ModifierAccumulator] instance
 */
open class ExpressionModifierBuilder<M : ModifierAccumulator>(
    builderFactory: () -> M,
) : ExpressionBuilder(),
    ModifierBuilder<M> {
    private val modifierBuilder = DefaultModifierBuilder(builderFactory)

    override fun modifiers(block: M.() -> Unit) = modifierBuilder.modifiers(block)

    override fun getModifiers(): String = modifierBuilder.getModifiers()
}
