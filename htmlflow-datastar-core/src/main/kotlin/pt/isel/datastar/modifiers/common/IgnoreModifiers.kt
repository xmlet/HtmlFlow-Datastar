package pt.isel.datastar.modifiers.common

import pt.isel.datastar.modifiers.base.ModifierScope

interface IgnoreModifiers : ModifierScope {
    fun self() = addModifier("__self")
}
