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
import pt.isel.views.htmlflow.hfLazyLoadDoc
import pt.isel.views.htmlflow.hfLazyLoadView

private val description = loadResource("pt/isel/views/fragments/lazy-load-description.html")
private val html = loadResource("public/html/lazy-load.html")

fun Route.demoLazyLoad() {
    route("/lazy-load") {
        get("/html", RoutingContext::getLazyLoadHtml)
        get("/htmlflow", RoutingContext::getLazyLoadHtmlFlow)
        get("/graph", RoutingContext::getLazyLoadGraph)
        get("/description", RoutingContext::getLazyLoadDescription)
    }
}

private suspend fun RoutingContext.getLazyLoadHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getLazyLoadHtmlFlow() {
    call.respondText(hfLazyLoadDoc, ContentType.Text.Html)
}

private suspend fun RoutingContext.getLazyLoadGraph() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        delay(2000)

        generator.patchElements(hfLazyLoadView.render(TOKYO_IMAGE))
    }
}

private suspend fun RoutingContext.getLazyLoadDescription() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        generator.patchElements(description)
    }
}

data class LazyLoadImage(
    val src: String,
    val alt: String,
)

val TOKYO_IMAGE =
    LazyLoadImage(
        src =
            "/images/tokyo-climate.png",
        alt = "Tokyo",
    )
