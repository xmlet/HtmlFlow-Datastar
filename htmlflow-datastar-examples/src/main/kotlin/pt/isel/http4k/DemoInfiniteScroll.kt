package pt.isel.http4k

import jakarta.ws.rs.Path
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.PolyHandler
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
import pt.isel.ktor.Agent
import pt.isel.ktor.Signals
import pt.isel.ktor.newAgents
import pt.isel.utils.loadResource
import pt.isel.views.fragments.hfInfiniteScrollDescription
import pt.isel.views.htmlflow.hfAgentRowView
import pt.isel.views.htmlflow.hfInfiniteScroll

private val html = loadResource("public/html/infinite-scroll.html")

fun demoInfiniteScroll(): PolyHandler =
    poly(
        "/html" bind Method.GET to ::getInfiniteScrollHtml,
        "/htmlflow" bind Method.GET to ::getInfiniteScrollHtmlFlow,
        "/description" bindSse Method.GET to ::getInfiniteScrollDescription,
        "/more" bindSse Method.GET to ::getMoreAgents,
    )

fun getInfiniteScrollHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html; charset=utf-8")

fun getInfiniteScrollHtmlFlow(req: Request): Response =
    Response(OK).body(hfInfiniteScroll).header("Content-Type", "text/html; charset=utf-8")

@Path("/infinite-scroll/more")
fun getMoreAgents(req: Request): SseResponse =
    SseResponse { sse ->
        Thread.sleep(1000) // Simulate some delay in loading more agents
        val datastarQueryArg = req.query("datastar")
        requireNotNull(datastarQueryArg)

        val (offset, limit) = Json.decodeFromString<Signals>(datastarQueryArg)
        sse.sendPatchSignals(Signal.of("{offset: ${offset + limit}}"))

        val agents = newAgents(offset, offset + limit)

        val htmlRows = agents.map { agent: Agent -> hfAgentRowView.render(agent) }.joinToString(separator = "") { it }

        sse.sendPatchElements(
            Element.of(htmlRows),
            selector = Selector.of("#agents"),
            morphMode = MorphMode.append,
        )
    }

@Path("/infinite-scroll/description")
fun getInfiniteScrollDescription(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(Element.of(hfInfiniteScrollDescription))
    }
