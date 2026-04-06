package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierAccumulator

/**
 * A concrete implementation of [ModifierBuilder] that builds modifier strings using the Builder pattern.
 *
 * This class accumulates modifiers by delegating to a [ModifierAccumulator] instance. Each call to [modifiers]
 * applies a configuration block to the accumulator and appends its string representation to the final modifier string.
 * Example usage:**
 * ```kotlin
 *
 *   modifiers { // Modifier accumulation
 *       // Configure modifiers
 *       case(CaseStyle.PASCAL)
 *  }
 *
 * @param M the type of [ModifierAccumulator] used to build and accumulate the modifiers
 * @param builderFactory a factory function that creates a new instance of the [ModifierAccumulator] used to build the modifiers string
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
 * A builder interface for constructing modifier strings to be applied to data attributes.
 *
 * This interface defines the contract for accumulating and building modifier strings through a fluent API.
 * Implementations use the Builder pattern with a [ModifierAccumulator] to configure modifiers step by step
 * and produce the final modifier string representation.
 *
 * @param M the type of [ModifierAccumulator] used to configure and accumulate the modifiers
 */
interface ModifierBuilder<M : ModifierAccumulator> {
    /**
     * Applies the given configuration block to the modifier accumulator of type [M].
     *
     * The block is executed in the context of the accumulator, allowing for a fluent DSL-style
     * configuration of modifiers. Multiple calls to this method will accumulate modifiers into a single string.
     *
     * @param block a lambda with receiver that configures the [ModifierAccumulator]
     */
    fun modifiers(block: M.() -> Unit)

    /**
     * Returns the complete modifier string built from all accumulated modifiers.
     *
     * This string contains all modifiers configured through previous calls to [modifiers] and is ready
     * to be passed to data attributes in the generated HTML.
     *
     * @return the accumulated modifiers as a string
     */
    fun getModifiers(): String
}
