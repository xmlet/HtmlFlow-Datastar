package pt.isel.http4k

import io.ktor.util.logging.Logger
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.bind
import org.http4k.routing.poly
import org.http4k.routing.static
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory

fun main() {
    val logger: Logger = LoggerFactory.getLogger("Http4kApp")

    val app =
        poly(
            "/" bind static(Classpath("public")),
            *("/counter-signals" bind demoCounterSignals()).toTypedArray(),
            *("/counter" bind demoCounter).toTypedArray(),
        )
    val server = app.asServer(Jetty(8000)).start()
    logger.info("Server started, listening on http://localhost:8000")
    server.block()
}
