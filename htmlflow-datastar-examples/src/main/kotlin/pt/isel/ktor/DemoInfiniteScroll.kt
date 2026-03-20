package pt.isel.ktor

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import dev.datastar.kotlin.sdk.ServerSentEventGenerator.Companion.invoke
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.htmlflow.hfInfiniteScroll

private val description = loadResource("pt/isel/views/fragments/infinite-scroll-description.html")
private val html = loadResource("public/html/infinite-scroll.html")

fun Route.demoInfiniteScroll() {
    route("/infinite-scroll") {
        get("/html", RoutingContext::getInfiniteScrollHtml)
        get("/htmlflow", RoutingContext::getInfiniteScrollHtmlFlow)
        get("/more", RoutingContext::getMoreAgents)
        get("/description", RoutingContext::getInfiniteScrollDescription)
    }
}

private suspend fun RoutingContext.getInfiniteScrollHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getInfiniteScrollHtmlFlow() {
    call.respondText(hfInfiniteScroll, ContentType.Text.Html)
}

private suspend fun RoutingContext.getMoreAgents() {
    getMore()
}

private suspend fun RoutingContext.getInfiniteScrollDescription() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        generator.patchElements(description)
    }
}
