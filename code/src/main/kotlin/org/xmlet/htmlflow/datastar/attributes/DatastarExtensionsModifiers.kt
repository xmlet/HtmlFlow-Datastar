package org.xmlet.htmlflow.datastar.attributes

import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlflow.datastar.Signal
import org.xmlet.htmlflow.datastar.builders.DefaultModifierBuilder
import org.xmlet.htmlflow.datastar.builders.EventExpressionBuilder
import org.xmlet.htmlflow.datastar.builders.ExpressionModifierBuilder
import org.xmlet.htmlflow.datastar.events.Event
import org.xmlet.htmlflow.datastar.modifiers.CaseStyle
import org.xmlet.htmlflow.datastar.modifiers.CaseStyle.Companion.extractCaseStyle
import org.xmlet.htmlflow.datastar.modifiers.attributes.DataClassModifiers
import org.xmlet.htmlflow.datastar.modifiers.attributes.DataComputedModifiers
import org.xmlet.htmlflow.datastar.modifiers.attributes.DataInitModifiers
import org.xmlet.htmlflow.datastar.modifiers.attributes.DataJsonSignalsModifiers
import org.xmlet.htmlflow.datastar.modifiers.attributes.DataSignalModifiers
import org.xmlet.htmlflow.datastar.modifiers.attributes.DataSignalsModifiers
import kotlin.reflect.full.memberProperties

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
fun <E : Element<*, *>, P : Element<*, *>, T> Element<E, P>.dataSignals(
    vararg signals: Pair<String, T?>,
    modifiersBuilder: DefaultModifierBuilder<DataSignalsModifiers>.() -> Unit = {},
): List<Signal<T>> {
    signals.toList().toJson().also {
        val mods = DefaultModifierBuilder(::DataSignalsModifiers).apply(modifiersBuilder).getModifiers()
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
 * @param modifiersBuilder configuration lambda for signal modifiers
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>, R> Element<E, P>.dataSignal(
    name: String,
    value: R,
    modifiersBuilder: DefaultModifierBuilder<DataSignalModifiers>.() -> Unit = {},
): Signal<R> {
    val mods = DefaultModifierBuilder(::DataSignalModifiers).apply(modifiersBuilder).getModifiers()
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
 * @param modifiersBuilder configuration lambda for signal modifiers
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataSignal(
    name: String,
    modifiersBuilder: DefaultModifierBuilder<DataSignalModifiers>.() -> Unit = {},
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
 * @param modifiersBuilder configuration lambda for JSON signals modifiers
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataJsonSignals(
    jsObj: String = "",
    modifiersBuilder: DefaultModifierBuilder<DataJsonSignalsModifiers>.() -> Unit,
) {
    val modifiers = DefaultModifierBuilder(::DataJsonSignalsModifiers).apply(modifiersBuilder).getModifiers()
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

// Helper Functions
private fun List<Pair<String, Any?>>.toJson(): String =
    this.joinToString(prefix = "{", postfix = "}", separator = ", ") { (name, value) ->
        val res = serializeValue(value)
        "$name: $res"
    }

private fun serializeValue(
    value: Any?,
    isTopLevel: Boolean = true,
): String =
    when (value) {
        is String -> {
            "'${value.replace("'", "\\'")}'"
        }

        is Function0<*> -> {
            value().toString()
        }

        null -> {
            if (isTopLevel) "" else "null"
        }

        is Number, is Boolean -> {
            "$value"
        }

        else -> {
            if (!value::class.isData) {
                "$value"
            } else {
                val properties = value::class.memberProperties
                if (properties.isEmpty()) {
                    "$value"
                } else {
                    properties.joinToString(separator = ", ", prefix = "{", postfix = "}") { prop ->
                        val propValue = runCatching { prop.getter.call(value) }.getOrNull()
                        "${prop.name}: ${serializeValue(propValue, isTopLevel = false)}"
                    }
                }
            }
        }
    }
