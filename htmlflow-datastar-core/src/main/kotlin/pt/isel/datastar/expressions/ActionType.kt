package pt.isel.datastar.expressions

enum class ActionType(
    val syntax: String,
) {
    PEEK("@peek"),
    SET_ALL("@setAll"),
    TOGGLE_ALL("@toggleAll"),
    GET("@get"),
    POST("@post"),
    PUT("@put"),
    DELETE("@delete"),
    PATCH("@patch"),
}
