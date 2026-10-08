package org.xmlet.htmlflow.datastar.serialization

@JvmInline
value class RawJS(
    val js: String = "",
)

fun String.asRawJS(): RawJS = RawJS(this)
