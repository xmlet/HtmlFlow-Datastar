package pt.isel.ktor

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.htmlflow.hfBulkUpdate

private val html = loadResource("public/html/bulk-update.html")

fun Route.demoBulkUpdate() {
    route("/bulk-update") {
        get("/html", RoutingContext::getBulkUpdateHtml)
        get("/htmlflow", RoutingContext::getBulkUpdateHtmlFlow)
        put("/activate", RoutingContext::activateUsers)
        put("/deactivate", RoutingContext::deactivateUsers)
    }
}

private suspend fun RoutingContext.getBulkUpdateHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getBulkUpdateHtmlFlow() {
    call.respondText(hfBulkUpdate.render(users), ContentType.Text.Html)
}

private val users =
    mutableListOf(
        User("Joe Smith", "joe@smith.org", UserStatus.ACTIVE),
        User("Angie MacDowell", "angie@macdowell.org", UserStatus.ACTIVE),
        User("Fuqua Tarkenton", "fuqua@tarkenton.org", UserStatus.INACTIVE),
        User("Kim Yee", "kim@yee.org", UserStatus.INACTIVE),
    )

private suspend fun RoutingContext.activateUsers() {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val datastarBodyArgs = call.request.call.receiveText()

        // Decode the selections from the request body
        // And reset all selections to false
        val (selections) = Json.decodeFromString<BulkUpdateSignals>(datastarBodyArgs)
        generator.patchSignals(""" {"selections" : """ + List(selections.size) { false }.toString() + "}")

        // Not thread safe update the selected users to ACTIVE
        selections.forEachIndexed { index, selected ->
            if (selected) {
                users[index] = users[index].copy(status = UserStatus.ACTIVE)
            }
        }
        val html = hfBulkUpdate.render(users).trimIndent().replace("\n", "")
        generator.patchElements(html)
    }
}

private suspend fun RoutingContext.deactivateUsers() {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val datastarBodyArgs = call.request.call.receiveText()

        // Decode the selections from the request body
        // And reset all selections to false
        val (selections) = Json.decodeFromString<BulkUpdateSignals>(datastarBodyArgs)
        generator.patchSignals(""" {"selections" : """ + List(selections.size) { false }.toString() + "}")

        // Not thread safe update the selected users to INACTIVE
        selections.forEachIndexed { index, selected ->
            if (selected) {
                users[index] = users[index].copy(status = UserStatus.INACTIVE)
            }
        }
        val html = hfBulkUpdate.render(users)

        generator.patchElements(html)
    }
}

@Serializable
data class BulkUpdateSignals(
    val selections: List<Boolean>,
)

data class User(
    val name: String,
    val email: String,
    val status: UserStatus,
)

enum class UserStatus(
    val syntax: String,
) {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
}
