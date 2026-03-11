package pt.isel.ktor

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
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

private val html = loadResource("public/html/delete-row.html")

val DEFAULT_USERS =
    listOf(
        TableUser("Joe Smith", "joe@smith.org"),
        TableUser("Angie MacDowell", "angie@macdowell.org"),
        TableUser("Fuqua Tarkenton", "fuqua@tarkenton.org"),
        TableUser("Kim Yee", "kim@yee.org"),
    )

fun Route.demoDeleteRow() {
    val users = DEFAULT_USERS.toMutableList()
    route("/delete-row") {
        get("/html", RoutingContext::getDeleteRowHtml)
        get("/htmlflow") { getDeleteRowHtmlFlow(users) }
        delete("/{index}") { deleteRow(users) }
        patch("/reset") { resetUsers(users) }
    }
}

private suspend fun RoutingContext.getDeleteRowHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getDeleteRowHtmlFlow(users: MutableList<TableUser>) {
    call.respondText(hfDeleteRow.render(TableState(users = users)), ContentType.Text.Html)
}

private suspend fun RoutingContext.deleteRow(users: MutableList<TableUser>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)

        if (index in users.indices) {
            users.removeAt(index)
            generator.patchElements(hfDeleteRow.render(TableState(users = users)))
        } else {
            return@respondTextWriter
        }
    }
}

private suspend fun RoutingContext.resetUsers(users: MutableList<TableUser>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        users.clear()
        users.addAll(DEFAULT_USERS)

        generator.patchElements(hfDeleteRow.render(TableState(users = users)))
    }
}
