package pt.isel

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
import pt.isel.views.htmlflow.hfCounterViaSignals

private val html = loadResource("public/html/counter-signals.html")

fun Route.demoCounterSignals() {
    val counter: MutableStateFlow<Int> = MutableStateFlow(0)

    route("/counter-signals") {
        get("/html", RoutingContext::getCounterPageHtml)

        get("/htmlflow", RoutingContext::getCounterPageHtmlFlow)

        get("/events") {
            getCounterEvents(counter)
        }

        post("/increment") {
            postCounterIncrement(counter)
        }

        post("/decrement") {
            postCounterDecrement(counter)
        }
    }
}

private suspend fun RoutingContext.getCounterPageHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getCounterPageHtmlFlow() {
    call.respondText(hfCounterViaSignals, ContentType.Text.Html)
}

private suspend fun RoutingContext.getCounterEvents(counter: MutableStateFlow<Int>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val response = response(this)
        val generator = ServerSentEventGenerator(response)

        counter.collect {
            generator.patchSignals("{count: ${counter.value}}")
        }
    }
}

private fun RoutingContext.postCounterIncrement(counter: MutableStateFlow<Int>) {
    counter.value++
    call.response.status(HttpStatusCode.NoContent)
}

private fun RoutingContext.postCounterDecrement(counter: MutableStateFlow<Int>) {
    counter.value--
    call.response.status(HttpStatusCode.NoContent)
}
