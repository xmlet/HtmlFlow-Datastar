package pt.isel.datastar.builders

import pt.isel.datastar.events.Event
import pt.isel.datastar.expressions.DataStarExpression
import pt.isel.datastar.modifiers.attributes.DataOnModifiers

class EventHandlerScope<EVT : Event>(
    val evt: EVT,
) : ModBuilder<DataOnModifiers>(::DataOnModifiers) {
    private val handlerExpression = mutableListOf<DataStarExpression>()

    fun expr(expr: DataStarExpression) {
        handlerExpression.addLast(expr)
    }

    fun expr(js: String) {
        handlerExpression.addLast(DataStarExpression(js))
    }

    operator fun DataStarExpression.unaryPlus() {
        handlerExpression.addLast(this)
    }

    operator fun String.unaryPlus() {
        handlerExpression.addLast(DataStarExpression(this))
    }

    fun getExpressions() = handlerExpression.joinToString("; ")

    fun getModifiers() = mods
}
