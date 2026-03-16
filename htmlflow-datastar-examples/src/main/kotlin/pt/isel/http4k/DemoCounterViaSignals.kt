package pt.isel.http4k

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Signal
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.sse.SseHandler
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchSignals
import pt.isel.utils.EventBus
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfCounterViaSignals

private val html = loadResource("public/html/counter-signals.html")

private val bus = EventBus(0)

fun demoCounterSignals(): PolyHandler =
    poly(
        "/html" bind Method.GET to getCounterPageHtml,
        "/htmlflow" bind Method.GET to getCounterPageHtmlFlow,
        "/increment" bind Method.POST to increment,
        "/decrement" bind Method.POST to decrement,
        "/events" bindSse getCounterEvents,
    )

private val getCounterPageHtml: HttpHandler = { _ ->
    Response(OK).body(html).header("Content-Type", "text/html")
}

private val getCounterPageHtmlFlow: HttpHandler = { _ ->
    Response(OK).body(hfCounterViaSignals).header("Content-Type", "text/html; charset=utf-8")
}

private val getCounterEvents: SseHandler = { _ ->
    val queue = bus.subscribe()
    SseResponse { sse ->
        while (true) {
            val value = queue.take()
            sse.sendPatchSignals(Signal.of("{count: $value}"))
        }
        sse.onClose { bus.unsubscribe(queue) }
    }
}

private val increment: HttpHandler = { _ ->
    val value = bus.currentValue
    checkNotNull(value)
    bus.publish(value + 1)
    Response(Status.NO_CONTENT)
}

private val decrement: HttpHandler = { _ ->
    val value = bus.currentValue
    checkNotNull(value)
    bus.publish(value - 1)
    Response(Status.NO_CONTENT)
}
