package pt.isel.datastar.extensions

import org.xmlet.htmlapifaster.Element
import pt.isel.datastar.Signal
import pt.isel.datastar.builders.EventExpressionBuilder
import pt.isel.datastar.builders.ExpressionModifierBuilder
import pt.isel.datastar.builders.ModifierBuilder
import pt.isel.datastar.events.Event
import pt.isel.datastar.modifiers.attributes.DataClassModifiers
import pt.isel.datastar.modifiers.attributes.DataComputedModifiers
import pt.isel.datastar.modifiers.attributes.DataInitModifiers
import pt.isel.datastar.modifiers.attributes.DataJsonSignalsModifiers
import pt.isel.datastar.modifiers.attributes.DataSignalModifiers
import pt.isel.datastar.modifiers.attributes.DataSignalsModifiers
import pt.isel.datastar.modifiers.extractCaseStyle

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
    modifiersBuilder: ModifierBuilder<DataSignalsModifiers>.() -> Unit = {},
): List<Signal<Any?>> {
    signals.toList().toJson().also {
        val mods = ModifierBuilder(::DataSignalsModifiers).apply(modifiersBuilder).getModifiers()
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
    modifiersBuilder: ModifierBuilder<DataSignalModifiers>.() -> Unit = {},
): Signal<R?> {
    val mods = ModifierBuilder(::DataSignalModifiers).apply(modifiersBuilder).getModifiers()
    val res =
        when (value) {
            is String -> "'$value'"
            null -> ""
            else -> "$value"
        }

    this.visitor.visitAttribute("data-signals:$name$mods", res)

    val caseStyle = extractCaseStyle(mods)

    return Signal(name, value, caseStyle)
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
    modifiersBuilder: ModifierBuilder<DataSignalModifiers>.() -> Unit = {},
): Signal<Any?> = dataSignal(name, null, modifiersBuilder)

fun <E : Element<*, *>, P : Element<*, *>, EVT : Event> Element<E, P>.dataOn(
    event: EVT,
    block: EventExpressionBuilder<EVT>.() -> Unit,
) {
    val builder = EventExpressionBuilder(event)

    builder.block()
    val expr = builder.getExpression()
    val mods = builder.getModifiers()

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
    val expressionModifiers = ExpressionModifierBuilder(::DataInitModifiers).apply(block)
    val expression = expressionModifiers.getExpression()
    val modifiers = expressionModifiers.getModifiers()
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
    val builder = ExpressionModifierBuilder(::DataComputedModifiers).apply(block)
    val expression = builder.getExpression()
    val modifiers = builder.getModifiers()

    this.visitor.visitAttribute("data-computed-$name$modifiers", expression)
    val caseStyle = extractCaseStyle(modifiers)
    return Signal(name, expression, caseStyle)
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
    modifiersBuilder: ModifierBuilder<DataJsonSignalsModifiers>.() -> Unit,
) {
    val modifiers = ModifierBuilder(::DataJsonSignalsModifiers).apply(modifiersBuilder).getModifiers()
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
 * @param block configuration lambda for initialization modifiers and create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataClass(
    className: String,
    block: ExpressionModifierBuilder<DataClassModifiers>.() -> Unit,
) {
    val builder = ExpressionModifierBuilder(::DataClassModifiers).apply(block)
    val expression = builder.getExpression()
    val modifiers = builder.getModifiers()

    this.visitor.visitAttribute("data-class:$className$modifiers", expression)
}
