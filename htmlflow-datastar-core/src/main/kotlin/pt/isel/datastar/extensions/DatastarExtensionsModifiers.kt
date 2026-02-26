package pt.isel.datastar.extensions

import org.xmlet.htmlapifaster.Element
import pt.isel.datastar.Signal
import pt.isel.datastar.builders.ModBuilder
import pt.isel.datastar.builders.ModCodeBuilder
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
): Signal {
    val res =
        when (value) {
            is String -> "'$value'"
            null -> ""
            else -> "$value"
        }

    this.visitor.visitAttribute("data-signals:$name$modifiers", res)

    val caseStyle = extractCaseStyle(modifiers)

    return Signal(name, caseStyle)
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
): List<Signal> {
    val mods = ModBuilder(::DataSignalsModifiers).apply(modifiersBuilder).mods
    signals.toList().toJson().also {
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
 * @param modifiersBuilder configuration lambda for signal modifiers
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>, R> Element<E, P>.dataSignal(
    name: String,
    value: R?,
    modifiersBuilder: ModBuilder<DataSignalModifiers>.() -> Unit,
): Signal {
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
): Signal {
    val mods = ModBuilder(::DataSignalModifiers).apply(modifiersBuilder).mods
    return dataSignal(name, null, mods)
}

/**
 *
 * Runs an expression once when an element is initialized, with optional modifiers and script lambdas.
 *
 * @param modCodeBuilder Builder lambda where you can call code { ... } for JS code and mod { ... } for modifiers.
 * Example:
 *   dataInit {
 *     code { "console.log('init')" }
 *     mod { delay(100) }
 *   }
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataInit(modCodeBuilder: ModCodeBuilder<DataInitModifiers>.() -> Unit) {
    val builder = ModCodeBuilder(::DataInitModifiers).apply(modCodeBuilder)
    val js = builder.script
    val mods = builder.mods
    this.visitor.visitAttribute("data-init$mods", js)
}

/**
 *
 * Creates a computed signal whose value is derived from an expression, with optional modifiers and script lambdas.
 *
 * @param name The name of the signal.
 * @param modCodeBuilder Builder lambda where you can call code { ... } for JS code and mod { ... } for modifiers.
 * Example:
 *   dataComputed("foo") {
 *     code { "return bar + 1;" }
 *     mod { case(CaseStyle.PASCAL) }
 *   }
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataComputed(
    name: String,
    modCodeBuilder: ModCodeBuilder<DataComputedModifiers>.() -> Unit,
): Signal {
    val builder = ModCodeBuilder(::DataComputedModifiers).apply(modCodeBuilder)
    val js = builder.script
    val mods = builder.mods
    this.visitor.visitAttribute("data-computed-$name$mods", js)
    val caseStyle = extractCaseStyle(mods)
    return Signal(name, caseStyle)
}

/**
 *
 * Attaches an event handler to this element with optional modifiers and script lambdas.
 *
 * @param event The event to handle.
 * @param modCodeBuilder Builder lambda where you can call code { ... } for JS code and mod { ... } for modifiers.
 * Example:
 *   dataOn("click") {
 *     code { "alert('clicked')" }
 *     mod { debounce(200) }
 *   }
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOn(
    event: String,
    modCodeBuilder: ModCodeBuilder<DataOnModifiers>.() -> Unit,
) {
    val builder = ModCodeBuilder(::DataOnModifiers).apply(modCodeBuilder)
    val js = builder.script
    val mods = builder.mods
    this.visitor.visitAttribute("data-on:$event$mods", js)
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
 * @param modCodeBuilder Builder lambda where you can call code { ... } for JS code and mod { ... } for modifiers.
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataClass(
    className: String,
    modCodeBuilder: ModCodeBuilder<DataClassModifiers>.() -> Unit,
) {
    val builder = ModCodeBuilder(::DataClassModifiers).apply(modCodeBuilder)
    val predicate = builder.script
    val mods = builder.mods
    this.visitor.visitAttribute("data-class:$className$mods", predicate)
}
