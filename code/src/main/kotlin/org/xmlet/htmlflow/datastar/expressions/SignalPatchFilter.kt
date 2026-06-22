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
        val parts = mutableListOf<Pair<String, String>>()
        include?.let { parts.add("include" to JavaScriptSerialization.regexLiteral(it)) }
        exclude?.let { parts.add("exclude" to JavaScriptSerialization.regexLiteral(it)) }
        return JavaScriptSerialization.objectLiteral(parts)
    }
}
