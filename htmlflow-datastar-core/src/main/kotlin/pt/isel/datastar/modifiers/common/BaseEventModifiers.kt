package pt.isel.datastar.modifiers.common

import pt.isel.datastar.modifiers.base.ModifierScope

interface BaseEventModifiers :
    DelayModifiers,
    DebounceModifiers,
    ThrottleModifiers,
    ViewTransitionModifiers,
    ModifierScope {
    fun once() = addModifier("__once")
}
