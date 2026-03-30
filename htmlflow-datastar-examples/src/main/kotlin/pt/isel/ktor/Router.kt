package pt.isel.ktor

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.routing

fun Application.demosKtorRouting() =
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
