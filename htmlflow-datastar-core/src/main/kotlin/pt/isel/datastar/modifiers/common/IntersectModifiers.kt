package pt.isel.datastar.modifiers.common

import pt.isel.datastar.modifiers.base.ModifierScope

interface IntersectModifiers :
    BaseEventModifiers,
    ModifierScope {
    fun half() = addModifier("__half")

    fun full() = addModifier("__full")
}
