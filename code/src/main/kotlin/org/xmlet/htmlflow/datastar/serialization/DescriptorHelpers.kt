package org.xmlet.htmlflow.datastar.serialization

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder

/**
 * Returns the descriptor for a child element at the given index, or null when invalid.
 *
 * @param descriptor The parent descriptor.
 * @param index The child element index.
 * @return The child descriptor, or null when the index is out of bounds or unknown.
 */
internal fun safeChildDescriptor(
    descriptor: SerialDescriptor?,
    index: Int,
): SerialDescriptor? {
    if (descriptor == null || index == CompositeDecoder.UNKNOWN_NAME) return null
    if (index !in 0 until descriptor.elementsCount) return null
    return descriptor.getElementDescriptor(index)
}

/**
 * Returns the descriptor for elements of a list or collection.
 *
 * @return The element descriptor, or null when the descriptor is not a list or has no elements.
 */
internal fun SerialDescriptor.listElementDescriptor(): SerialDescriptor? =
    if (kind is StructureKind.LIST && elementsCount > 0) {
        getElementDescriptor(0)
    } else {
        null
    }

/**
 * Returns the descriptor for values of a map.
 *
 * @return The value descriptor, or null when the descriptor is not a map or has fewer than two elements.
 */
internal fun SerialDescriptor.mapValueDescriptor(): SerialDescriptor? =
    if (kind is StructureKind.MAP && elementsCount > 1) {
        getElementDescriptor(1)
    } else {
        null
    }
