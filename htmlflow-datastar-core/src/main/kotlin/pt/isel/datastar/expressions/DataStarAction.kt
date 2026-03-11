package pt.isel.datastar.expressions

import jakarta.ws.rs.Path
import pt.isel.datastar.expressions.DataStarAction.Companion.addApostrophe
import pt.isel.datastar.expressions.DataStarAction.Companion.convertFuncToPath
import kotlin.reflect.KFunction

fun get(
    func: KFunction<*>,
    options: String? = null,
) = DataStarAction(ActionType.GET, convertFuncToPath(func), options)

fun get(
    path: String,
    options: String? = null,
) = DataStarAction(ActionType.GET, addApostrophe(path), options)

fun peek(callable: () -> String) = DataStarAction(ActionType.PEEK, callable)

fun peek(js: String) = DataStarAction(ActionType.PEEK, js)

fun setAll(
    value: Any,
    filter: String? = null,
) = DataStarAction(ActionType.SET_ALL, value, filter ?: "")

fun toggleAll(filter: String? = null) = DataStarAction(ActionType.TOGGLE_ALL, filter ?: "")

fun post(
    func: KFunction<*>,
    options: String? = null,
) = DataStarAction(ActionType.POST, convertFuncToPath(func), options)

fun post(
    path: String,
    options: String? = null,
) = DataStarAction(ActionType.POST, addApostrophe(path), options)

fun put(func: KFunction<*>) = DataStarAction(ActionType.PUT, convertFuncToPath(func))

fun put(path: String) = DataStarAction(ActionType.PUT, addApostrophe(path))

fun delete(func: KFunction<*>) = DataStarAction(ActionType.DELETE, convertFuncToPath(func))

fun delete(path: String) = DataStarAction(ActionType.DELETE, addApostrophe(path))

fun patch(func: KFunction<*>) = DataStarAction(ActionType.PATCH, convertFuncToPath(func))

fun patch(path: String) = DataStarAction(ActionType.PATCH, addApostrophe(path))

class DataStarAction internal constructor(
    val type: ActionType,
    vararg params: Any?,
) : DataStarExpression(makeExpression(type, *params)) {
    companion object {
        internal fun convertFuncToPath(func: KFunction<*>): String {
            val pathAnnotation = func.annotations.filterIsInstance<Path>().firstOrNull()
            val path = pathAnnotation?.value ?: throw IllegalArgumentException("Function ${func.name} must be annotated with @Path")
            return "'$path'"
        }

        internal fun addApostrophe(path: String) = "'$path'"
    }
}

private fun makeExpression(
    type: ActionType,
    vararg params: Any?,
): String =
    when (type) {
        ActionType.PEEK -> {
            if (params[0] is String) {
                type.syntax + "(" + params[0] + ")"
            } else {
                throw NotImplementedError("Only string literals are supported for peek actions at the moment.")
            }
        }

        else -> {
            type.syntax + "(" + params.filterNotNull().joinToString(", ") + ")"
        }
    }
