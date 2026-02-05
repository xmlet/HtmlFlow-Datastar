package pt.isel.datastar.extensions

import org.xmlet.htmlapifaster.Element
import pt.isel.datastar.Signal
import pt.isel.datastar.modifiers.attributes.DataComputedModifiers
import pt.isel.datastar.modifiers.attributes.DataInitModifiers
import pt.isel.datastar.modifiers.attributes.DataJsonSignalsModifiers
import pt.isel.datastar.modifiers.attributes.DataOnModifiers
import pt.isel.datastar.modifiers.attributes.DataSignalModifiers

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
): Signal {
    val res =
        when (value) {
            is String -> "'$value'"
            null -> ""
            else -> "$value"
        }

    this.visitor.visitAttribute("data-signals-$name$modifiers", res)
    return Signal(name)
}

/**
 *
 * Initializes one or more signals with their initial values and modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signals attribute will be added
 * @param signals pairs of signal names and their corresponding values
 * @param modifiers configuration lambda for signal modifiers
 * @return a list of Signal instances with the given names
 */
fun <E : Element<*, *>, P : Element<*, *>, Any> Element<E, P>.dataSignals(
    vararg signals: Pair<String, Any?>,
    modifiers: DataSignalModifiers.() -> Unit,
): List<Signal> {
    signals.toList().toJson().also {
        val mods =
            DataSignalModifiers()
                .apply(modifiers)
                .toString()
        this.visitor.visitAttribute("data-signals$mods", it)
    }
    return signals.map { (name) ->
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
 * @param modifiers configuration lambda for signal modifiers (delegates to the String overload)
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>, R> Element<E, P>.dataSignal(
    name: String,
    value: R?,
    modifiers: DataSignalModifiers.() -> Unit,
): Signal {
    val mods =
        DataSignalModifiers()
            .apply(modifiers)
            .toString()
    return dataSignal(name, value, mods)
}

/**
 * Creates a data signal without an initial value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param modifiers configuration lambda for signal modifiers (delegates to the value overload with null)
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataSignal(
    name: String,
    modifiers: DataSignalModifiers.() -> Unit,
): Signal = dataSignal(name, null, modifiers)

/**
 *
 * Attaches an event handler to this element with modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on attribute will be added
 * @param event the event to handle
 * @param js a JavaScript expression that computes the value of the signal
 * @param modifiers configuration lambda for event modifiers (delegates to the String overload)
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOn(
    event: String,
    js: String,
    modifiers: DataOnModifiers.() -> Unit,
) {
    val mods =
        DataOnModifiers()
            .apply(modifiers)
            .toString()
    return dataOn(event, js, mods)
}

/**
 *
 * Runs an expression once when an element is initialized, with modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-init attribute will be added
 * @param js a JavaScript expression that computes the value of the signal
 * @param modifiers configuration lambda for initialization modifiers
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataInit(
    js: String,
    modifiers: DataInitModifiers.() -> Unit,
) {
    val mods =
        DataInitModifiers()
            .apply(modifiers)
    this.visitor.visitAttribute("data-init$mods", js)
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
): Signal {
    this.visitor.visitAttribute("data-computed-$name$modifiers", js)
    return Signal(name)
}

/**
 *
 * Creates a computed signal whose value is derived from an expression, with modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-computed attribute will be added
 * @param name the name of the signal
 * @param js a JavaScript expression that computes the value of the signal
 * @param modifiers configuration lambda for computed signal modifiers (delegates to the String overload)
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataComputed(
    name: String,
    js: String,
    modifiers: DataComputedModifiers.() -> Unit,
): Signal {
    val mods =
        DataComputedModifiers()
            .apply(modifiers)
            .toString()
    return dataComputed(name, js, mods)
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
 * @param mods configuration lambda for JSON signals modifiers
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataJsonSignals(
    jsObj: String = "",
    mods: DataJsonSignalsModifiers.() -> Unit,
) {
    val modifiers =
        DataJsonSignalsModifiers()
            .apply(mods)
            .toString()
    this.visitor.visitAttribute("data-json-signals$modifiers", jsObj)
}
