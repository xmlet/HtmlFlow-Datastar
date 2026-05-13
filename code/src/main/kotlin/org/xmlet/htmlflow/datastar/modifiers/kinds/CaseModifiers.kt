package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.CaseStyle
import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter

interface CaseModifiers : ModifierWriter {
    fun case(style: CaseStyle) = append("__case.${style.tag}")
}
