package pt.isel

import io.javalin.Javalin
import io.javalin.http.staticfiles.Location

fun main() {
    server().start(8080)
}

private fun server(): Javalin =
    Javalin
        .create { config ->
            config.staticFiles.add("/", Location.CLASSPATH)
            demoCounter(config)
        }
