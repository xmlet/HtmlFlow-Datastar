package pt.isel

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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val html = loadResource("click-to-load.html")

fun Route.demoClickToLoad() {
    route("/click-to-load") {
        get(RoutingContext::getClickToLoad)
        get("/more", RoutingContext::getMore)
    }
}

private suspend fun RoutingContext.getClickToLoad() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getMore() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val response = response(this)
        val generator = ServerSentEventGenerator(response)
        val datastarQueryArg = call.request.queryParameters["datastar"]
        requireNotNull(datastarQueryArg)

        // Decode the signals from the datastar query argument
        // and update the signals for the next request
        val (offset, limit) = Json.decodeFromString<Signals>(datastarQueryArg)
        generator.patchSignals("{offset: ${offset + limit}}")

        // Generate the new rows to be added to the table
        // and send the patch to the client
        val htmlRow =
            newAgents(offset, offset + limit).joinToString("\n") { agent ->
                ROW.format(agent.name, agent.email, agent.id)
            }
        generator.patchElements(
            htmlRow,
            PatchElementsOptions(
                selector = "#agents",
                mode = ElementPatchMode.Append,
            ),
        )
    }
}

private const val ROW = """<tr><td>%s</td><td>%s</td><td>%s</td></tr>"""

private fun newAgents(
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

private data class Agent(
    val name: String,
    val email: String,
    val id: String,
)
