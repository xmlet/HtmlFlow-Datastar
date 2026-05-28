package org.xmlet.htmlflow.datastar.expressions

/**
 * Represents a filter object for signal patching.
 *
 * You can filter which signals to watch by including or excluding patterns.
 *
 * Contains two optional regular expression properties:
 *  - [include] — signals matching this pattern are included
 *  - [exclude] — signals matching this pattern are excluded
 */
class SignalPatchFilter {
    var include: Regex? = null
    var exclude: Regex? = null

    override fun toString(): String {
        val parts = mutableListOf<String>()
        include?.let { parts.add("include: /${it.pattern}/") }
        exclude?.let { parts.add("exclude: /${it.pattern}/") }
        return parts.joinToString(prefix = "{", postfix = "}")
    }
}
