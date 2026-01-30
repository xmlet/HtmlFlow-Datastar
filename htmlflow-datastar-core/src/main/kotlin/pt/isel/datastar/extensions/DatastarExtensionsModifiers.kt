package pt.isel.datastar.extensions

import org.xmlet.htmlapifaster.Element
import pt.isel.datastar.Signal
import pt.isel.datastar.modifiers.attributes.DataComputedModifiers
import pt.isel.datastar.modifiers.attributes.DataInitModifiers
import pt.isel.datastar.modifiers.attributes.DataJsonSignalsModifiers
import pt.isel.datastar.modifiers.attributes.DataOnModifiers
import pt.isel.datastar.modifiers.attributes.DataSignalModifiers

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @param R type of the value of the signal
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param value the value of the signal
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
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param signals pairs of signal names and their corresponding values
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
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @param R type of the value of the signal
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param value the value of the signal
 * @param modifiers that apply to the signal
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
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataSignal(
    name: String,
    modifiers: DataSignalModifiers.() -> Unit,
): Signal = dataSignal(name, null, modifiers)

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param event the event to handle
 * @param js a JavaScript expression that computes the value of the signal
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
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-init attribute will be added
 * @param js a JavaScript expression that computes the value of the signal
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
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param js a JavaScript expression that computes the value of the signal
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
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param js a JavaScript expression that computes the value of the signal
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
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param event the event to handle
 * @param js a JavaScript expression that computes the value of the signal
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOn(
    event: String,
    js: String,
    modifiers: String = "",
) {
    this.visitor.visitAttribute("data-on:$event$modifiers", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param jsObj a JavaScript object with include and/or exclude properties that are regular expressions, that filter which signals to watch.
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
