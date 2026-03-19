package pt.isel.http4k

import jakarta.ws.rs.Path
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Signal
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchSignals
import pt.isel.ktor.ClickToEditSignals
import pt.isel.utils.EventBus
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfClickToEditSignals

private val html = loadResource("public/html/click-to-edit-signals.html")
private val bus = EventBus(ClickToEditSignals())

fun demoClickToEditViaSignals() =
    poly(
        "/html" bind Method.GET to ::getClickToEditSignalsHtml,
        "/htmlflow" bind Method.GET to ::getClickToEditSignalsHf,
        "/events" bindSse Method.GET to ::getClickToEditEvents,
        "/reset" bind Method.PATCH to ::clickToEditSignalsReset,
        "/cancel" bind Method.GET to ::clickToEditSignalsCancel,
        "" bind Method.PUT to ::clickToEditSignalsSave,
    )

fun getClickToEditSignalsHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html")

fun getClickToEditSignalsHf(req: Request): Response = Response(OK).body(hfClickToEditSignals).header("Content-Type", "text/html")

@Path("/click-to-edit-signals/events")
fun getClickToEditEvents(req: Request): SseResponse {
    val queue = bus.subscribe()
    return SseResponse { sse ->
        sse.onClose { bus.unsubscribe(queue) }
        while (true) {
            val event = queue.take()
            sse.sendPatchSignals(
                Signal.of(
                    " { firstName: '${event.firstName}', lastName: '${event.lastName}' , email: '${event.email}' }",
                ),
            )
        }
    }
}

@Path("/click-to-edit-signals/reset")
fun clickToEditSignalsReset(req: Request): Response {
    bus.publish(ClickToEditSignals())
    return Response(NO_CONTENT)
}

@Path("/click-to-edit-signals/cancel")
fun clickToEditSignalsCancel(req: Request): Response {
    val signals = bus.currentValue
    checkNotNull(signals)
    bus.publish(signals)
    return Response(NO_CONTENT)
}

@Path("/click-to-edit-signals")
fun clickToEditSignalsSave(req: Request): Response {
    val body = req.bodyString()
    val signals = Json.decodeFromString<ClickToEditSignals>(body)
    bus.publish(signals)
    return Response(NO_CONTENT)
}
