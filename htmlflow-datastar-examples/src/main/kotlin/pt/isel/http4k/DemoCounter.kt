package pt.isel.http4k

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.datastar.MorphMode
import org.http4k.datastar.Selector
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseHandler
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import pt.isel.utils.EventBus
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfCounterEventView
import pt.isel.views.htmlflow.hfCounterViaSignals

private val html = loadResource("public/html/counter.html")

val bus = EventBus(0)

fun demoCounter() =
    poly(
        "/html" bind Method.GET to ::getCounterPageHtml,
        "/htmlflow" bind Method.GET to ::getCounterPageHtmlFlow,
        "/increment" bind Method.POST to ::increment,
        "/decrement" bind Method.POST to ::decrement,
        "/events" bindSse Method.GET to getCounterEvents,
    )

private fun getCounterPageHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html")

private fun getCounterPageHtmlFlow(req: Request): Response = Response(OK).body(hfCounterViaSignals).header("Content-Type", "text/html")

private val getCounterEvents: SseHandler = { req ->
    val queue = bus.subscribe()
    SseResponse { sse ->
        while (true) {
            try {
                val event = queue.take()
                sse.sendPatchElements(Element.of(hfCounterEventView.render(event)))

                if (event == 3) {
                    sse.sendPatchElements(
                        selector = Selector.of("#body"),
                        morphMode = MorphMode.append,
                        elements =
                            listOf(
                                Element.of(
                                    "<script data-effect=\"el.remove()\">alert('Thanks for trying Datastar!')</script>",
                                ),
                            ),
                    )
                }
            } catch (_: InterruptedException) {
                bus.unsubscribe(queue)
            }
        }
        sse.onClose { bus.unsubscribe(queue) }
    }
}

private fun increment(req: Request): Response {
    bus.publish(bus.currentValue.get()!! + 1)
    return Response(Status.NO_CONTENT)
}

private fun decrement(req: Request): Response {
    bus.publish(bus.currentValue.get()!! - 1)
    return Response(Status.NO_CONTENT)
}
