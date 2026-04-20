package org.xmlet.htmlflow.datastar.expressions

/**
 * [RegexSignalPatchFilter] is a concrete implementation of [SignalPatchFilter] that uses regular expressions
 * to define inclusion and exclusion criteria for signal patching.
 */
class RegexSignalPatchFilter(
    private val include: Regex? = null,
    private val exclude: Regex? = null,
) : SignalPatchFilter {
    override fun render(): String {
        val parts = mutableListOf<String>()

        include?.let {
            parts += "include: /${it.pattern}/"
        }

        exclude?.let {
            parts += "exclude: /${it.pattern}/"
        }

        return "{${parts.joinToString(", ")}}"
    }
}
