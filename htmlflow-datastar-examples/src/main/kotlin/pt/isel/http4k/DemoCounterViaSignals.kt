package pt.isel.http4k

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Signal
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchSignals
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfCounterViaSignals

private val html = loadResource("public/html/counter-signals.html")

fun demoCounterSignals(): PolyHandler {
    val counter = MutableStateFlow(0)
    return poly(
        "/html" bind Method.GET to ::getCounterPageHtml,
        "/htmlflow" bind Method.GET to ::getCounterPageHtmlFlow,
        "/increment" bind Method.POST to increment(counter),
        "/decrement" bind Method.POST to decrement(counter),
        "/events" bindSse getCounterEvents(counter),
    )
}

private fun getCounterPageHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html")

private fun getCounterPageHtmlFlow(req: Request): Response =
    Response(OK).body(hfCounterViaSignals).header("Content-Type", "text/html; charset=utf-8")

private fun getCounterEvents(counter: MutableStateFlow<Int>): (Request) -> SseResponse =
    {
        SseResponse { sse ->
            val job =
                CoroutineScope(Dispatchers.Default).launch {
                    counter.collect { value ->
                        sse.sendPatchSignals(Signal.of("{count: $value}"))
                    }
                }
            sse.onClose { job.cancel() }
        }
    }

private fun increment(counter: MutableStateFlow<Int>): (Request) -> Response =
    {
        counter.value++
        Response(Status.NO_CONTENT)
    }

private fun decrement(counter: MutableStateFlow<Int>): (Request) -> Response =
    {
        counter.value--
        Response(Status.NO_CONTENT)
    }
