package pt.isel.ktor

import dev.datastar.kotlin.sdk.ElementPatchMode
import dev.datastar.kotlin.sdk.PatchElementsOptions
import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import htmlflow.div
import htmlflow.view
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.htmlflow.hfDeleteRow
import pt.isel.views.htmlflow.hfDeleteRowTable

private val html = loadResource("public/html/delete-row.html")

val DEFAULT_USERS =
    listOf(
        TableUser("Joe Smith", "joe@smith.org"),
        TableUser("Angie MacDowell", "angie@macdowell.org"),
        TableUser("Fuqua Tarkenton", "fuqua@tarkenton.org"),
        TableUser("Kim Yee", "kim@yee.org"),
    )

val hfUsersTable: String =
    view<DeleteRowsState> {
        div {
            attrId("users-table")
            hfDeleteRowTable()
        }
    }.render(DeleteRowsState(DEFAULT_USERS))

fun Route.demoDeleteRow() {
    val deletedIndices = mutableSetOf<Int>()
    route("/delete-row") {
        get("/html", RoutingContext::getDeleteRowHtml)
        get("/htmlflow") { getDeleteRowHtmlFlow(DEFAULT_USERS, deletedIndices) }
        delete("/{index}") { deleteRow(deletedIndices) }
        patch("/reset") { resetUsers(deletedIndices) }
    }
}

private suspend fun RoutingContext.getDeleteRowHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getDeleteRowHtmlFlow(
    users: List<TableUser>,
    deletedIndices: Set<Int>,
) {
    val visibleUsers = users.filterIndexed { i, _ -> i !in deletedIndices }
    call.respondText(hfDeleteRow.render(DeleteRowsState(visibleUsers)), ContentType.Text.Html)
}

private suspend fun RoutingContext.deleteRow(deletedIndices: MutableSet<Int>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)
        deletedIndices.add(index)
        generator.patchElements(
            options = PatchElementsOptions(selector = "#row-$index", mode = ElementPatchMode.Remove),
        )
    }
}

private suspend fun RoutingContext.resetUsers(deletedIndices: MutableSet<Int>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        deletedIndices.clear()
        generator.patchElements(hfUsersTable, options = PatchElementsOptions(selector = "#users-table"))
    }
}

data class DeleteRowsState(
    val users: List<TableUser>,
)
