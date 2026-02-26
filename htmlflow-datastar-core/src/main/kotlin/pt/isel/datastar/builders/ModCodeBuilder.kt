package pt.isel.datastar.builders

import pt.isel.datastar.modifiers.base.ModifierBuilder

class ModCodeBuilder<M : ModifierBuilder>(
    private val builderFactory: () -> M,
) {
    private var codeString: String? = null
    private var modString: String = ""

    fun code(block: () -> String) {
        codeString = block()
    }

    /**
     * This function is a marker for the DataStar plugin and is not intended to be executed at runtime.
     *
     * Usage:
     * - Allows you to write script blocks that reference the HTML element context in a type-safe way.
     * - The DataStar plugin will replace this call at pre-compilation with a script block of type () -> String,
     *   passing the correct JS expression as a string.
     *
     * If this function is ever executed at runtime, it will throw an exception.
     *
     * TODO: The block should eventually accept a parameter `el` representing the HTML element context.
     */
    fun code(block: (el: Any) -> Any): Unit =
        throw Exception(
            "This function is not intended to be executed." +
                " It is only used to provide type safety for the script block." +
                " This function call will be replaced in pre-compilation by HtmlFlow DataStar-Plugin.",
        )

    fun mods(block: M.() -> Unit) {
        val builder = builderFactory()
        builder.apply(block)
        modString = builder.toString()
    }

    val script: String get() = codeString ?: throw Exception("Script block must be provided.")
    val mods: String get() = modString
}
