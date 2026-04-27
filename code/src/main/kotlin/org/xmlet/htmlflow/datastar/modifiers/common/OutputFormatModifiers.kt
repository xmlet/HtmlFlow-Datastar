package org.xmlet.htmlflow.datastar.modifiers.common

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierScope

interface OutputFormatModifiers : ModifierScope {
    fun terse() = addModifier("__terse")
}
