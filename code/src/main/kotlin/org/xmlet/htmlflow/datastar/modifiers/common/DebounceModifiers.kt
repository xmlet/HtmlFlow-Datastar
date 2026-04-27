package org.xmlet.htmlflow.datastar.modifiers.common

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierScope
import kotlin.time.Duration

interface DebounceModifiers : ModifierScope {
    fun debounce(
        time: Duration,
        block: (TimingEdgeModifiers.() -> Unit)? = null,
    ) {
        addModifier("__debounce.$time")
        block?.invoke(TimingEdgeModifiers(this))
    }
}
