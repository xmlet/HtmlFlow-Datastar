package pt.isel.datastar.modifiers.common

import pt.isel.datastar.modifiers.base.ModifierScope

interface OutputFormatModifiers : ModifierScope {
    fun terse() = addModifier("__terse")
}
