package org.xmlet.htmlflow.datastar.modifiers.common

import org.xmlet.htmlflow.datastar.modifiers.CaseStyle
import org.xmlet.htmlflow.datastar.modifiers.base.ModifierScope

interface CaseModifiers : ModifierScope {
    fun case(style: CaseStyle) = addModifier("__case.${style.tag}")
}
