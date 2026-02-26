package pt.isel.datastar.builders

import pt.isel.datastar.modifiers.base.ModifierBuilder

class ModBuilder<M : ModifierBuilder>(
    private val builderFactory: () -> M,
) {
    private var modString: String = ""

    fun mods(block: M.() -> Unit) {
        val builder = builderFactory()
        builder.apply(block)
        modString = builder.toString()
    }

    val mods: String get() = modString
}
