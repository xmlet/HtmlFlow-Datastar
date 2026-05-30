package org.xmlet.htmlflow.datastar.attributes

import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlflow.datastar.builders.EventExpressionBuilder
import org.xmlet.htmlflow.datastar.builders.ExpressionBuilder
import org.xmlet.htmlflow.datastar.builders.ExpressionModifierBuilder
import org.xmlet.htmlflow.datastar.builders.ModifierBuilder
import org.xmlet.htmlflow.datastar.events.Event
import org.xmlet.htmlflow.datastar.expressions.JavaScriptSerialization
import org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator
import kotlin.reflect.full.memberProperties

internal fun buildExpression(block: ExpressionBuilder.() -> Unit): String =
    ExpressionBuilder()
        .apply(block)
        .getExpression()

internal fun <M : ModifierAccumulator> buildModifiers(
    builderFactory: () -> M,
    block: ModifierBuilder<M>.() -> Unit,
): String =
    ModifierBuilder(builderFactory)
        .apply(block)
        .getModifiers()

internal fun <M : ModifierAccumulator> buildExpressionWithModifiers(
    builderFactory: () -> M,
    block: ExpressionModifierBuilder<M>.() -> Unit,
): AttributeExpression =
    ExpressionModifierBuilder(builderFactory)
        .apply(block)
        .let { AttributeExpression(it.getExpression(), it.getModifiers()) }

internal fun <EVT : Event> buildEventExpressionWithModifiers(
    event: EVT,
    block: EventExpressionBuilder<EVT>.() -> Unit,
): AttributeExpression =
    EventExpressionBuilder(event)
        .apply(block)
        .let { AttributeExpression(it.getExpression(), it.getModifiers()) }

internal fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.visitExpressionAttribute(
    name: String,
    block: ExpressionBuilder.() -> Unit,
) {
    visitor.visitAttribute(name, buildExpression(block))
}

internal fun <E : Element<*, *>, P : Element<*, *>, M : ModifierAccumulator> Element<E, P>.visitExpressionModifierAttribute(
    name: String,
    builderFactory: () -> M,
    block: ExpressionModifierBuilder<M>.() -> Unit,
) {
    val result = buildExpressionWithModifiers(builderFactory, block)
    visitor.visitAttribute("$name${result.modifiers}", result.expression)
}

internal data class AttributeExpression(
    val expression: String,
    val modifiers: String,
)

/**
 * Transforms a list of pairs containing the names and values of signals to create
 *  into a JSON representation to pass to the `data-signals` attribute.
 *
 * @receiver The list of pairs to transform
 * @return The String with the corresponding representation in JSON
 */

internal fun List<Pair<String, Any?>>.toJson(): String =
    JavaScriptSerialization.objectLiteral(
        this.map { (name, value) ->
            name to serializeValue(value)
        },
    )

/**
 * Serializes the value received for correct display of complex Signal domains.
 *
 * @param value The value to serialize
 * @param isTopLevel If true, returns the value as an empty String, otherwise with "null" String
 * @return The string representation of the value
 */

internal fun serializeValue(
    value: Any?,
    isTopLevel: Boolean = true,
): String =
    when (value) {
        is String -> {
            JavaScriptSerialization.stringLiteral(value)
        }

        is Function0<*> -> {
            value().toString()
        }

        null -> {
            if (isTopLevel) "\"\"" else "null"
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
                    JavaScriptSerialization.objectLiteral(
                        properties.map { prop ->
                            val propValue = runCatching { prop.getter.call(value) }.getOrNull()
                            prop.name to serializeValue(propValue, isTopLevel = false)
                        },
                    )
                }
            }
        }
    }

/**
 * Serializes a computed signal into a JavaScript object containing a lambda expression associated with the given signal name.
 *
 * @param name the name of the signal
 * @param expression The expression evaluated by the lambda.
 * @return A JavaScript object representation for the `data-computed` attribute.
 */
internal fun serializeComputed(
    name: String,
    expression: String,
): String = JavaScriptSerialization.objectLiteral(listOf(name to "() => $expression"))
