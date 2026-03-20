package pt.isel.http4k

import jakarta.ws.rs.Path
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.datastar.MorphMode
import org.http4k.datastar.Selector
import org.http4k.datastar.Signal
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import org.http4k.sse.sendPatchSignals
import pt.isel.ktor.Signals
import pt.isel.ktor.newAgents
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfAgentRowView
import pt.isel.views.htmlflow.hfClickToLoad

private val description = loadResource("pt/isel/views/fragments/click-to-load-description.html")
private val html = loadResource("public/html/click-to-load.html")

fun demoClickToLoad() =
    poly(
        "/html" bind Method.GET to ::getClickToLoadHtml,
        "/htmlflow" bind Method.GET to ::getClickToLoadHtmlFlow,
        "/more" bindSse Method.GET to ::getMore,
        "/description" bindSse Method.GET to ::getClickToLoadDescription,
    )

fun getClickToLoadHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html")

fun getClickToLoadHtmlFlow(req: Request): Response = Response(OK).body(hfClickToLoad).header("Content-Type", "text/html")

@Path("/click-to-load/more")
fun getMore(req: Request): SseResponse =
    SseResponse { sse ->
        val datastarQueryArg = requireNotNull(req.query("datastar"))
        val (offset, limit) = Json.decodeFromString<Signals>(datastarQueryArg)

        Thread.sleep(1000) // Simulate delay — Thread.sleep instead of coroutine delay

        // Update the offset signal so the next "Load more" click advances correctly
        sse.sendPatchSignals(Signal.of("{offset: ${offset + limit}}"))

        // Render new rows and append them to #agents
        val htmlRows =
            newAgents(offset, offset + limit)
                .joinToString(separator = "") { agent -> hfAgentRowView.render(agent) }

        sse.sendPatchElements(
            selector = Selector.of("#agents"),
            morphMode = MorphMode.append,
            elements = listOf(Element.of(htmlRows)),
        )

        sse.close()
    }

@Path("/click-to-load/description")
fun getClickToLoadDescription(req: Request): SseResponse =
    SseResponse { sse ->
        sse
            .sendPatchElements(
                elements = listOf(Element.of(description)),
            )
    }
