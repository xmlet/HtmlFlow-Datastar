package pt.isel

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.http4k.demosHttp4kRouting
import pt.isel.ktor.demosKtorRouting

private val logger = LoggerFactory.getLogger("MultiServerDemo")

fun main() {
    logger.info("Starting servers...")

    val ktorServer = embeddedServer(Netty, port = 8080) { demosKtorRouting() }.start(wait = false)

    logger.info("Ktor running on http://localhost:8080")

    val http4kServer = demosHttp4kRouting.asServer(Jetty(8000)).start()

    logger.info("http4k running on http://localhost:8000")

    Runtime.getRuntime().addShutdownHook(
        Thread {
            logger.info("Shutting down servers...")
            ktorServer.stop(1000, 2000)
            http4kServer.stop()
        },
    )
}
