package org.xmlet.htmlflow.datastar.modifiers.base

/**
 * An abstract base class for accumulating DataStar modifiers into a string representation.
 *
 * Subclasses implement specific modifier configurations by calling [addModifier] to build
 * the final modifier string accessible via [toString].
 */
abstract class ModifierAccumulator : ModifierScope {
    private val sb = StringBuilder()

    override fun addModifier(mod: String) {
        sb.append(mod)
    }

    override fun toString(): String = sb.toString()
}
