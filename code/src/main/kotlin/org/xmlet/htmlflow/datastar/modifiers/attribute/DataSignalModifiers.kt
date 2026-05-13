package org.xmlet.htmlflow.datastar.modifiers.attribute

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator
import org.xmlet.htmlflow.datastar.modifiers.kinds.CaseModifiers

class DataSignalModifiers :
    ModifierAccumulator(),
    CaseModifiers {
    fun ifMissing() = append("__ifmissing")
}
