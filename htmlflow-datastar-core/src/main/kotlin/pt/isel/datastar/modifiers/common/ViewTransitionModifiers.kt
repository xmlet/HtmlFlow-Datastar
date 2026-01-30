package pt.isel.datastar.modifiers.common

import pt.isel.datastar.modifiers.base.ModifierScope

interface ViewTransitionModifiers : ModifierScope {
    fun viewTransition() = addModifier("__viewtransition")
}
