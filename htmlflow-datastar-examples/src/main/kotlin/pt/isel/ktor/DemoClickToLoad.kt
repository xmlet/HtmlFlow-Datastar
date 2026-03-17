package pt.isel.ktor

import dev.datastar.kotlin.sdk.ElementPatchMode
import dev.datastar.kotlin.sdk.PatchElementsOptions
import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.htmlflow.hfAgentRowView
import pt.isel.views.htmlflow.hfClickToLoad

private val html = loadResource("public/html/click-to-load.html")

fun Route.demoClickToLoad() {
    route("/click-to-load") {
        get("/html", RoutingContext::getClickToLoadHtml)
        get("/htmlflow", RoutingContext::getClickToLoadHtmlFlow)
        get("/more", RoutingContext::getMore)
    }
}

private suspend fun RoutingContext.getClickToLoadHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getClickToLoadHtmlFlow() {
    call.respondText(hfClickToLoad, ContentType.Text.Html)
}

suspend fun RoutingContext.getMore() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        delay(1000) // Simulate some delay
        val generator = ServerSentEventGenerator(response(this))
        val datastarQueryArg = call.request.queryParameters["datastar"]
        requireNotNull(datastarQueryArg)

        // Decode the signals from the datastar query argument
        // and update the signals for the next request
        val (offset, limit) = Json.decodeFromString<Signals>(datastarQueryArg)
        generator.patchSignals("{offset: ${offset + limit}}")

        // Generate the new rows to be added to the table
        // and send the patch to the client

        val agents = newAgents(offset, offset + limit)

        val htmlRows = agents.map { agent: Agent -> hfAgentRowView.render(agent) }.joinToString(separator = "") { it }

        generator.patchElements(
            htmlRows,
            PatchElementsOptions(
                selector = "#agents",
                mode = ElementPatchMode.Append,
            ),
        )
    }
}

fun newAgents(
    from: Int,
    to: Int,
) = sequence {
    for (i in from until to) {
        val uuid = (0..7).joinToString("") { "%02x".format((0..255).random()) }
        yield(Agent("Agent Smith $i", "void$i@null.org", uuid))
    }
}

@Serializable
data class Signals(
    val offset: Int,
    val limit: Int,
)

data class Agent(
    val name: String,
    val email: String,
    val id: String,
)
