package org.xmlet.htmlflow.datastar.expressions

/**
 * Represents a filter for signal patching, allowing specification
 * of inclusion and exclusion criteria based on regular expressions.
 */
interface SignalPatchFilter {
    val include: Regex?
    val exclude: Regex?
}
