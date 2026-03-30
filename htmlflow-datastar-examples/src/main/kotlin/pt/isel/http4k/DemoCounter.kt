package pt.isel.http4k

import jakarta.ws.rs.Path
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
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import pt.isel.utils.EventBus
import pt.isel.utils.loadResource
import pt.isel.views.fragments.hfCounterDescription
import pt.isel.views.htmlflow.hfCounter
import pt.isel.views.htmlflow.hfCounterEventView
import java.io.InputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream

private val html = loadResource("public/html/counter.html")

private val bus = EventBus(0)

fun demoCounter() =
    poly(
        "/html" bind Method.GET to ::getCounterPageHtml,
        "/htmlflow" bind Method.GET to ::getCounterPageHtmlFlow,
        "/increment" bind Method.POST to ::incrementCounter,
        "/decrement" bind Method.POST to ::decrementCounter,
        "/events" bindSse Method.GET to ::counterEvents,
        "/description" bind Method.GET to ::getCounterDescription,
    )

fun getCounterPageHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html")

fun getCounterPageHtmlFlow(req: Request): Response = Response(OK).body(hfCounter).header("Content-Type", "text/html")

@Path("/counter/events")
fun counterEvents(req: Request): SseResponse {
    val queue = bus.subscribe()
    return SseResponse { sse ->
        sse.onClose { bus.unsubscribe(queue) }
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
    }
}

@Path("/counter/description")
fun getCounterDescription(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(
            elements = listOf(Element.of(hfCounterDescription)),
        )
    }

@Path("/counter/increment")
fun incrementCounter(req: Request): Response {
    val currentCount = bus.currentValue ?: 0
    bus.publish(currentCount + 1)
    return Response(Status.NO_CONTENT)
}

@Path("/counter/decrement")
fun decrementCounter(req: Request): Response {
    val currentCount = bus.currentValue ?: 0
    bus.publish(currentCount - 1)
    return Response(Status.NO_CONTENT)
}
