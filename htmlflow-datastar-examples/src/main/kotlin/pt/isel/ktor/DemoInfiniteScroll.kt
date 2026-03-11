package pt.isel.ktor

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfInfiniteScroll

private val html = loadResource("public/html/infinite-scroll.html")

fun Route.demoInfiniteScroll() {
    route("/infinite-scroll") {
        get("/html", RoutingContext::getInfiniteScrollHtml)
        get("/htmlflow", RoutingContext::getInfiniteScrollHtmlFlow)
        get("/more", RoutingContext::getMoreAgents)
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
