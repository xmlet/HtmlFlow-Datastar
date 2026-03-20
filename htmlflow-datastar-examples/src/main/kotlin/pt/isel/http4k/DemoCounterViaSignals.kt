package pt.isel.http4k

import jakarta.ws.rs.Path
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.datastar.Signal
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import org.http4k.sse.sendPatchSignals
import pt.isel.utils.EventBus
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfCounterViaSignals

private val description = loadResource("pt/isel/views/fragments/counter-signals-description.html")
private val html = loadResource("public/html/counter-signals.html")

private val bus = EventBus(0)

fun demoCounterSignals(): PolyHandler =
    poly(
        "/html" bind Method.GET to ::getCounterSignalsPageHtml,
        "/htmlflow" bind Method.GET to ::getCounterSignalsPageHtmlFlow,
        "/increment" bind Method.POST to ::incrementCounterViaSignals,
        "/decrement" bind Method.POST to ::decrementCounterViaSignals,
        "/events" bindSse ::getCounterEventsSignals,
        "/description" bindSse Method.GET to ::getCounterSignalsDescription,
    )

fun getCounterSignalsPageHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html")

fun getCounterSignalsPageHtmlFlow(req: Request): Response =
    Response(OK).body(hfCounterViaSignals).header("Content-Type", "text/html; charset=utf-8")

@Path("/counter-signals/events")
fun getCounterEventsSignals(req: Request): SseResponse {
    val queue = bus.subscribe()
    return SseResponse { sse ->
        sse.onClose { bus.unsubscribe(queue) }
        while (true) {
            val value = queue.take()
            sse.sendPatchSignals(Signal.of("{count: $value}"))
        }
    }
}

@Path("/counter-signals/increment")
fun incrementCounterViaSignals(req: Request): Response {
    val value = bus.currentValue
    checkNotNull(value)
    bus.publish(value + 1)
    return Response(Status.NO_CONTENT)
}

@Path("/counter-signals/decrement")
fun decrementCounterViaSignals(req: Request): Response {
    val value = bus.currentValue
    checkNotNull(value)
    bus.publish(value - 1)
    return Response(Status.NO_CONTENT)
}

@Path("/counter-signals/description")
fun getCounterSignalsDescription(req: Request): SseResponse =
    SseResponse { sse ->
        sse
            .sendPatchElements(
                elements = listOf(Element.of(description)),
            )
    }
