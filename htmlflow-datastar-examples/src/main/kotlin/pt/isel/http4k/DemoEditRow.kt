package pt.isel.http4k

import jakarta.ws.rs.Path
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Request
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
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import org.http4k.sse.sendPatchSignals
import pt.isel.ktor.DEFAULT_USERS
import pt.isel.ktor.TableState
import pt.isel.ktor.TableUser
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.defaultRowView
import pt.isel.views.htmlflow.hfEditRow
import pt.isel.views.htmlflow.hfPartialEditRowView

private val html = loadResource("public/html/edit-row.html")

var tableState = TableState(DEFAULT_USERS.toMutableList())

fun demoEditRow(): PolyHandler =
    poly(
        "/html" bind Method.GET to ::getEditRowPageHtml,
        "/htmlflow" bind Method.GET to ::getEditRowHtmlFlow,
        "/reset" bindSse Method.PUT to ::resetTable,
        "/cancel" bindSse Method.GET to ::cancelEditRow,
        "/{index}" bindSse Method.GET to ::editRow,
        "/{index}" bindSse Method.PATCH to ::saveEditRow,
    )

fun getEditRowPageHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html; charset=utf-8")

fun getEditRowHtmlFlow(req: Request): Response =
    Response(OK).body(hfEditRow.render(tableState)).header("Content-Type", "text/html; charset=utf-8")

fun editRow(req: Request): SseResponse {
    val index = req.path("index")?.toIntOrNull()
    checkNotNull(index) { "Index can't be null" }
    val user = tableState.users[index]
    return SseResponse { sse ->
        sse.sendPatchSignals(Signal.of("""{ "idx": ${user.idx}, "name": "${user.name}", "email": "${user.email}" }"""))
        sse.sendPatchElements(
            elements = listOf(Element.of(hfPartialEditRowView.render(user))),
            morphMode = MorphMode.replace,
            selector = Selector.of("#row-$index"),
        )
    }
}

@Path("/edit-row/reset")
fun resetTable(req: Request): SseResponse =
    SseResponse { sse ->
        (tableState.users as MutableList).clear()
        (tableState.users as MutableList).addAll(DEFAULT_USERS)
        tableState.users.forEach { user ->
            sse.sendPatchElements(
                elements = listOf(Element.of(defaultRowView.render(user))),
                morphMode = MorphMode.replace,
                selector = Selector.of("#row-${user.idx}"),
            )
        }
    }

@Path("/edit-row/cancel")
fun cancelEditRow(req: Request): SseResponse =
    SseResponse { sse ->
        tableState.users.forEach { user ->
            sse.sendPatchElements(
                elements = listOf(Element.of(defaultRowView.render(user))),
                morphMode = MorphMode.replace,
                selector = Selector.of("#row-${user.idx}"),
            )
        }
    }

fun saveEditRow(req: Request): SseResponse {
    val index = req.path("index")?.toIntOrNull()
    checkNotNull(index) { "Index can't be null" }
    val body = req.bodyString()
    val editedUser = Json.decodeFromString<TableUser>(body)
    val userIdx = tableState.users.indexOf(tableState.users.first { it.idx == index })
    (tableState.users as MutableList)[userIdx] = editedUser
    return SseResponse { sse ->
        sse.sendPatchElements(
            elements = listOf(Element.of(defaultRowView.render(editedUser))),
            morphMode = MorphMode.replace,
            selector = Selector.of("#row-$index"),
        )
    }
}
