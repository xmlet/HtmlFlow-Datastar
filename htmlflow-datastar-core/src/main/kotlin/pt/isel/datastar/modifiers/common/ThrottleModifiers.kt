package pt.isel.datastar.modifiers.common

import pt.isel.datastar.modifiers.base.ModifierScope
import kotlin.time.Duration

interface ThrottleModifiers : ModifierScope {
    fun throttle(
        time: Duration,
        block: (TimingEdgeModifiers.() -> Unit)? = null,
    ) {
        addModifier("__throttle.$time")
        block?.invoke(
            TimingEdgeModifiers(this),
        )
    }
}
