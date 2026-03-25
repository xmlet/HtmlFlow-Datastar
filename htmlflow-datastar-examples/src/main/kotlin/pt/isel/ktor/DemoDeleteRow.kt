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
import pt.isel.views.fragments.hfDeleteRowDescription
import pt.isel.views.htmlflow.hfDeleteRow
import pt.isel.views.htmlflow.hfDeleteRowTable

private val html = loadResource("public/html/delete-row.html")

val DEFAULT_USERS =
    listOf(
        TableUser(0, "Joe Smith", "joe@smith.org"),
        TableUser(1, "Angie MacDowell", "angie@macdowell.org"),
        TableUser(2, "Fuqua Tarkenton", "fuqua@tarkenton.org"),
        TableUser(3, "Kim Yee", "kim@yee.org"),
    )

val hfUsersTable: String =
    view<DeleteRowsState> {
        div {
            attrId("users-table")
            hfDeleteRowTable()
        }
    }.render(DeleteRowsState(DEFAULT_USERS))

private val deletedIndices = mutableSetOf<Int>()

fun Route.demoDeleteRow() {
    route("/delete-row") {
        get("/html", RoutingContext::getDeleteRowHtml)
        get("/htmlflow", RoutingContext::getDeleteRowHtmlFlow)
        delete("/{index}", RoutingContext::deleteRow)
        patch("/reset", RoutingContext::resetUsers)
        get("/description", RoutingContext::getDeleteRowDescription)
    }
}

private suspend fun RoutingContext.getDeleteRowHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getDeleteRowHtmlFlow() {
    val visibleUsers = DEFAULT_USERS.filterIndexed { i, _ -> i !in deletedIndices }
    call.respondText(hfDeleteRow.render(DeleteRowsState(visibleUsers)), ContentType.Text.Html)
}

private suspend fun RoutingContext.deleteRow() {
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

private suspend fun RoutingContext.resetUsers() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        deletedIndices.clear()
        generator.patchElements(hfUsersTable, options = PatchElementsOptions(selector = "#users-table"))
    }
}

private suspend fun RoutingContext.getDeleteRowDescription() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        generator.patchElements(hfDeleteRowDescription)
    }
}

data class DeleteRowsState(
    val users: List<TableUser>,
)
