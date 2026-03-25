package pt.isel.datastar.modifiers.attributes

import pt.isel.datastar.modifiers.base.ModifierAccumulator
import pt.isel.datastar.modifiers.common.CaseModifiers
import pt.isel.datastar.modifiers.common.DebounceModifiers
import pt.isel.datastar.modifiers.common.DelayModifiers
import pt.isel.datastar.modifiers.common.DurationModifiers
import pt.isel.datastar.modifiers.common.EventModifiers
import pt.isel.datastar.modifiers.common.IgnoreModifiers
import pt.isel.datastar.modifiers.common.IntersectModifiers
import pt.isel.datastar.modifiers.common.OutputFormatModifiers
import pt.isel.datastar.modifiers.common.ThrottleModifiers
import pt.isel.datastar.modifiers.common.ViewTransitionModifiers

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
