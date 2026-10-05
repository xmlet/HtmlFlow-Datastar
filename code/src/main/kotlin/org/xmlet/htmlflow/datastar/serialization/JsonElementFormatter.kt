package org.xmlet.htmlflow.datastar.serialization

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Formats a JSON element as a JavaScript literal.
 *
 * @param element The JSON element to format.
 * @param descriptor The serializer descriptor for the current element.
 * @param metadata Pre-computed formatting metadata for the element's type.
 * @param quote Whether string primitives should be quoted.
 * @return The JavaScript literal representation of the element.
 */
internal fun formatElement(
    element: JsonElement,
    descriptor: SerialDescriptor?,
    metadata: IndexedFormatMetadata? = null,
    quote: Boolean = true,
): String =
    when (element) {
        JsonNull -> {
            "null"
        }

        is JsonPrimitive -> {
            val content = element.content
            return if (element.isString) {
                if (!quote) content else JavaScriptLiterals.stringLiteral(content)
            } else {
                content
            }
        }

        is JsonArray -> {
            val childDescriptor = descriptor?.listElementDescriptor()
            val childMetadata = metadata?.listElement
            element.joinToString(prefix = "[", postfix = "]", separator = ", ") { childElement ->
                formatElement(childElement, childDescriptor, childMetadata)
            }
        }

        is JsonObject -> {
            if (descriptor?.kind is StructureKind.MAP) {
                formatMapObject(element, descriptor, metadata)
            } else {
                formatClassObject(element, descriptor, metadata)
            }
        }
    }

/**
 * Formats a JSON object produced from a serializable class as a JavaScript object literal.
 *
 * @param element The JSON object to format.
 * @param descriptor The serializer descriptor for the object's class.
 * @param metadata Pre-computed formatting metadata for the object's type.
 * @return The JavaScript object literal representation.
 */
private fun formatClassObject(
    element: JsonObject,
    descriptor: SerialDescriptor?,
    metadata: IndexedFormatMetadata?,
): String =
    element.entries.joinToString(prefix = "{", postfix = "}", separator = ", ") { (key, childElement) ->
        val index = descriptor?.getElementIndex(key) ?: CompositeDecoder.UNKNOWN_NAME
        val propertyMetadata =
            if (index != CompositeDecoder.UNKNOWN_NAME) {
                metadata?.properties?.getOrNull(index)
            } else {
                null
            }

        val childDescriptor = safeChildDescriptor(descriptor, index)

        val formattedValue =
            formatElement(
                element = childElement,
                descriptor = childDescriptor,
                metadata = propertyMetadata?.metadata,
                quote = propertyMetadata?.quote == true,
            )

        "${JavaScriptLiterals.objectKey(key)}: $formattedValue"
    }

/**
 * Formats a JSON object produced from a map as a JavaScript object literal.
 *
 * @param element The JSON object to format.
 * @param descriptor The serializer descriptor for the map.
 * @param metadata Pre-computed formatting metadata for the map's value type.
 * @return The JavaScript object literal representation.
 */
private fun formatMapObject(
    element: JsonObject,
    descriptor: SerialDescriptor?,
    metadata: IndexedFormatMetadata?,
): String {
    val valueDescriptor = descriptor?.mapValueDescriptor()
    val valueMetadata = metadata?.mapValue

    return element.entries.joinToString(prefix = "{", postfix = "}", separator = ", ") { (key, childElement) ->
        val formattedValue =
            formatElement(childElement, valueDescriptor, valueMetadata)

        "${JavaScriptLiterals.objectKey(key)}: $formattedValue"
    }
}
