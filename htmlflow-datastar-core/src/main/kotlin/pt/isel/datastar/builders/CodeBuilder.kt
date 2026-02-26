package pt.isel.datastar.builders

class CodeBuilder {
    private var codeString: String? = null

    fun code(block: () -> String) {
        codeString = block()
    }

    fun code(block: (el: Any) -> Any): Unit =
        throw Exception(
            "This function is not intended to be executed." +
                " It is only used to provide type safety for the script block." +
                "This functions call will be replaced in pre-compilation by HtmlfFlow DataStar-Plugin.",
        )

    val code: String get() =
        codeString ?: throw Exception("Script block must be provided.")
}
