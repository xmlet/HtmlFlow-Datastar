package pt.isel

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

private val html = loadResource("click-to-load.html")

fun Route.demoClickToLoad() {
    route("/click-to-load") {
        get {
            call.respondText(html, ContentType.Text.Html)
        }
    }
}
