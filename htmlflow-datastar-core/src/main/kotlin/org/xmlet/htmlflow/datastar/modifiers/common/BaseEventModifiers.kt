package org.xmlet.htmlflow.datastar.modifiers.common

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierScope

interface BaseEventModifiers :
    DelayModifiers,
    DebounceModifiers,
    ThrottleModifiers,
    ViewTransitionModifiers,
    ModifierScope {
    fun once() = addModifier("__once")
}
