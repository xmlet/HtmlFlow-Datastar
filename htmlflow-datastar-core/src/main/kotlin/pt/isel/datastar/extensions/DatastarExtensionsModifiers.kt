package pt.isel.datastar.extensions

import org.xmlet.htmlapifaster.Element
import pt.isel.datastar.Signal
import pt.isel.datastar.builders.ModBuilder
import pt.isel.datastar.expressions.DataStarExpression
import pt.isel.datastar.modifiers.attributes.DataClassModifiers
import pt.isel.datastar.modifiers.attributes.DataComputedModifiers
import pt.isel.datastar.modifiers.attributes.DataInitModifiers
import pt.isel.datastar.modifiers.attributes.DataJsonSignalsModifiers
import pt.isel.datastar.modifiers.attributes.DataOnModifiers
import pt.isel.datastar.modifiers.attributes.DataSignalModifiers
import pt.isel.datastar.modifiers.attributes.DataSignalsModifiers
import pt.isel.datastar.modifiers.extractCaseStyle

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
 * @param modifiers optional modifiers for the signal attribute
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>, R> Element<E, P>.dataSignal(
    name: String,
    value: R?,
    modifiers: String = "",
): Signal<R?> {
    val res =
        when (value) {
            is String -> "'$value'"
            null -> ""
            else -> "$value"
        }

    this.visitor.visitAttribute("data-signals:$name$modifiers", res)

    val caseStyle = extractCaseStyle(modifiers)

    return Signal(name, value, caseStyle)
}

/**
 *
 * Initializes one or more signals with their initial values and modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signals attribute will be added
 * @param signals pairs of signal names and their corresponding values
 * @param modifiersBuilder configuration lambda for signal modifiers
 * @return a list of Signal instances with the given names
 */
fun <E : Element<*, *>, P : Element<*, *>, Any> Element<E, P>.dataSignals(
    vararg signals: Pair<String, Any?>,
    modifiersBuilder: ModBuilder<DataSignalsModifiers>.() -> Unit,
): List<Signal<Any?>> {
    signals.toList().toJson().also {
        val mods = ModBuilder(::DataSignalsModifiers).apply(modifiersBuilder).mods
        signals.toList().toJson().also {
            this.visitor.visitAttribute("data-signals$mods", it)
        }
    }
    return signals.map { (name, value) ->
        Signal(name, value)
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
 * @param modifiersBuilder configuration lambda for signal modifiers
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>, R> Element<E, P>.dataSignal(
    name: String,
    value: R?,
    modifiersBuilder: ModBuilder<DataSignalModifiers>.() -> Unit,
): Signal<R?> {
    val mods = ModBuilder(::DataSignalModifiers).apply(modifiersBuilder).mods
    return dataSignal(name, value, mods)
}

/**
 * Creates a data signal without an initial value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param modifiersBuilder configuration lambda for signal modifiers
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataSignal(
    name: String,
    modifiersBuilder: ModBuilder<DataSignalModifiers>.() -> Unit,
): Signal<Any?> {
    val mods = ModBuilder(::DataSignalModifiers).apply(modifiersBuilder).mods
    return dataSignal(name, null, mods)
}

fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOn(
    event: String,
    expr: DataStarExpression,
    modifiersBuilder: ModBuilder<DataOnModifiers>.() -> Unit,
) {
    /**
     *
     * Attaches an event handler to this element with modifiers.
     *
     * @param E type of the Element receiver
     * @param P type of the parent Element of the receiver
     * @receiver the Element to which the data-on attribute will be added
     * @param event the event to handle
     * @param expr DataStarExpression that computes the value of the signal
     * @param modifiersBuilder configuration lambda for event modifiers
     */
    val mods = ModBuilder(::DataOnModifiers).apply(modifiersBuilder).mods
    return dataOn(event, expr.expression, mods)
}

/**
 *
 * Runs an expression once when an element is initialized, with modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-init attribute will be added
 * @param expr DataStarExpression that computes the value of the signal
 * @param modifiersBuilder configuration lambda for initialization modifiers
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataInit(
    expr: DataStarExpression,
    modifiersBuilder: ModBuilder<DataInitModifiers>.() -> Unit,
) {
    val mods = ModBuilder(::DataInitModifiers).apply(modifiersBuilder).mods
    this.visitor.visitAttribute("data-init$mods", expr.expression)
}

/**
 *
 * Creates a computed signal whose value is derived from an expression.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-computed attribute will be added
 * @param name the name of the signal
 * @param js a JavaScript expression that computes the value of the signal
 * @param modifiers optional modifiers for the computed signal attribute
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataComputed(
    name: String,
    js: String,
    modifiers: String = "",
): Signal<Any> {
    this.visitor.visitAttribute("data-computed-$name$modifiers", js)
    val caseStyle = extractCaseStyle(modifiers)
    return Signal(name, js, caseStyle)
}

/**
 *
 * Creates a computed signal whose value is derived from an expression, with modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-computed attribute will be added
 * @param name the name of the signal
 * @param expr DataStarExpression that computes the value of the signal
 * @param modifiersBuilder configuration lambda for computed signal modifiers (delegates to the String overload)
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataComputed(
    name: String,
    expr: DataStarExpression,
    modifiersBuilder: ModBuilder<DataComputedModifiers>.() -> Unit,
): Signal<Any> {
    val mods = ModBuilder(::DataComputedModifiers).apply(modifiersBuilder).mods
    return dataComputed(name, expr.expression, mods)
}

/**
 *
 * Attaches an event handler to an element.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on attribute will be added
 * @param event the event to handle
 * @param js a JavaScript expression that computes the value of the signal
 * @param modifiers optional modifiers for the event handler
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOn(
    event: String,
    js: String,
    modifiers: String = "",
) {
    this.visitor.visitAttribute("data-on:$event$modifiers", js)
}

/**
 *
 * Initializes multiple signals from a JSON object, with modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-json-signals attribute will be added
 * @param jsObj a JavaScript object with include and/or exclude properties that are regular expressions, that filter which signals to watch.
 * @param modifiersBuilder configuration lambda for JSON signals modifiers
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataJsonSignals(
    jsObj: String = "",
    modifiersBuilder: ModBuilder<DataJsonSignalsModifiers>.() -> Unit,
) {
    val modifiers = ModBuilder(::DataJsonSignalsModifiers).apply(modifiersBuilder).mods
    this.visitor.visitAttribute("data-json-signals$modifiers", jsObj)
}

/**
 *
 * Adds or removes a class from an element based on an expression.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-class attribute will be added
 * @param className the name of the class from the element
 * @param predicate a JavaScript expression that if true adds the class to element otherwise removes it.
 * @param modifiersBuilder configuration lambda for class modifiers
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataClass(
    className: String,
    predicate: DataStarExpression,
    modifiersBuilder: ModBuilder<DataClassModifiers>.() -> Unit,
) {
    val mods = ModBuilder(::DataClassModifiers).apply(modifiersBuilder).mods
    this.visitor.visitAttribute("data-class:$className$mods", predicate.expression)
}
