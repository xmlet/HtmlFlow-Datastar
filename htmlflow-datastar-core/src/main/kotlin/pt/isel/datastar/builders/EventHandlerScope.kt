package pt.isel.datastar.builders

import pt.isel.datastar.events.Event
import pt.isel.datastar.expressions.DataStarExpression
import pt.isel.datastar.modifiers.attributes.DataOnModifiers

class EventHandlerScope<EVT : Event>(
    val evt: EVT,
) : ModBuilder<DataOnModifiers>(::DataOnModifiers) {
    private var handlerExpression = ""

    fun expr(expr: DataStarExpression) {
        handlerExpression + expr
    }

    fun expr(js: String) {
        handlerExpression + js
    }

    operator fun DataStarExpression.unaryPlus() {
        handlerExpression += this
    }

    operator fun String.unaryPlus() {
        handlerExpression += this
    }

    val expr: String get() = handlerExpression
}
