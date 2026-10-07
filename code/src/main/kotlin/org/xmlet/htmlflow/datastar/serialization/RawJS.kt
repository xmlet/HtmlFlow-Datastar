package org.xmlet.htmlflow.datastar.serialization

data class RawJS(
    val js: String = "",
)

fun String.asRawJS(): RawJS = RawJS(this)
