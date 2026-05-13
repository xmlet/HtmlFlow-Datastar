package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter
import kotlin.time.Duration

interface DurationModifiers : ModifierWriter {
    fun duration(
        time: Duration,
        leading: Boolean,
    ) = append("__duration.$time" + if (leading) ".leading" else "")
}
