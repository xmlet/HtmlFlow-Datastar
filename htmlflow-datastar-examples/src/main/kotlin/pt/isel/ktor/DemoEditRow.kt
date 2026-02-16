package pt.isel.ktor

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.views.htmlflow.hfEditRow

private val html = loadResource("public/html/edit-row.html")

fun Route.demoEditRow() {
    val state = MutableStateFlow(TableState(users = DEFAULT_USERS))
    route("/edit-row") {
        get("/html", RoutingContext::getEditRowHtml)
        get("/htmlflow", RoutingContext::getEditRowHtmlFlow)
        get("/{index}") { editRow(state) }
        put("/reset") { resetUsers(state) }
        get("/cancel") { cancelEditRow(state) }
        patch("/{index}") { saveEditRow(state) }
    }
}

private suspend fun RoutingContext.getEditRowHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getEditRowHtmlFlow() {
    call.respondText(hfEditRow.render(TableState(DEFAULT_USERS)), ContentType.Text.Html)
}

private suspend fun RoutingContext.editRow(state: MutableStateFlow<TableState>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)

        val user = state.value.users.getOrNull(index) ?: return@respondTextWriter

        generator.patchSignals(
            """ { "name": "${user.name}", "email": "${user.email}" } """,
        )
        state.emit(state.value.copy(editingIndex = index))
        generator.patchElements(hfEditRow.render(state.value))
    }
}

private suspend fun RoutingContext.cancelEditRow(state: MutableStateFlow<TableState>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        state.emit(state.value.copy(editingIndex = null))
        generator.patchElements(hfEditRow.render(state.value))
    }
}

private suspend fun RoutingContext.resetUsers(state: MutableStateFlow<TableState>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        state.emit(TableState(DEFAULT_USERS))
        generator.patchElements(hfEditRow.render(state.value))
    }
}

private suspend fun RoutingContext.saveEditRow(state: MutableStateFlow<TableState>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)

        val datastarBodyArgs = call.request.call.receiveText()

        val editedUser = Json.decodeFromString<TableUser>(datastarBodyArgs)

        if (index in state.value.users.indices) {
            val updatedUsers =
                state.value.users
                    .toMutableList()
                    .apply { this[index] = editedUser }
            state.emit(TableState(users = updatedUsers))
        }

        generator.patchElements(hfEditRow.render(state.value))
    }
}

@Serializable
data class TableUser(
    val name: String,
    val email: String,
)

data class TableState(
    val users: List<TableUser>,
    val editingIndex: Int? = null,
)
