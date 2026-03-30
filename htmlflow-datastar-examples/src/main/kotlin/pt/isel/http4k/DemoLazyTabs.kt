package pt.isel.http4k

import jakarta.ws.rs.Path
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.datastar.MorphMode
import org.http4k.datastar.Selector
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.path
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import pt.isel.utils.loadResource
import pt.isel.views.fragments.hfLazyTabsDescription
import pt.isel.views.htmlflow.TAB_CONTENTS
import pt.isel.views.htmlflow.hfLazyTabs
import pt.isel.views.htmlflow.hfTabPanelView

private val html = loadResource("public/html/lazy-tabs.html")

fun demoLazyTabs(): PolyHandler =
    poly(
        "/html" bind Method.GET to ::getLazyTabsHtml,
        "/htmlflow" bind Method.GET to ::getLazyTabsHtmlFlow,
        "/description" bindSse Method.GET to ::getLazyTabsDescription,
        "/{index}" bindSse Method.GET to ::getLazyTabsText,
    )

fun getLazyTabsHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html; charset=utf-8")

fun getLazyTabsHtmlFlow(req: Request): Response = Response(OK).body(hfLazyTabs).header("Content-Type", "text/html; charset=utf-8")

fun getLazyTabsText(req: Request): SseResponse =
    SseResponse { sse ->
        val idx = req.path("index")?.toInt() ?: 0

        val content = TAB_CONTENTS[idx]

        sse.sendPatchElements(
            Element.of(hfTabPanelView.render(content)),
            selector = Selector.of("#tabpanel"),
            morphMode = MorphMode.replace,
        )
        sse.close()
    }

@Path("/lazy-tabs/description")
fun getLazyTabsDescription(req: Request): SseResponse = SseResponse { sse -> sse.sendPatchElements(Element.of(hfLazyTabsDescription)) }
