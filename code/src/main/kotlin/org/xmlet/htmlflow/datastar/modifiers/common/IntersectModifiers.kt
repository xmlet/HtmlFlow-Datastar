package org.xmlet.htmlflow.datastar.modifiers.common

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierScope

interface IntersectModifiers :
    BaseEventModifiers,
    ModifierScope {
    fun half() = addModifier("__half")

    fun full() = addModifier("__full")
}
