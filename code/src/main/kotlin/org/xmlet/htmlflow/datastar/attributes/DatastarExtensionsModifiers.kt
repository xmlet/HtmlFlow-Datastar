package org.xmlet.htmlflow.datastar.attributes

import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlflow.datastar.builders.EventExpressionBuilder
import org.xmlet.htmlflow.datastar.builders.ExpressionModifierBuilder
import org.xmlet.htmlflow.datastar.builders.ModifierBuilder
import org.xmlet.htmlflow.datastar.events.Event
import org.xmlet.htmlflow.datastar.expressions.Signal
import org.xmlet.htmlflow.datastar.expressions.SignalPatchFilter
import org.xmlet.htmlflow.datastar.modifiers.CaseStyle
import org.xmlet.htmlflow.datastar.modifiers.CaseStyle.Companion.extractCaseStyle
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataClassModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataComputedModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataIgnoreModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataIndicatorModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataInitModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataJsonSignalsModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataOnIntersectModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataOnIntervalModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataOnSignalPatchModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataSignalModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataSignalsModifiers

/**
 *
 * Initializes one or more signals with their initial values and modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signals attribute will be added
 * @param signals pairs of signal names and their corresponding values
 * @param block configuration lambda for signal modifiers
 * @return a list of Signal instances with the given names
 */
fun <E : Element<*, *>, P : Element<*, *>, T> Element<E, P>.dataSignals(
    vararg signals: Pair<String, T?>,
    block: ModifierBuilder<DataSignalsModifiers>.() -> Unit = {},
): List<Signal<T>> {
    signals.toList().toJson().also {
        val mods = ModifierBuilder(::DataSignalsModifiers).apply(block).build()
        this.visitor.visitAttribute("data-signals$mods", it)
    }
    return signals.map { (name, _) ->
        Signal(name)
    }
}

/**
 *
 * Initializes a single signal with its initial value and modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @param R type of the value of the signal
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param value the value of the signal
 * @param block configuration lambda for signal modifiers
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>, R> Element<E, P>.dataSignal(
    name: String,
    value: R,
    block: ModifierBuilder<DataSignalModifiers>.() -> Unit = {},
): Signal<R> {
    val mods = ModifierBuilder(::DataSignalModifiers).apply(block).build()
    val res =
        when (value) {
            is String -> "'$value'"
            null -> ""
            else -> "$value"
        }

    this.visitor.visitAttribute("data-signals:$name$mods", res)

    val caseStyle = mods.extractCaseStyle() ?: CaseStyle.CAMEL

    return Signal(name, caseStyle)
}

/**
 * Creates a data signal without an initial value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param block configuration lambda for signal modifiers
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataSignal(
    name: String,
    block: ModifierBuilder<DataSignalModifiers>.() -> Unit = {},
): Signal<Any?> = dataSignal(name, null, block)

fun <E : Element<*, *>, P : Element<*, *>, EVT : Event> Element<E, P>.dataOn(
    event: EVT,
    block: EventExpressionBuilder<EVT>.() -> Unit,
) {
    val result =
        EventExpressionBuilder(event)
            .apply(block)
            .build()
    val expr = result.expression
    val mods = result.modifiers

    this.visitor.visitAttribute("data-on:$event$mods", expr)
}

/**
 *
 * Runs an expression once when an element is initialized, with modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-init attribute will be added
 * @param block configuration lambda for initialization modifiers and create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataInit(block: ExpressionModifierBuilder<DataInitModifiers>.() -> Unit) {
    val result = ExpressionModifierBuilder(::DataInitModifiers).apply(block).build()
    val expression = result.expression
    val modifiers = result.modifiers
    this.visitor.visitAttribute("data-init$modifiers", expression)
}

/**
 *
 * Creates a computed signal whose value is derived from an expression, with modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-computed attribute will be added
 * @param name the name of the signal
 * @param block configuration lambda for initialization modifiers and create expressions
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataComputed(
    name: String,
    block: ExpressionModifierBuilder<DataComputedModifiers>.() -> Unit,
): Signal<Any> {
    val result =
        ExpressionModifierBuilder(::DataComputedModifiers)
            .apply(block)
            .build()
    val expression = result.expression
    val modifiers = result.modifiers

    this.visitor.visitAttribute("data-computed-$name$modifiers", expression)

    val caseStyle = modifiers.extractCaseStyle() ?: CaseStyle.CAMEL

    return Signal(name, caseStyle)
}

/**
 *
 * Initializes multiple signals from a JSON object, with modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-json-signals attribute will be added
 * @param jsObj a JavaScript object with include and/or exclude properties that are regular expressions, that filter which signals to watch.
 * @param block configuration lambda for JSON signals modifiers
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataJsonSignals(
    jsObj: SignalPatchFilter,
    block: ModifierBuilder<DataJsonSignalsModifiers>.() -> Unit,
) {
    val modifiers = ModifierBuilder(::DataJsonSignalsModifiers).apply(block).build()
    this.visitor.visitAttribute("data-json-signals$modifiers", jsObj.render())
}

/**
 *
 * Adds or removes a class from an element based on an expression.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-class attribute will be added
 * @param className the name of the class from the element
 * @param block configuration lambda for initialization modifiers and create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataClass(
    className: String,
    block: ExpressionModifierBuilder<DataClassModifiers>.() -> Unit,
) {
    val result =
        ExpressionModifierBuilder(::DataClassModifiers)
            .apply(block)
            .build()
    val expression = result.expression
    val modifiers = result.modifiers

    this.visitor.visitAttribute("data-class:$className$modifiers", expression)
}

/**
 *
 * Runs an expression at a regular interval.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-Interval attribute will be added
 * @param block configuration lambda for initialization modifiers and create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnInterval(
    block: ExpressionModifierBuilder<DataOnIntervalModifiers>.() -> Unit,
) {
    val result =
        ExpressionModifierBuilder(::DataOnIntervalModifiers)
            .apply(block)
            .build()
    val expression = result.expression
    val modifiers = result.modifiers
    this.visitor.visitAttribute("data-on-interval$modifiers", expression)
}

/**
 *
 * Runs an expression when the element intersects with the viewport.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-intersect attribute will be added
 * @param block configuration lambda for initialization modifiers and create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnIntersect(
    block: ExpressionModifierBuilder<DataOnIntersectModifiers>.() -> Unit,
) {
    val result =
        ExpressionModifierBuilder(::DataOnIntersectModifiers)
            .apply(block)
            .build()
    val expression = result.expression
    val modifiers = result.modifiers
    this.visitor.visitAttribute("data-on-intersect$modifiers", expression)
}

/**
 *
 * Tells DataStar to ignore this element and its descendants.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-intersect attribute will be added
 * @param block configuration lambda for initialization modifiers and create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataIgnore(block: ExpressionModifierBuilder<DataIgnoreModifiers>.() -> Unit) {
    val result =
        ExpressionModifierBuilder(::DataIgnoreModifiers)
            .apply(block)
            .build()
    val expression = result.expression
    val modifiers = result.modifiers
    this.visitor.visitAttribute("data-ignore$modifiers", expression)
}

/**
 *
 * Runs an expression whenever any Signal is patched.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-signal-patch attribute will be added
 * @param block configuration lambda for initialization modifiers and create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnSignalPatch(
    block: ExpressionModifierBuilder<DataOnSignalPatchModifiers>.() -> Unit,
) {
    val result =
        ExpressionModifierBuilder(::DataOnSignalPatchModifiers)
            .apply(block)
            .build()
    val expression = result.expression
    val modifiers = result.modifiers
    this.visitor.visitAttribute("data-on-signal-patch$modifiers", expression)
}

/**
 *
 * Creates a signal and sets its value to true while a fetch request is in flight, otherwise false.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-indicator attribute will be added
 * @param name the name of the indicator signal
 * @param block configuration lambda for initialization modifiers and create expressions
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataIndicator(
    name: String,
    block: ExpressionModifierBuilder<DataIndicatorModifiers>.() -> Unit,
): Signal<Boolean> {
    val result =
        ExpressionModifierBuilder(::DataIndicatorModifiers)
            .apply(block)
            .build()
    val expression = result.expression
    val modifiers = result.modifiers
    this.visitor.visitAttribute("data-indicator:$name$modifiers", expression)
    return Signal(name)
}
