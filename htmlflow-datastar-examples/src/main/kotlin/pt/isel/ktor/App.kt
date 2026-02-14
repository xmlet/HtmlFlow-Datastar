package pt.isel.ktor

import dev.datastar.kotlin.sdk.Response
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import java.io.Writer
import java.nio.file.Path
import java.nio.file.Paths

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
        demoClickToEdit()
        demoFileUpload()
        demoInfiniteScroll()
        demoInlineValidation()
        demoDeleteRow()
        demoEditRow()
        demoLazyLoad()
        demoProgressiveLoad()
    }

/**
 * Loads a resource file from the classpath and returns its content as a String.
 *
 * @param path The path to the resource file relative to the classpath.
 * @return The content of the resource file as a String.
 * @throws IllegalArgumentException if the resource is not found.
 */
fun loadResource(path: String): String =
    object {}
        .javaClass.classLoader
        .getResource(path)
        ?.readText() ?: throw IllegalArgumentException("Resource not found:$path")

/**
 * Get a resource absolute from the classpath and returns its.
 *
 * @param path The path to the resource file relative to the classpath.
 * @return The url to the resource file as a String.
 */
fun getResourcePath(path: String): Path =
    object {}
        .javaClass.classLoader
        .getResource(path)
        ?.let { url ->
            Paths.get(url.toURI())
        } ?: throw IllegalArgumentException("Resource not found:$path")

/**
 * Creates a `Response` implementation that interacts with java.io.Writer.
 *
 * @return A `Response` implementation for sending headers and writing data to the response.
 */
fun response(writer: Writer): Response =
    object : Response {
        override fun sendConnectionHeaders(
            status: Int,
            headers: Map<String, List<String>>,
        ) {
            // connection is already set up when used
        }

        override fun write(text: String) {
            writer.write(text)
        }

        override fun flush() {
            writer.flush()
        }
    }
