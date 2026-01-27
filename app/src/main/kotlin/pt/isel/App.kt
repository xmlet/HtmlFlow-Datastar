package pt.isel

import dev.datastar.kotlin.sdk.Response
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.staticfiles.Location

fun main() {
    server().start(8080)
}

private fun server(): Javalin =
    Javalin
        .create { config ->
            config.staticFiles.add("/public", Location.CLASSPATH)
            demoCounter(config)
            demoCounterSignals(config)
            demoClickToLoad(config)
        }

/**
 * Creates a `Response` implementation that interacts with the Javalin `Context`.
 *
 * @param context The Javalin `Context` object representing the HTTP request and response.
 * @return A `Response` implementation for sending headers and writing data to the response.
 */
fun response(context: Context): Response =
    object : Response {
        private val outputStream = context.res().outputStream

        override fun sendConnectionHeaders(
            status: Int,
            headers: Map<String, List<String>>,
        ) {
            context.res().status = status
            for ((key, values) in headers) {
                context.res().setHeader(key, values.joinToString(","))
            }
        }

        override fun write(text: String) {
            outputStream.write(text.toByteArray())
        }

        override fun flush() {
            outputStream.flush()
        }
    }
