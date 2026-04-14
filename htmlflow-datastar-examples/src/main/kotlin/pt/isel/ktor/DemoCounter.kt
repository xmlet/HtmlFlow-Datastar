package pt.isel.ktor

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.flow.MutableStateFlow
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.fragments.hfCounterDescription
import pt.isel.views.htmlflow.hfCounterEventView

private val html = loadResource("public/html/counter.html")

private val counter: MutableStateFlow<Int> = MutableStateFlow(0)

fun Route.demoCounter() {
    route("/counter") {
        get("/html", RoutingContext::getCounterPageHtml)

        get("/events", RoutingContext::getCounterEvents)

        post("/increment", RoutingContext::postCounterIncrement)

        post("/decrement", RoutingContext::postCounterDecrement)

        get("/description", RoutingContext::getCounterDescription)
    }
}

private suspend fun RoutingContext.getCounterPageHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getCounterEvents() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val response = response(this)
        val generator = ServerSentEventGenerator(response)

        counter.collect { event ->
            generator.patchElements(hfCounterEventView(event))

            if (event == 3) {
                generator.executeScript("""alert('Thanks for trying Datastar!')""")
            }
        }
    }
}

private suspend fun RoutingContext.getCounterDescription() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        generator.patchElements(hfCounterDescription)
    }
}

private fun RoutingContext.postCounterIncrement() {
    counter.value++
    call.response.status(HttpStatusCode.NoContent)
}

private fun RoutingContext.postCounterDecrement() {
    counter.value--
    call.response.status(HttpStatusCode.NoContent)
}
