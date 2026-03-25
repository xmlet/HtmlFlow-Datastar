package pt.isel.datastar.modifiers.base

abstract class ModifierAccumulator : ModifierScope {
    private val sb = StringBuilder()

    override fun addModifier(mod: String) {
        sb.append(mod)
    }

    override fun toString(): String = sb.toString()
}
