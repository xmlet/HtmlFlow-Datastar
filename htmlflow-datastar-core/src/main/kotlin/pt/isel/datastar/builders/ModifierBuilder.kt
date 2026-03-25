package pt.isel.datastar.builders

import pt.isel.datastar.modifiers.base.ModifierAccumulator

/**
 * A [ModifierBuilder] is used to create the string of modifiers that will be passed to the data attributes.
 *
 * @property builderFactory the block to apply the lambda code to create the modifiers string.
 */
open class ModifierBuilder<M : ModifierAccumulator>(
    private val builderFactory: () -> M,
) {
    private val modString = StringBuilder()

    fun modifiers(block: M.() -> Unit) {
        val builder = builderFactory()
        builder.apply(block)
        modString.append(builder)
    }

    fun getModifiers() = modString.toString()
}
