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
import pt.isel.views.htmlflow.hfLazyLoad
import pt.isel.views.htmlflow.hfLazyLoadDoc

private val description = loadResource("public/html/fragments/lazy-load-description.html")
private val html =
    loadResource("public/html/lazy-load.html")
        .replace("<!-- DESCRIPTION -->", description)

fun Route.demoLazyLoad() {
    route("/lazy-load") {
        get("/html", RoutingContext::getLazyLoadHtml)
        get("/htmlflow", RoutingContext::getLazyLoadHtmlFlow)
        get("/graph", RoutingContext::getLazyLoadGraph)
    }
}

private suspend fun RoutingContext.getLazyLoadHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getLazyLoadHtmlFlow() {
    call.respondText(hfLazyLoad.render(), ContentType.Text.Html)
}

private suspend fun RoutingContext.getLazyLoadGraph() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        delay(2000)

        generator.patchElements(hfLazyLoadDoc.render(TOKYO_IMAGE))
    }
}

data class LazyLoadImage(
    val src: String,
    val alt: String,
)

val TOKYO_IMAGE =
    LazyLoadImage(
        src = @Suppress("ktlint:standard:max-line-length")
        "https://data-star.dev/cdn-cgi/image/format=auto,width=554/static/images/examples/tokyo-ded8c96be2a77738ddbd2f43b9d6c49e2e4c40756c8fb12ee2a60d64d4a1a0ec.png",
        alt = "Tokyo",
    )
