package pt.isel.datastar.modifiers.attributes

import pt.isel.datastar.modifiers.base.ModifierBuilder
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
    ModifierBuilder(),
    CaseModifiers

class DataClassModifiers :
    ModifierBuilder(),
    CaseModifiers

class DataComputedModifiers :
    ModifierBuilder(),
    CaseModifiers

class DataIgnoreModifiers :
    ModifierBuilder(),
    IgnoreModifiers

class DataIndicatorModifiers :
    ModifierBuilder(),
    CaseModifiers

class DataInitModifiers :
    ModifierBuilder(),
    DelayModifiers,
    ViewTransitionModifiers

class DataJsonSignalsModifiers :
    ModifierBuilder(),
    OutputFormatModifiers

class DataOnIntersectModifiers :
    ModifierBuilder(),
    IntersectModifiers

class DataOnIntervalModifiers :
    ModifierBuilder(),
    DurationModifiers,
    ViewTransitionModifiers

class DataOnModifiers :
    ModifierBuilder(),
    EventModifiers,
    CaseModifiers

class DataOnSignalPatchModifiers :
    ModifierBuilder(),
    DelayModifiers,
    DebounceModifiers,
    ThrottleModifiers

class DataRefModifiers :
    ModifierBuilder(),
    CaseModifiers

class DataSignalModifiers :
    ModifierBuilder(),
    CaseModifiers {
    fun ifMissing() = addModifier("__ifmissing")
}
