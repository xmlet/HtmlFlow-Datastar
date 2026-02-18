package pt.isel.datastar.extensions

fun convertToValidSignalName(name: String): String =
    name
        .split('-')
        .joinToString("") { part ->
            part.replaceFirstChar { if (it.isLowerCase()) it.uppercase() else it.toString() }
        }.replaceFirstChar { it.lowercase() }

fun isValidSignalName(name: String): Boolean = name.split('-').size == 1
