package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierAccumulator

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
class DefaultModifierBuilder<M : ModifierAccumulator>(
    builderFactory: () -> M,
) : ModifierBuilder<M> {
    private val modString = StringBuilder()

    private val builder = builderFactory()

    override fun modifiers(block: M.() -> Unit) {
        builder.apply(block)
        modString.append(builder)
    }

    override fun getModifiers() = modString.toString()
}

/**
 * A builder interface for constructing modifier strings to be applied to data attributes
 * by accumulating modifiers through a fluent API.
 *
 * @param M the type of [ModifierAccumulator] used to accumulate modifiers
 */
interface ModifierBuilder<M : ModifierAccumulator> {
    /**
     * Configures modifiers via a lambda with receiver on the [ModifierAccumulator].
     *
     * @param block a lambda that configures the [ModifierAccumulator]
     */
    fun modifiers(block: M.() -> Unit)

    /**
     * Returns the complete modifier string built from all accumulated modifiers.
     *
     * @return the accumulated modifiers as a string
     */
    fun getModifiers(): String
}
