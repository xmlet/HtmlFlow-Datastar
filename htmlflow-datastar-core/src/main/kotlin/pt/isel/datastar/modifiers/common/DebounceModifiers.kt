package pt.isel.datastar.modifiers.common

import pt.isel.datastar.modifiers.base.ModifierScope
import kotlin.time.Duration

interface DebounceModifiers : ModifierScope {
    fun debounce(
        time: Duration,
        block: (TimingEdgeModifiers.() -> Unit)? = null,
    ) {
        addModifier("__debounce.$time")
        block?.invoke(
            TimingEdgeModifiers(this),
        )
    }
}
