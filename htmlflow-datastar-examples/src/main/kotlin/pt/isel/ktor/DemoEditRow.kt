package pt.isel.ktor

import dev.datastar.kotlin.sdk.ElementPatchMode
import dev.datastar.kotlin.sdk.PatchElementsOptions
import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.htmlflow.defaultRowView
import pt.isel.views.htmlflow.hfEditRow
import pt.isel.views.htmlflow.hfPartialEditRowView

private val description = loadResource("pt/isel/views/fragments/edit-row-description.html")
private val html = loadResource("public/html/edit-row.html")

private val users = DEFAULT_USERS.toMutableList()

fun Route.demoEditRow() {
    route("/edit-row") {
        get("/html", RoutingContext::getEditRowHtml)
        get("/htmlflow", RoutingContext::getEditRowHtmlFlow)
        get("/{index}", RoutingContext::editRow)
        put("/reset", RoutingContext::resetUsers)
        get("/cancel", RoutingContext::cancelEditRow)
        patch("/{index}", RoutingContext::saveEditRow)
        get("/description", RoutingContext::getEditRowDescription)
    }
}

private suspend fun RoutingContext.getEditRowHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getEditRowHtmlFlow() {
    call.respondText(hfEditRow.render(TableState(users)), ContentType.Text.Html)
}

private suspend fun RoutingContext.editRow() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)

        if (index > users.size - 1) return@respondTextWriter call.respond(HttpStatusCode.BadRequest)
        val user = users.first { it.idx == index }
        generator.patchSignals(
            """ { "idx":${user.idx}, "name": "${user.name}", "email": "${user.email}" } """,
        )
        generator.patchElements(
            hfPartialEditRowView.render(user),
            PatchElementsOptions("#row-${user.idx}", mode = ElementPatchMode.Replace),
        )
    }
}

private suspend fun RoutingContext.cancelEditRow() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        users.forEach { user ->
            generator.patchElements(
                defaultRowView.render(user),
                PatchElementsOptions("#row-${user.idx}", mode = ElementPatchMode.Replace),
            )
        }
    }
}

private suspend fun RoutingContext.resetUsers() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        users.clear()
        users.addAll(DEFAULT_USERS)
        users.forEach { user ->
            generator.patchElements(
                defaultRowView.render(user),
                PatchElementsOptions("#row-${user.idx}", mode = ElementPatchMode.Replace),
            )
        }
    }
}

private suspend fun RoutingContext.saveEditRow() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)

        val datastarBodyArgs = call.request.call.receiveText()
        val editedUser = Json.decodeFromString<TableUser>(datastarBodyArgs)
        val userIdx = users.indexOf(users.first { it.idx == index })
        users[userIdx] = editedUser

        generator.patchElements(
            defaultRowView.render(editedUser),
            PatchElementsOptions("#row-${editedUser.idx}", mode = ElementPatchMode.Replace),
        )
    }
}

private suspend fun RoutingContext.getEditRowDescription() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        generator.patchElements(description)
    }
}

@Serializable
data class TableUser(
    val idx: Int,
    val name: String,
    val email: String,
)

data class TableState(
    val users: List<TableUser>,
)
