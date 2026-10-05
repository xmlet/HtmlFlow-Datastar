package org.xmlet.htmlflow.datastar.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import org.xmlet.htmlflow.datastar.serialization.Serializer.metadataCache
import kotlin.collections.set
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

/**
 * Pre-computed formatting metadata for a serializable type.
 *
 * @property properties Formatting metadata for each class property, indexed by descriptor position.
 * @property listElement Metadata for collection or array elements.
 * @property mapValue Metadata for map values.
 */
internal data class IndexedFormatMetadata(
    val properties: List<IndexedPropertyMetadata> = emptyList(),
    val listElement: IndexedFormatMetadata? = null,
    val mapValue: IndexedFormatMetadata? = null,
)

/**
 * Pre-computed formatting metadata for a single class property.
 *
 * @property serialName The name kotlinx.serialization emits for this property.
 * @property quote Whether string values should be quoted (false when annotated with [NoQuote]).
 * @property metadata Nested formatting metadata for the property's type.
 */
internal data class IndexedPropertyMetadata(
    val serialName: String,
    val quote: Boolean,
    val metadata: IndexedFormatMetadata?,
)

/**
 * Builds and caches formatting metadata for a serializable type.
 *
 * @param ownerClass The Kotlin class that owns this descriptor, when known.
 * @param ownerType The Kotlin type that describes this descriptor, when known.
 * @param visited Set of descriptors currently being processed, used for cycle detection.
 * @return The pre-computed formatting metadata for this descriptor.
 */
internal fun SerialDescriptor.buildIndexedMetadata(
    ownerClass: KClass<*>?,
    ownerType: KType?,
    visited: MutableSet<SerialDescriptor> = mutableSetOf(),
): IndexedFormatMetadata =
    metadataCache[this]
        ?: run {
            if (!visited.add(this)) {
                return IndexedFormatMetadata()
            }

            try {
                val result =
                    when (kind) {
                        is StructureKind.CLASS -> buildClassMetadata(ownerClass, ownerType, visited)
                        is StructureKind.LIST -> buildListMetadata(ownerType, visited)
                        is StructureKind.MAP -> buildMapMetadata(ownerType, visited)
                        else -> IndexedFormatMetadata()
                    }
                metadataCache[this] = result
                result
            } finally {
                visited.remove(this)
            }
        }

/**
 * Builds formatting metadata for a class descriptor by resolving property annotations.
 *
 * @param ownerClass The Kotlin class that owns this descriptor, when known.
 * @param ownerType The Kotlin type that describes this descriptor, when known.
 * @param visited Set of descriptors currently being processed, used for cycle detection.
 * @return The pre-computed formatting metadata for the class.
 */
private fun SerialDescriptor.buildClassMetadata(
    ownerClass: KClass<*>?,
    ownerType: KType?,
    visited: MutableSet<SerialDescriptor>,
): IndexedFormatMetadata {
    val resolvedClass = ownerClass ?: ownerType?.classifier as? KClass<*>
    val properties =
        (0 until elementsCount).map { index ->
            val name = getElementName(index)
            val childDescriptor = getElementDescriptor(index)
            val property = resolvedClass?.propertyForSerializedName(name)
            val childReturnType = property?.returnType

            val hasNoQuote = property?.findAnnotation<NoQuote>() != null
            val isStringProperty = property?.returnType?.classifier == String::class

            IndexedPropertyMetadata(
                serialName = name,
                quote = !(hasNoQuote && isStringProperty),
                metadata =
                    childDescriptor.buildIndexedMetadata(
                        childReturnType?.classifier as? KClass<*>,
                        childReturnType,
                        visited,
                    ),
            )
        }
    return IndexedFormatMetadata(properties = properties)
}

/**
 * Builds formatting metadata for a list or collection descriptor.
 *
 * @param ownerType The Kotlin type that describes this descriptor, when known.
 * @param visited Set of descriptors currently being processed, used for cycle detection.
 * @return The pre-computed formatting metadata for the list element type.
 */
private fun SerialDescriptor.buildListMetadata(
    ownerType: KType?,
    visited: MutableSet<SerialDescriptor>,
): IndexedFormatMetadata {
    val elementDescriptor = listElementDescriptor()
    val elementType = ownerType?.collectionElementType()
    return IndexedFormatMetadata(
        listElement =
            elementDescriptor?.buildIndexedMetadata(
                elementType?.classifier as? KClass<*>,
                elementType,
                visited,
            ),
    )
}

/**
 * Builds formatting metadata for a map descriptor.
 *
 * @param ownerType The Kotlin type that describes this descriptor, when known.
 * @param visited Set of descriptors currently being processed, used for cycle detection.
 * @return The pre-computed formatting metadata for the map value type.
 */
private fun SerialDescriptor.buildMapMetadata(
    ownerType: KType?,
    visited: MutableSet<SerialDescriptor>,
): IndexedFormatMetadata {
    val valueDescriptor = mapValueDescriptor()
    val valueType = ownerType?.mapValueType()
    return IndexedFormatMetadata(
        mapValue =
            valueDescriptor?.buildIndexedMetadata(
                valueType?.classifier as? KClass<*>,
                valueType,
                visited,
            ),
    )
}

/**
 * Finds a Kotlin property by the name that kotlinx.serialization emitted in JSON.
 *
 * @param serializedName The serialized property name to match.
 * @return The matching property, or null when not found.
 */
private fun KClass<*>.propertyForSerializedName(serializedName: String): KProperty1<out Any, *>? =
    memberProperties.firstOrNull { property ->
        property.findAnnotation<SerialName>()?.value == serializedName || property.name == serializedName
    }

/**
 * Determines the element type of a collection or array KType.
 *
 * @return The element type, or null when the type is not a collection or array.
 */
private fun KType.collectionElementType(): KType? {
    val kClass = classifier as? KClass<*> ?: return null
    if (!kClass.isSubclassOf(Collection::class) && !kClass.java.isArray) return null
    return arguments.firstOrNull()?.type
}

/**
 * Determines the value type of a map KType.
 *
 * @return The value type, or null when the type is not a map.
 */
private fun KType.mapValueType(): KType? {
    val kClass = classifier as? KClass<*> ?: return null
    if (!kClass.isSubclassOf(Map::class)) return null
    return arguments.getOrNull(1)?.type
}
