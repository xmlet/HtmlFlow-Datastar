package pt.isel.ktor

import dev.datastar.kotlin.sdk.ElementPatchMode
import dev.datastar.kotlin.sdk.PatchElementsOptions
import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import htmlflow.doc
import htmlflow.tr
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
import pt.isel.views.htmlflow.editRow
import pt.isel.views.htmlflow.hfEditRow

private val description = loadResource("public/html/fragments/edit-row-description.html")
private val html =
    loadResource("public/html/edit-row.html")
        .replace("<!-- DESCRIPTION -->", description)

fun hfPartialEditRowDoc(idx: Int): String =
    StringBuilder()
        .apply {
            doc {
                tr {
                    attrId("row-$idx")
                    editRow(idx)
                }
            }
        }.toString()

private val users = DEFAULT_USERS.toMutableList()

fun Route.demoEditRow() {
    route("/edit-row") {
        get("/html", RoutingContext::getEditRowHtml)
        get("/htmlflow", RoutingContext::getEditRowHtmlFlow)
        get("/{index}", RoutingContext::editRow)
        put("/reset", RoutingContext::resetUsers)
        get("/cancel", RoutingContext::cancelEditRow)
        patch("/{index}", RoutingContext::saveEditRow)
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
        val user = users[index]
        generator.patchSignals(
            """ { "name": "${user.name}", "email": "${user.email}" } """,
        )
        generator.patchElements(
            hfPartialEditRowDoc(index),
            PatchElementsOptions("#row-$index", mode = ElementPatchMode.Replace),
        )
    }
}

private suspend fun RoutingContext.cancelEditRow() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        users.forEachIndexed { index, user ->
            generator.patchElements(
                defaultRowView(index).render(user),
                PatchElementsOptions("#row-$index", mode = ElementPatchMode.Replace),
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
        users.forEachIndexed { index, user ->
            generator.patchElements(
                defaultRowView(index).render(user),
                PatchElementsOptions("#row-$index", mode = ElementPatchMode.Replace),
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
        users[index] = editedUser
        generator.patchElements(
            defaultRowView(index).render(editedUser),
            PatchElementsOptions("#row-$index", mode = ElementPatchMode.Replace),
        )
    }
}

@Serializable
data class TableUser(
    val name: String,
    val email: String,
)

data class TableState(
    val users: List<TableUser>,
)
