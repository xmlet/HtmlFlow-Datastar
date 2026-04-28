package org.xmlet.htmlflow.datastar.attributes

import org.xmlet.htmlflow.datastar.expressions.SignalPatchFilter
import kotlin.reflect.full.memberProperties

/**
 * Renders a [SignalPatchFilter] into a string format suitable for use in a
 * `data-on-signal-patch-filter` and `data-json-signals´ attribute.
 *
 * The output format is:
 * - If only `include` is specified: `{include: /pattern/}`
 * - If only `exclude` is specified: `{exclude: /pattern/}`
 * - If both are specified: `{include: /includePattern/, exclude: /excludePattern/}`
 *
 * @receiver the SignalPatchFilter to render
 * @return a string representation of the filter for HTML attributes
 */

internal fun SignalPatchFilter.render(): String {
    val parts = mutableListOf<String>()
    include?.let { parts += "include: /${it.pattern}/" }
    exclude?.let { parts += "exclude: /${it.pattern}/" }
    return "{${parts.joinToString(", ")}}"
}

/**
 * Transforms a list of pairs containing the names and values of signals to create
 *  into a JSON representation to pass to the `data-signals` attribute.
 *
 * @receiver The list of pairs to transform
 * @return The String with the corresponding representation in JSON
 */

internal fun List<Pair<String, Any?>>.toJson(): String =
    this.joinToString(prefix = "{", postfix = "}", separator = ", ") { (name, value) ->
        val res = serializeValue(value)
        "$name: $res"
    }

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
