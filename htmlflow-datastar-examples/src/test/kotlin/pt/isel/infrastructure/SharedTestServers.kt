package pt.isel.infrastructure

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import org.http4k.server.Jetty
import org.http4k.server.ServerConfig
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.http4k.demosHttp4kRouting
import pt.isel.ktor.demosKtorRouting

/**
 * Singleton that manages shared test servers for all integration tests.
 * Servers are launched lazily on first access and reused across all test classes.
 */
object SharedTestServers {
    private val logger = LoggerFactory.getLogger("SharedTestServers")

    private val serverKtor by lazy {
        embeddedServer(Netty, port = 0) {
            routing {
                demosKtorRouting()
            }
        }.start().also { logger.info("Started Ktor") }
    }

    private val serverHttp4k by lazy {
        demosHttp4kRouting
            .asServer(
                Jetty(
                    0,
                    ServerConfig.StopMode.Immediate,
                ),
            ).start()
            .also { logger.info("Started Http4k") }
    }

    val http4kPort: Int by lazy { serverHttp4k.port() }
    val ktorPort: Int by lazy {
        runBlocking {
            serverKtor.engine
                .resolvedConnectors()
                .first()
                .port
        }
    }

    init {
        Runtime.getRuntime().addShutdownHook(
            Thread {
                runBlocking {
                    serverHttp4k.stop()
                    serverKtor.stop(1000, 2000)
                }
            },
        )
    }

    fun getPort(serverType: String): Int = if (serverType == "Ktor") ktorPort else http4kPort
}
