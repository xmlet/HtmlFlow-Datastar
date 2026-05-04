package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter
import kotlin.time.Duration

interface ThrottleModifiers : ModifierWriter {
    fun throttle(
        time: Duration,
        block: (TimingEdgeModifiers.() -> Unit)? = null,
    ) {
        append("__throttle.$time")
        block?.invoke(TimingEdgeModifiers(this))
    }
}
