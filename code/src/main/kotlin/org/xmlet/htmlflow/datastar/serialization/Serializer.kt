package org.xmlet.htmlflow.datastar.serialization

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.full.createType

/**
 * Object responsible for serializing values, computed signal and signals.
 */
internal object Serializer {
    private val json =
        Json {
            encodeDefaults = true
            explicitNulls = true
        }

    internal val metadataCache = mutableMapOf<SerialDescriptor, IndexedFormatMetadata>()

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
    ): String = JavaScriptLiterals.objectLiteral(listOf(name to "() => $expression"))

    /**
     * Transforms a list of pairs containing the names and values of signals to create
     *  into a JSON representation to pass to the `data-signals` attribute.
     *
     * @receiver The list of pairs to transform
     * @param quote Flag to enable or disable quotation on a string
     * @return The String with the corresponding representation in JSON
     */
    internal fun List<Pair<String, Any?>>.serializeSignals(quote: Boolean = true): String =
        JavaScriptLiterals.objectLiteral(
            this.map { (name, value) ->
                name to serializeValue(value, quote)
            },
        )

    /**
     * Serializes the value received for correct display of complex Signal domains.
     *
     * @param value The value to serialize
     * @param quote Flag to enable or disable quotation on a string
     * @return The string representation of the value
     */
    internal fun serializeValue(
        value: Any?,
        quote: Boolean = true,
    ): String =
        when (value) {
            null -> {
                "null"
            }

            is String -> {
                if (!quote) value else JavaScriptLiterals.stringLiteral(value)
            }

            is Number, is Boolean -> {
                "$value"
            }

            is Map<*, *> -> {
                value.entries.joinToString(prefix = "{", postfix = "}", separator = ", ") { (key, value) ->
                    "${JavaScriptLiterals.objectKey(key.toString())}: ${serializeValue(value, quote)}"
                }
            }

            is Collection<*> -> {
                formatBareCollection(value, quote)
            }

            is Array<*> -> {
                formatBareCollection(value.toList(), quote)
            }

            else -> {
                serializeObject(value)
            }
        }

    /**
     * Serializes a serializable object into a JavaScript literal using kotlinx.serialization.
     *
     * @param value The object to serialize.
     * @return The JavaScript literal representation of the object.
     */
    private fun serializeObject(value: Any): String {
        val valueType = value::class.createType()
        val serializer =
            json.serializersModule.serializerOrNull(valueType) ?: return value.toString()
        val element = json.encodeToJsonElement(serializer, value)
        val metadata = serializer.descriptor.buildIndexedMetadata(value::class, null)

        return formatElement(
            element = element,
            descriptor = serializer.descriptor,
            metadata = metadata,
        )
    }

    /**
     * Formats a collection that has no associated KType as a JavaScript array literal.
     *
     * @param collection The collection to format.
     * @param quote Whether string values should be quoted.
     * @return The JavaScript array literal representation.
     */
    private fun formatBareCollection(
        collection: Collection<*>,
        quote: Boolean = true,
    ): String =
        collection.joinToString(prefix = "[", postfix = "]", separator = ", ") { item ->
            serializeValue(item, quote)
        }
}
