package org.xmlet.htmlflow.datastar.serialization

/**
 * Marks JavaScript source that must not be quoted, escaped, or interpreted as a Kotlin string value.
 *
 * The expression DSL preserves [js] verbatim, both as a standalone statement and as an operand.
 * Callers are responsible for including any parentheses needed to group their raw JavaScript.
 * Use valid JavaScript expressions as operands; statement sequences belong in standalone output.
 * @property js The Raw JavaScript string
 */
@JvmInline
value class RawJS(
    val js: String = "",
)

/**
 * Marks this string as intentionally raw JavaScript rather than a string literal.
 * @receiver The String containing the raw JavaScript
 */
fun String.asRawJS(): RawJS = RawJS(this)
