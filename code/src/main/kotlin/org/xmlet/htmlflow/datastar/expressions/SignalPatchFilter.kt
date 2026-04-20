package org.xmlet.htmlflow.datastar.expressions

/**
 * Represents a filter that can be applied to a signal patch in DataStar expressions.
 */
interface SignalPatchFilter {
    fun render(): String
}
