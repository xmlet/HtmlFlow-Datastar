package org.xmlet.htmlflow.datastar.modifiers.common

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierScope

interface ViewTransitionModifiers : ModifierScope {
    fun viewTransition() = addModifier("__viewtransition")
}
