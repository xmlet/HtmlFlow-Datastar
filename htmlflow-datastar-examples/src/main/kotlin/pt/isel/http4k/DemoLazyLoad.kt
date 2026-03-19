package pt.isel.http4k

import jakarta.ws.rs.Path
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import pt.isel.ktor.TOKYO_IMAGE
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfLazyLoadDoc
import pt.isel.views.htmlflow.hfLazyLoadView

private val html = loadResource("public/html/lazy-load.html")

fun demoLazyLoad(): PolyHandler =
    poly(
        "/html" bind Method.GET to ::getLazyLoadHtml,
        "/htmlflow" bind Method.GET to ::getLazyLoadHtmlFlow,
        "/graph" bindSse Method.GET to ::loadGraph,
    )

fun getLazyLoadHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html; charset=utf-8")

fun getLazyLoadHtmlFlow(req: Request): Response = Response(OK).body(hfLazyLoadDoc).header("Content-Type", "text/html")

@Path("/lazy-load/graph")
fun loadGraph(req: Request): SseResponse =
    SseResponse { sse ->
        Thread.sleep(2000)
        sse.sendPatchElements(
            Element.of(hfLazyLoadView.render(TOKYO_IMAGE)),
        )
    }
