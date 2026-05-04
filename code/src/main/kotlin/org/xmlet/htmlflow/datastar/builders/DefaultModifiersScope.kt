package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator

/**
 * A default implementation of [ModifiersScope] that uses a [StringBuilder] to accumulate modifier strings. This class delegates the actual building of modifiers to a [org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator]
 * instance, which is provided via the [builderFactory] parameter. The accumulated modifiers can be retrieved as a single string using the [build] method.
 * @param builderFactory a factory function that creates a new instance of [org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator] to be used for accumulating modifiers
 */
class DefaultModifiersScope<M : ModifierAccumulator>(
    builderFactory: () -> M,
) : ModifiersScope<M> {
    private val builder = builderFactory()

    override fun modifiers(block: M.() -> Unit) {
        builder.apply(block)
    }

    fun build() = builder.modifiers
}
