package pt.isel

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import htmlflow.div
import htmlflow.view
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.coroutines.delay
import org.xmlet.htmlapifaster.img
import pt.isel.views.htmlflow.hfLazyLoad

private val html = loadResource("public/html/lazy-load.html")

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
    call.respondText(hfLazyLoad, ContentType.Text.Html)
}

private suspend fun RoutingContext.getLazyLoadGraph() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        delay(2000)

        generator.patchElements(
            elements = buildLazyLoadHtml(),
        )
    }
}

private fun buildLazyLoadHtml(): String =
    view<Unit> {
        div {
            attrId("graph")
            img {
                attrSrc("/images/examples/tokyo.png")
                attrAlt("Tokyo")
                attrWidth(554)
                attrHeight(336)
            }
        }
    }.render(Unit)
