package pt.isel.datastar.builders

import pt.isel.datastar.modifiers.base.ModifierAccumulator

/**
 * The [ExpressionModifierBuilder] has the functionalities of both the [ExpressionBuilder]
 * and [pt.isel.datastar.builders.ModifierBuilder].
 *
 * @property builderFactory same as in [pt.isel.datastar.builders.ModifierBuilder]
 */
open class ExpressionModifierBuilder<M : ModifierAccumulator>(
    builderFactory: () -> M,
) : ExpressionBuilder() {
    private val modifierBuilder = ModifierBuilder(builderFactory)

    fun modifiers(block: M.() -> Unit) = modifierBuilder.modifiers(block)

    fun getModifiers(): String = modifierBuilder.getModifiers()
}
