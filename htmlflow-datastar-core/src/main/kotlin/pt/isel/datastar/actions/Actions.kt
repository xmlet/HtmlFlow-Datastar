package pt.isel.datastar.actions

import jakarta.ws.rs.Path
import kotlin.reflect.KFunction

enum class ActionsType(
    val js: String,
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

/*
dataInit {
	script { Action.get(example::class) }
	mods { delay(1000.ms) }
}
 */

class Action private constructor(
    val type: ActionsType,
    vararg val params: Any,
) {
    companion object {
        fun peek(callable: () -> String) = Action(ActionsType.PEEK, callable)

        fun setAll(
            value: Any,
            filter: String? = null,
        ) = Action(ActionsType.SET_ALL, value, filter ?: "")

        fun toggleAll(filter: String? = null) = Action(ActionsType.TOGGLE_ALL, filter ?: "")

        fun get(func: KFunction<*>) = Action(ActionsType.GET, convertFuncToPath(func))

        fun post(func: KFunction<*>) = Action(ActionsType.POST, convertFuncToPath(func))

        fun put(func: KFunction<*>) = Action(ActionsType.PUT, convertFuncToPath(func))

        fun delete(func: KFunction<*>) = Action(ActionsType.DELETE, convertFuncToPath(func))

        fun patch(func: KFunction<*>) = Action(ActionsType.PATCH, convertFuncToPath(func))

        private fun convertFuncToPath(func: KFunction<*>): String {
            val pathAnnotation = func.annotations.filterIsInstance<Path>().firstOrNull()
            val path = pathAnnotation?.value ?: throw IllegalArgumentException("Function ${func.name} must be annotated with @Path")
            return "'$path'"
        }
    }

    override fun toString(): String =
        when (type) {
            ActionsType.PEEK -> {
                val callable = params[0] as () -> String
                type.js + "(" + callable() + ")"
            }
			
            else -> {
                type.js + "(" + params.joinToString(", ") + ")"
            }
        }
}
