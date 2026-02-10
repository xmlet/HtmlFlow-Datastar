package pt.isel

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import htmlflow.div
import htmlflow.tr
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.i
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn
import pt.isel.views.htmlflow.hfDeleteRow

private val html = loadResource("public/html/delete-row.html")

fun Route.demoDeleteRow() {
    val users = MutableStateFlow(DEFAULT_USERS)
    route("/delete-row") {
        get("/html", RoutingContext::getDeleteRowHtml)
        get("/htmlflow", RoutingContext::getDeleteRowHtmlFlow)
        delete("/{index}") { deleteRow(users) }
        patch("/reset") { resetUsers(users) }
    }
}

private suspend fun RoutingContext.getDeleteRowHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getDeleteRowHtmlFlow() {
    call.respondText(hfDeleteRow, ContentType.Text.Html)
}

private suspend fun RoutingContext.deleteRow(users: MutableStateFlow<List<DeleteRowSignals>>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)

        if (index in users.value.indices) {
            val updatedUsers = users.value.filterIndexed { i, _ -> i != index }
            users.emit(updatedUsers)
        }

        val html = buildDeleteRowHtml(users.value)
        generator.patchElements(html)
    }
}

private suspend fun RoutingContext.resetUsers(users: MutableStateFlow<List<DeleteRowSignals>>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        users.emit(DEFAULT_USERS)

        val html = buildDeleteRowHtml(users.value)
        generator.patchElements(html)
    }
}

private fun buildDeleteRowHtml(users: List<DeleteRowSignals>): String =
    view<List<DeleteRowSignals>> {
        div {
            attrId("demo")
            table {
                thead {
                    tr {
                        th { text("Name") }
                        th { text("Email") }
                        th { text("Actions") }
                    }
                }
                tbody {
                    users.forEachIndexed { index, user ->
                        tr {
                            td { text(user.name) }
                            td { text(user.email) }
                            td {
                                button {
                                    attrClass("error")
                                    dataOn("click", "confirm('Are you sure?') && @delete('/delete-row/$index')")
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled", "$fetching")
                                    text("Delete")
                                }
                            }
                        }
                    }
                }
            }
            div {
                button {
                    attrClass("warning")
                    dataOn("click", "@patch('/delete-row/reset')")
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled", "$fetching")
                    i { attrClass("pixelarticons:user-plus") }
                    text("Reset")
                }
            }
        }
    }.render(users)

@Serializable
data class DeleteRowSignals(
    val name: String,
    val email: String,
)

val DEFAULT_USERS =
    listOf(
        DeleteRowSignals("Joe Smith", "joe@smith.org"),
        DeleteRowSignals("Angie MacDowell", "angie@macdowell.org"),
        DeleteRowSignals("Fuqua Tarkenton", "fuqua@tarkenton.org"),
        DeleteRowSignals("Kim Yee", "kim@yee.org"),
    )
