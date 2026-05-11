package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator

/**
 * A concrete implementation of [ModifierBuilder] that accumulates modifier strings
 * by delegating to a [ModifierAccumulator].
 *
 * **Example usage:**
 * ```kotlin
 * modifiers {
 *     case(CaseStyle.PASCAL)
 * }
 * ```
 *
 * @param M the type of [ModifierAccumulator] used to accumulate modifiers
 * @param builderFactory factory function that creates a new [ModifierAccumulator] instance
 */
class ModifierBuilder<M : ModifierAccumulator>(
    builderFactory: () -> M,
) : ModifierScope<M> {
    private val builder = builderFactory()

    override fun modifiers(block: M.() -> Unit) {
        builder.apply(block)
    }

    override fun getModifiers() = builder.modifiers
}
