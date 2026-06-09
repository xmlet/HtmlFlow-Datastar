package org.xmlet.kotlinx.html.datastar

import kotlinx.html.Tag
import org.xmlet.htmlflow.datastar.attributes.serializeValue
import org.xmlet.htmlflow.datastar.builders.EventExpressionBuilder
import org.xmlet.htmlflow.datastar.events.Event
import org.xmlet.htmlflow.datastar.expressions.Signal

fun Tag.dataSignal(
    name: String,
    value: Any?,
): Signal<Any?> {
    attributes["data-signals:$name"] = serializeValue(value)
    return Signal(name)
}

fun <EVT : Event> Tag.dataOn(
    event: EVT,
    block: EventExpressionBuilder<EVT>.() -> Unit,
) {
    val builder = EventExpressionBuilder(event)

    builder.block()
    val expr = builder.getExpression()
    val mods = builder.getModifiers()

    attributes["data-on:$event$mods"] = expr
}

fun Tag.dataBind(name: String) {
    attributes["data-bind"] = name
}
