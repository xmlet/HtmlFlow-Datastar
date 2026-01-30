package pt.isel.datastar.modifiers.common

import pt.isel.datastar.modifiers.base.ModifierScope
import kotlin.time.Duration

interface DelayModifiers : ModifierScope {
    fun delay(time: Duration) = addModifier("__delay.$time")
}
