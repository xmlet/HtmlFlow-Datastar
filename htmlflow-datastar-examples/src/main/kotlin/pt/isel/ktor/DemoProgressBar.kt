package pt.isel.ktor

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
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.fragments.hfProgressBarDescription
import pt.isel.views.htmlflow.hfProgressBar
import pt.isel.views.htmlflow.renderProgressBarFragment
import kotlin.random.Random

private val html = loadResource("public/html/progress-bar.html")

fun Route.demoProgressBar() {
    route("/progress-bar") {
        get("/html", RoutingContext::getProgressBarHtml)
        get("/htmlflow", RoutingContext::getProgressBarHtmlFlow)
        get("/updates", RoutingContext::progressBarUpdates)
        get("/description", RoutingContext::getProgressBarDescription)
    }
}

private suspend fun RoutingContext.getProgressBarHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getProgressBarHtmlFlow() {
    call.respondText(hfProgressBar, ContentType.Text.Html)
}

private suspend fun RoutingContext.progressBarUpdates() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        var progress = 0

        while (progress < 100) {
            val state = ProgressBarState(progress = progress, completed = false)
            generator.patchElements(renderProgressBarFragment.render(state))

            delay(200)
            progress += Random.nextInt(1, 16)
        }

        val finalState = ProgressBarState(progress = 100, completed = true)
        generator.patchElements(renderProgressBarFragment.render(finalState))
    }
}

private suspend fun RoutingContext.getProgressBarDescription() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        generator.patchElements(hfProgressBarDescription)
    }
}

data class ProgressBarState(
    val progress: Int = 0,
    val completed: Boolean = false,
)
