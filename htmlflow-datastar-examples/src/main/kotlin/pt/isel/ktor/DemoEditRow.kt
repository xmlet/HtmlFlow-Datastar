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
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.htmlflow.hfEditRow

private val html = loadResource("public/html/edit-row.html")

fun Route.demoEditRow() {
    val users = DEFAULT_USERS.toMutableList()
    val editingIndex = MutableStateFlow<Int?>(null)

    route("/edit-row") {
        get("/html", RoutingContext::getEditRowHtml)
        get("/htmlflow") { getEditRowHtmlFlow(TableState(users, editingIndex.value)) }
        get("/{index}") { editRow(users, editingIndex) }
        put("/reset") { resetUsers(users, editingIndex) }
        get("/cancel") { cancelEditRow(users, editingIndex) }
        patch("/{index}") { saveEditRow(users, editingIndex) }
    }
}

private suspend fun RoutingContext.getEditRowHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getEditRowHtmlFlow(tableState: TableState) {
    call.respondText(hfEditRow.render(tableState), ContentType.Text.Html)
}

private suspend fun RoutingContext.editRow(
    users: MutableList<TableUser>,
    editingIndex: MutableStateFlow<Int?>,
) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)

        val user =
            users
                .getOrNull(index) ?: return@respondTextWriter
        generator.patchSignals(
            """ { "name": "${user.name}", "email": "${user.email}" } """,
        )
        editingIndex.emit(index)
        generator.patchElements(hfEditRow.render(TableState(users, editingIndex.value)))
    }
}

private suspend fun RoutingContext.cancelEditRow(
    users: MutableList<TableUser>,
    editingIndex: MutableStateFlow<Int?>,
) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        editingIndex.emit(null)
        generator.patchElements(hfEditRow.render(TableState(users, null)))
    }
}

private suspend fun RoutingContext.resetUsers(
    users: MutableList<TableUser>,
    editingIndex: MutableStateFlow<Int?>,
) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        users.clear()
        users.addAll(DEFAULT_USERS)
        editingIndex.emit(null)
        generator.patchElements(hfEditRow.render(TableState(users, null)))
    }
}

private suspend fun RoutingContext.saveEditRow(
    users: MutableList<TableUser>,
    editingIndex: MutableStateFlow<Int?>,
) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)

        val datastarBodyArgs = call.request.call.receiveText()
        val editedUser = Json.decodeFromString<TableUser>(datastarBodyArgs)

        users[index] = editedUser
        editingIndex.emit(null)
        generator.patchElements(hfEditRow.render(TableState(users, null)))
    }
}

@Serializable
data class TableUser(
    val name: String,
    val email: String,
)

data class TableState(
    val users: MutableList<TableUser>,
    val editingIndex: Int? = null,
)
