package pt.isel

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.util.logging.Logger
import org.http4k.core.PolyHandler
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.bind
import org.http4k.routing.poly
import org.http4k.routing.static
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.http4k.demoCounter
import pt.isel.http4k.demoCounterSignals
import pt.isel.ktor.demoActiveSearch
import pt.isel.ktor.demoBulkUpdate
import pt.isel.ktor.demoClickToEdit
import pt.isel.ktor.demoClickToEditViaSignals
import pt.isel.ktor.demoClickToLoad
import pt.isel.ktor.demoCounter
import pt.isel.ktor.demoCounterSignals
import pt.isel.ktor.demoDeleteRow
import pt.isel.ktor.demoEditRow
import pt.isel.ktor.demoFileUpload
import pt.isel.ktor.demoInfiniteScroll
import pt.isel.ktor.demoInlineValidation
import pt.isel.ktor.demoLazyLoad
import pt.isel.ktor.demoLazyTabs
import pt.isel.ktor.demoProgressBar
import pt.isel.ktor.demoProgressiveLoad
import pt.isel.ktor.demoTodoMvc

private val logger = LoggerFactory.getLogger("MultiServerDemo")

fun main() {
    logger.info("Starting servers...")

    val ktorServer =
        embeddedServer(Netty, port = 8080) {
            demoHtmlFlowDatastarRouting()
        }.start(wait = false)

    val http4kServer =
        poly(
            "/" bind static(Classpath("public")),
            *("/counter-signals" bind demoCounterSignals()).toTypedArray(),
            *("/counter" bind demoCounter).toTypedArray(),
        ).asServer(Jetty(8000)).start()

    logger.info("Ktor running on http://localhost:8080")
    logger.info("http4k running on http://localhost:8000")

    Runtime.getRuntime().addShutdownHook(
        Thread {
            logger.info("Shutting down servers...")
            ktorServer.stop(1000, 2000)
            http4kServer.stop()
        },
    )
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
