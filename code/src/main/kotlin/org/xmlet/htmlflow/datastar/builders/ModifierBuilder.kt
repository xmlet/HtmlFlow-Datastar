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
    private val modifiersScope: DefaultModifiersScope<M> = DefaultModifiersScope(builderFactory),
) : ModifiersScope<M> by modifiersScope {
    fun build() = modifiersScope.build()
}
