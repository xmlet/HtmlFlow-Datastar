package pt.isel.ktor

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080) {
        demoHtmlFlowDatastarRouting()
    }.start(wait = true)
}

fun Application.demoHtmlFlowDatastarRouting() =
    routing {
        staticResources("/", "public")
        demoCounter()
        demoCounterSignals()
        demoClickToLoad()
        demoActiveSearch()
        demoBulkUpdate()
        demoClickToEditViaSignals()
        demoFileUpload()
        demoInfiniteScroll()
        demoInlineValidation()
        demoDeleteRow()
        demoEditRow()
        demoLazyLoad()
        demoLazyTabs()
        demoProgressiveLoad()
        demoTodoMvc()
        demoProgressBar()
        demoClickToEdit()
    }
