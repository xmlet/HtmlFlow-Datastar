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
import pt.isel.ktor.ProgressBarState
import pt.isel.utils.loadResource
import pt.isel.views.fragments.hfProgressBarDescription
import pt.isel.views.htmlflow.hfProgressBar
import pt.isel.views.htmlflow.renderProgressBarFragment
import kotlin.random.Random

private val html = loadResource("public/html/progress-bar.html")

fun demoProgressBar(): PolyHandler =
    poly(
        "/html" bind Method.GET to ::getProgressBarHtml,
        "/htmlflow" bind Method.GET to ::getProgressBarHtmlFlow,
        "/description" bindSse Method.GET to ::getProgressBarDescription,
        "/updates" bind Method.GET to ::progressBarUpdates,
    )

fun getProgressBarHtml(req: Request): Response = Response(OK).body(html).body(html).header("Content-Type", "text/html; charset=utf-8")

fun getProgressBarHtmlFlow(req: Request): Response = Response(OK).body(hfProgressBar).header("Content-Type", "text/html; charset=utf-8")

@Path("/progress-bar/updates")
fun progressBarUpdates(req: Request): SseResponse =
    SseResponse { sse ->
        var progress = 0
        while (progress < 100) {
            val state = ProgressBarState(progress = progress, completed = false)
            sse.sendPatchElements(Element.of(renderProgressBarFragment.render(state)))

            Thread.sleep(200)
            progress += Random.nextInt(1, 16)
        }
        val finalState = ProgressBarState(progress = 100, completed = true)
        sse.sendPatchElements(Element.of(renderProgressBarFragment.render(finalState)))
    }

@Path("/progress-bar/description")
fun getProgressBarDescription(req: Request): SseResponse =
    SseResponse { sse -> sse.sendPatchElements(Element.of(hfProgressBarDescription)) }
