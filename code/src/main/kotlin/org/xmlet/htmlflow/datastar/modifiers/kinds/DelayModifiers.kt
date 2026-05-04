package org.xmlet.htmlflow.datastar.modifiers.kinds

import kotlin.time.Duration

interface DelayModifiers : org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter {
    fun delay(time: Duration) = append("__delay.$time")
}
