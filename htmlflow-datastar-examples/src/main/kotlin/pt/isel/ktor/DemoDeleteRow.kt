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
import kotlinx.coroutines.flow.MutableStateFlow
import pt.isel.views.htmlflow.hfDeleteRow

private val html = loadResource("public/html/delete-row.html")

fun Route.demoDeleteRow() {
    val state = MutableStateFlow(TableState(users = DEFAULT_USERS))
    route("/delete-row") {
        get("/html", RoutingContext::getDeleteRowHtml)
        get("/htmlflow", RoutingContext::getDeleteRowHtmlFlow)
        delete("/{index}") { deleteRow(state) }
        patch("/reset") { resetUsers(state) }
    }
}

private suspend fun RoutingContext.getDeleteRowHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getDeleteRowHtmlFlow() {
    call.respondText(hfDeleteRow.render(TableState(DEFAULT_USERS)), ContentType.Text.Html)
}

private suspend fun RoutingContext.deleteRow(state: MutableStateFlow<TableState>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)

        if (index in state.value.users.indices) {
            val updatedUsers = state.value.users.filterIndexed { i, _ -> i != index }
            state.emit(state.value.copy(users = updatedUsers))
        }

        generator.patchElements(hfDeleteRow.render(state.value))
    }
}

private suspend fun RoutingContext.resetUsers(state: MutableStateFlow<TableState>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        state.emit(TableState(DEFAULT_USERS))

        generator.patchElements(hfDeleteRow.render(state.value))
    }
}

val DEFAULT_USERS =
    listOf(
        TableUser("Joe Smith", "joe@smith.org"),
        TableUser("Angie MacDowell", "angie@macdowell.org"),
        TableUser("Fuqua Tarkenton", "fuqua@tarkenton.org"),
        TableUser("Kim Yee", "kim@yee.org"),
    )
