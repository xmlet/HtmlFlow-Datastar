package pt.isel.http4k

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfCounterEventView
import pt.isel.views.htmlflow.hfCounterViaSignals
import java.util.Observable
import java.util.concurrent.Flow

private val html = loadResource("public/html/counter.html")

private val counter = MutableStateFlow(0)

val demoCounter =
    poly(
        "/html" bind Method.GET to ::getCounterPageHtml,
        "/htmlflow" bind Method.GET to ::getCounterPageHtmlFlow,
        "/increment" bind Method.POST to ::increment,
        "/decrement" bind Method.POST to ::decrement,
        "/events" bindSse ::getCounterEvents,
    )

private fun getCounterPageHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html")

private fun getCounterPageHtmlFlow(req: Request): Response = Response(OK).body(hfCounterViaSignals).header("Content-Type", "text/html")

private fun getCounterEvents(req: Request): SseResponse =
    SseResponse { sse ->
        val job =
            CoroutineScope(Dispatchers.Default).launch {
                counter.collect { event ->
                    sse.sendPatchElements(Element.of(hfCounterEventView.render(event)))
                    if (event == 3) {
                        sse.sendPatchElements(
                            selector = Selector.of("#body"),
                            morphMode = MorphMode.append,
                            elements =
                                listOf(
                                    Element.of("<script data-effect=\"el.remove()\">alert('Thanks for trying Datastar!')</script>"),
                                ),
                        )
                    }
                }
            }
        sse.onClose { job.cancel() }
    }

private fun increment(req: Request): Response {
    counter.value++
    return Response(Status.NO_CONTENT)
}

private fun decrement(req: Request): Response {
    counter.value--
    return Response(Status.NO_CONTENT)
}
