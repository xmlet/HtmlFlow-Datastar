package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierScope

/**
 * Defines a scope for building modifiers in a fluent API style. Implementations of this interface
 * should provide a way to accumulate modifier strings based on the provided block of code.
 */
interface ModifiersScope<M : ModifierScope> {
    fun modifiers(block: M.() -> Unit)
}
