package org.xmlet.htmlflow.datastar.modifiers

enum class CaseStyle(
    val tag: String,
) {
    CAMEL("camel"),
    KEBAB("kebab"),
    SNAKE("snake"),
    PASCAL("pascal"),
    ;

    companion object {
        private val caseRegex = "__case\\.([a-z]+)".toRegex()

        fun String.extractCaseStyle(): CaseStyle? =
            caseRegex
                .find(this)
                ?.groupValues
                ?.get(1)
                ?.let { tag -> CaseStyle.entries.firstOrNull { it.tag == tag } }
    }

    fun apply(name: String): String =
        when (this) {
            CAMEL -> toCamel(name)
            PASCAL -> toPascal(name)
            SNAKE -> toSnake(name)
            KEBAB -> toKebab(name)
        }
}

private fun toCamel(name: String): String = convertCase(name, style = CaseStyle.CAMEL)

private fun toPascal(name: String): String = convertCase(name, style = CaseStyle.PASCAL)

private fun toSnake(name: String): String = convertCase(name, style = CaseStyle.SNAKE)

private fun toKebab(name: String): String = convertCase(name, style = CaseStyle.KEBAB)

private fun convertCase(
    name: String,
    style: CaseStyle,
): String {
    val (prefix, core) = splitLeadingUnderscores(name)

    val tokens =
        core
            // split camelCase / PascalCase
            .replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
            // normalize separators
            .replace('-', ' ')
            .replace('_', ' ')
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }

    val converted =
        when (style) {
            CaseStyle.CAMEL -> {
                tokens
                    .mapIndexed { i, t ->
                        if (i == 0) {
                            t.lowercase()
                        } else {
                            t.replaceFirstChar { it.uppercase() }
                        }
                    }.joinToString("")
            }

            CaseStyle.PASCAL -> {
                tokens.joinToString("") {
                    it.replaceFirstChar { c -> c.uppercase() }
                }
            }

            CaseStyle.SNAKE -> {
                tokens.joinToString("_") { it.lowercase() }
            }

            CaseStyle.KEBAB -> {
                tokens.joinToString("-") { it.lowercase() }
            }
        }

    return prefix + converted
}

private fun splitLeadingUnderscores(name: String): Pair<String, String> {
    val match = "^_+".toRegex().find(name)
    val prefix = match?.value ?: ""
    val rest = name.removePrefix(prefix)
    return prefix to rest
}
