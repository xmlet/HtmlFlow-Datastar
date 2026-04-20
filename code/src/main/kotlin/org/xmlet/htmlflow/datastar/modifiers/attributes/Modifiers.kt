package org.xmlet.htmlflow.datastar.modifiers.attributes

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierAccumulator
import org.xmlet.htmlflow.datastar.modifiers.common.CaseModifiers
import org.xmlet.htmlflow.datastar.modifiers.common.DebounceModifiers
import org.xmlet.htmlflow.datastar.modifiers.common.DelayModifiers
import org.xmlet.htmlflow.datastar.modifiers.common.DurationModifiers
import org.xmlet.htmlflow.datastar.modifiers.common.EventModifiers
import org.xmlet.htmlflow.datastar.modifiers.common.IgnoreModifiers
import org.xmlet.htmlflow.datastar.modifiers.common.IntersectModifiers
import org.xmlet.htmlflow.datastar.modifiers.common.OutputFormatModifiers
import org.xmlet.htmlflow.datastar.modifiers.common.ThrottleModifiers
import org.xmlet.htmlflow.datastar.modifiers.common.ViewTransitionModifiers

class DataBindModifiers :
    ModifierAccumulator(),
    CaseModifiers

class DataClassModifiers :
    ModifierAccumulator(),
    CaseModifiers

class DataComputedModifiers :
    ModifierAccumulator(),
    CaseModifiers

class DataIgnoreModifiers :
    ModifierAccumulator(),
    IgnoreModifiers

class DataIndicatorModifiers :
    ModifierAccumulator(),
    CaseModifiers

class DataInitModifiers :
    ModifierAccumulator(),
    DelayModifiers,
    ViewTransitionModifiers

class DataJsonSignalsModifiers :
    ModifierAccumulator(),
    OutputFormatModifiers

class DataOnIntersectModifiers :
    ModifierAccumulator(),
    IntersectModifiers

class DataOnIntervalModifiers :
    ModifierAccumulator(),
    DurationModifiers,
    ViewTransitionModifiers

class DataOnModifiers :
    ModifierAccumulator(),
    EventModifiers,
    CaseModifiers

class DataOnSignalPatchModifiers :
    ModifierAccumulator(),
    DelayModifiers,
    DebounceModifiers,
    ThrottleModifiers

class DataRefModifiers :
    ModifierAccumulator(),
    CaseModifiers

class DataSignalModifiers :
    ModifierAccumulator(),
    CaseModifiers {
    fun ifMissing() = addModifier("__ifmissing")
}

class DataSignalsModifiers : ModifierAccumulator() {
    fun ifMissing() = addModifier("__ifmissing")
}
