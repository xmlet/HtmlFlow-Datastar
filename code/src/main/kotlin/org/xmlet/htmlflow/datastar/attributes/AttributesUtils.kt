package org.xmlet.htmlflow.datastar.attributes

import org.xmlet.htmlflow.datastar.expressions.SignalPatchFilter

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
