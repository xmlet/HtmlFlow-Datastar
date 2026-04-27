package org.xmlet.htmlflow.datastar.modifiers.common

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierScope
import kotlin.time.Duration

interface DurationModifiers : ModifierScope {
    fun duration(
        time: Duration,
        leading: Boolean,
    ) = addModifier("__duration.$time" + if (leading) ".leading" else "")
}
