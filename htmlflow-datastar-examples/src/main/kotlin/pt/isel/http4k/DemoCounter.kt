package pt.isel.http4k

import io.reactivex.rxjava3.subjects.BehaviorSubject
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
import java.util.concurrent.atomic.AtomicBoolean

private val html = loadResource("public/html/counter.html")

private val counter = BehaviorSubject.createDefault(0)

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
        val isActive = AtomicBoolean(true)
        val thread =
            Thread
                .ofVirtual()
                .start {
                    try {
                        counter.blockingSubscribe { event: Int ->
                            if (isActive.get()) {
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
                            }
                        }
                    } catch (e: InterruptedException) {
                        // Thread was interrupted, exit gracefully
                    }
                }

        sse.onClose {
            isActive.set(false)
            thread.interrupt()
            thread.join(1000) // Wait up to 1 second for thread to finish
        }
    }

private fun increment(req: Request): Response {
    val currentValue = counter.value ?: 0
    counter.onNext(currentValue + 1)
    return Response(Status.NO_CONTENT)
}

private fun decrement(req: Request): Response {
    val currentValue = counter.value ?: 0
    counter.onNext(currentValue - 1)
    return Response(Status.NO_CONTENT)
}
