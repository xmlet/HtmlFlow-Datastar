package pt.isel.datastar.modifiers.common

import pt.isel.datastar.modifiers.base.ModifierScope

interface EventModifiers :
    BaseEventModifiers,
    ModifierScope {
    /**
     * Only for built-in events
     */
    fun passive() = addModifier("__passive")

    /**
     * Only for built-in events
     */
    fun capture() = addModifier("__capture")

    fun window() = addModifier("__window")

    fun outside() = addModifier("__outside")

    fun prevent() = addModifier("__prevent")

    fun stop() = addModifier("__stop")
}
