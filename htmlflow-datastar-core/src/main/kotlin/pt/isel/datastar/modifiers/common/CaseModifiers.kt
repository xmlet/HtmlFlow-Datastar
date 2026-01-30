package pt.isel.datastar.modifiers.common

import pt.isel.datastar.modifiers.CaseStyle
import pt.isel.datastar.modifiers.base.ModifierScope

interface CaseModifiers : ModifierScope {
    fun case(style: CaseStyle) = addModifier("__case.${style.tag}")
}
