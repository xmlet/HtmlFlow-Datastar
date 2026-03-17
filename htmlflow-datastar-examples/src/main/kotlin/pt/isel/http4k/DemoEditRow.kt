package pt.isel.http4k

import kotlinx.serialization.json.Json
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.datastar.MorphMode
import org.http4k.datastar.Selector
import org.http4k.datastar.Signal
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.path
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseHandler
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import org.http4k.sse.sendPatchSignals
import pt.isel.ktor.DEFAULT_USERS
import pt.isel.ktor.TableState
import pt.isel.ktor.TableUser
import pt.isel.ktor.hfPartialEditRowDoc
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.defaultRowView
import pt.isel.views.htmlflow.hfEditRow

private val html = loadResource("public/html/edit-row.html")

fun demoEditRow(): PolyHandler {
    val users = DEFAULT_USERS.toMutableList()
    return poly(
        "/html" bind Method.GET to getEditRowPageHtml,
        "/htmlflow" bind Method.GET to getEditRowHtmlFlow(TableState(users)),
        "/reset" bindSse Method.PUT to resetTable(users),
        "/cancel" bindSse Method.GET to cancelEditRow(users),
        "/{index}" bindSse Method.GET to editRow(users),
        "/{index}" bindSse Method.PATCH to saveEditRow(users),
    )
}

private val getEditRowPageHtml: HttpHandler = { _ ->
    Response(OK).body(html).header("Content-Type", "text/html; charset=utf-8")
}

private fun getEditRowHtmlFlow(tableState: TableState): HttpHandler =
    { _ ->
        Response(OK).body(hfEditRow.render(tableState)).header("Content-Type", "text/html; charset=utf-8")
    }

private fun editRow(users: List<TableUser>): SseHandler =
    { request ->
        val index = request.path("index")?.toIntOrNull()
        checkNotNull(index) { "Index can't be null" }
        val user = users[index]
        SseResponse { sse ->
            sse.sendPatchSignals(Signal.of("""{ "name": "${user.name}", "email": "${user.email}" }"""))
            sse.sendPatchElements(
                elements = listOf(Element.of(hfPartialEditRowDoc(index))),
                morphMode = MorphMode.replace,
                selector = Selector.of("#row-$index"),
            )
        }
    }

private fun resetTable(users: MutableList<TableUser>): SseHandler =
    {
        SseResponse { sse ->
            users.clear()
            users.addAll(DEFAULT_USERS)
            users.forEachIndexed { index, user ->
                sse.sendPatchElements(
                    elements = listOf(Element.of(defaultRowView(index).render(user))),
                    morphMode = MorphMode.replace,
                    selector = Selector.of("#row-$index"),
                )
            }
        }
    }

private fun cancelEditRow(users: List<TableUser>): SseHandler =
    {
        SseResponse { sse ->

            users.forEachIndexed { index, user ->
                sse.sendPatchElements(
                    elements = listOf(Element.of(defaultRowView(index).render(user))),
                    morphMode = MorphMode.replace,
                    selector = Selector.of("#row-$index"),
                )
            }
        }
    }

private fun saveEditRow(users: MutableList<TableUser>): SseHandler =
    { request ->
        val index = request.path("index")?.toIntOrNull()
        checkNotNull(index) { "Index can't be null" }
        val body = request.bodyString()
        val editedUser = Json.decodeFromString<TableUser>(body)
        users[index] = editedUser
        SseResponse { sse ->
            sse.sendPatchElements(
                elements = listOf(Element.of(defaultRowView(index).render(editedUser))),
                morphMode = MorphMode.replace,
                selector = Selector.of("#row-$index"),
            )
        }
    }
