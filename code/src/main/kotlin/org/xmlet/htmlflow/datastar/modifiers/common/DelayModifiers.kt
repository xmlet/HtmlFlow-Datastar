package org.xmlet.htmlflow.datastar.modifiers.common

import kotlin.time.Duration

interface DelayModifiers : org.xmlet.htmlflow.datastar.modifiers.base.ModifierScope {
    fun delay(time: Duration) = addModifier("__delay.$time")
}
