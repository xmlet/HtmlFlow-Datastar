package pt.isel.datastar.expressions

import jakarta.ws.rs.Path
import kotlin.reflect.KFunction

class DataStarAction private constructor(
    val type: ActionType,
    vararg val params: Any,
) {
    companion object {
        fun peek(callable: () -> String) = DataStarAction(ActionType.PEEK, callable)

        fun peek(js: String) = DataStarAction(ActionType.PEEK, js)

        fun setAll(
            value: Any,
            filter: String? = null,
        ) = DataStarAction(ActionType.SET_ALL, value, filter ?: "")

        fun toggleAll(filter: String? = null) = DataStarAction(ActionType.TOGGLE_ALL, filter ?: "")

        fun get(func: KFunction<*>) = DataStarAction(ActionType.GET, convertFuncToPath(func))

        fun post(func: KFunction<*>) = DataStarAction(ActionType.POST, convertFuncToPath(func))

        fun put(func: KFunction<*>) = DataStarAction(ActionType.PUT, convertFuncToPath(func))

        fun delete(func: KFunction<*>) = DataStarAction(ActionType.DELETE, convertFuncToPath(func))

        fun patch(func: KFunction<*>) = DataStarAction(ActionType.PATCH, convertFuncToPath(func))

        private fun convertFuncToPath(func: KFunction<*>): String {
            val pathAnnotation = func.annotations.filterIsInstance<Path>().firstOrNull()
            val path = pathAnnotation?.value ?: throw IllegalArgumentException("Function ${func.name} must be annotated with @Path")
            return "'$path'"
        }
    }

    override fun toString(): String =
        when (type) {
            ActionType.PEEK -> {
                if (params[0] is String) {
                    type.syntax + "(" + params[0] + ")"
                } else {
                    throw NotImplementedError("Only string literals are supported for peek actions at the moment.")
                }
            }
			
            else -> {
                type.syntax + "(" + params.joinToString(", ") + ")"
            }
        }
}
