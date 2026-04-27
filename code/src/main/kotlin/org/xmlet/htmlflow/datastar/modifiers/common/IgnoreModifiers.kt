package org.xmlet.htmlflow.datastar.modifiers.common

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierScope

interface IgnoreModifiers : ModifierScope {
    fun self() = addModifier("__self")
}
