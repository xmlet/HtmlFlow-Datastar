package pt.isel.ktor

import dev.datastar.kotlin.sdk.ElementPatchMode
import dev.datastar.kotlin.sdk.PatchElementsOptions
import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.htmlflow.TAB_CONTENTS
import pt.isel.views.htmlflow.hfLazyTabs
import pt.isel.views.htmlflow.hfTabPanelView

private val description = loadResource("public/html/fragments/lazy-tabs-description.html")
private val html =
    loadResource("public/html/lazy-tabs.html")
        .replace("<!-- DESCRIPTION -->", description)

fun Route.demoLazyTabs() {
    route("/lazy-tabs") {
        get("/html", RoutingContext::getLazyTabsHtml)
        get("/htmlflow", RoutingContext::getLazyTabsHtmlFlow)
        get("/{index}", RoutingContext::getLazyTabsText)
    }
}

private suspend fun RoutingContext.getLazyTabsHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getLazyTabsHtmlFlow() {
    call.respondText(hfLazyTabs, ContentType.Text.Html)
}

private suspend fun RoutingContext.getLazyTabsText() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)

        val content = TAB_CONTENTS[index]
        generator.patchElements(
            hfTabPanelView.render(content),
            PatchElementsOptions(selector = "#tabpanel", mode = ElementPatchMode.Replace),
        )
    }
}
