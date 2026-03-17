package pt.isel.http4k

import kotlinx.serialization.json.Json
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Signal
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseHandler
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchSignals
import pt.isel.ktor.ClickToEditSignals
import pt.isel.ktor.loadResource
import pt.isel.utils.EventBus
import pt.isel.views.htmlflow.hfClickToEditSignals

private val html = loadResource("public/html/click-to-edit-signals.html")
private val bus = EventBus(ClickToEditSignals())

fun demoClickToEditViaSignals() =
    poly(
        "/html" bind Method.GET to getClickToEditSignalsHtml,
        "/htmlflow" bind Method.GET to getClickToEditSignalsHf,
        "/events" bindSse Method.GET to getClickToEditEvents,
        "/reset" bind Method.PATCH to resetViaSignals,
        "/cancel" bind Method.GET to cancelViaSignals,
        "" bind Method.PUT to saveViaSignals,
    )

val getClickToEditSignalsHtml: HttpHandler = { _ -> Response(OK).body(html).header("Content-Type", "text/html") }

val getClickToEditSignalsHf: HttpHandler = { _ -> Response(OK).body(hfClickToEditSignals).header("Content-Type", "text/html") }

val getClickToEditEvents: SseHandler = { _ ->
    val queue = bus.subscribe()
    SseResponse { sse ->
        while (true) {
            val event = queue.take()
            sse.sendPatchSignals(
                Signal.of(
                    " { firstName: '${event.firstName}', lastName: '${event.lastName}' , email: '${event.email}' }",
                ),
            )
        }
        sse.onClose { bus.unsubscribe(queue) }
    }
}

val resetViaSignals: HttpHandler = { _ ->
    bus.publish(ClickToEditSignals())
    Response(NO_CONTENT)
}

val cancelViaSignals: HttpHandler = { _ ->
    val signals = bus.currentValue
    checkNotNull(signals)
    bus.publish(signals)
    Response(NO_CONTENT)
}

val saveViaSignals: HttpHandler = { req ->
    val body = req.bodyString()
    val signals = Json.decodeFromString<ClickToEditSignals>(body)
    bus.publish(signals)
    Response(NO_CONTENT)
}
