package pt.isel.http4k

import jakarta.ws.rs.Path
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.datastar.MorphMode
import org.http4k.datastar.Selector
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.path
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import pt.isel.ktor.DEFAULT_USERS
import pt.isel.ktor.DeleteRowsState
import pt.isel.ktor.hfUsersTable
import pt.isel.utils.loadResource
import pt.isel.views.fragments.hfDeleteRowDescription
import pt.isel.views.htmlflow.hfDeleteRow

private val html = loadResource("public/html/delete-row.html")
private val deletedIndices = mutableSetOf<Int>()

fun demoDeleteRow() =
    poly(
        "/html" bind Method.GET to ::getDeleteRowHtml,
        "/htmlflow" bind Method.GET to ::getDeleteRowHtmlFlow,
        "/{index}" bindSse Method.DELETE to ::deleteRow,
        "/reset" bindSse Method.PATCH to ::restoreRows,
        "/description" bindSse Method.GET to ::getDeleteRowDescription,
    )

private fun getDeleteRowHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html")

private fun getDeleteRowHtmlFlow(req: Request): Response {
    val visibleUsers = DEFAULT_USERS.filterIndexed { i, _ -> i !in deletedIndices }
    return Response(OK)
        .body(hfDeleteRow.render(DeleteRowsState(visibleUsers)))
        .header("Content-Type", "text/html")
}

fun deleteRow(req: Request): SseResponse {
    val index = req.path("index")?.toIntOrNull()
    checkNotNull(index) { "Index can't be null" }
    deletedIndices.add(index)
    return SseResponse { sse ->
        sse.sendPatchElements(
            selector = Selector.of("#row-$index"),
            morphMode = MorphMode.remove,
        )
        sse.close()
    }
}

@Path("/delete-row/reset")
fun restoreRows(req: Request): SseResponse =
    SseResponse { sse ->
        deletedIndices.clear()
        sse.sendPatchElements(
            Element.of(hfUsersTable),
            selector = Selector.of("#users-table"),
        )
        sse.close()
    }

@Path("/delete-row/description")
fun getDeleteRowDescription(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(
            elements = listOf(Element.of(hfDeleteRowDescription)),
        )
    }
