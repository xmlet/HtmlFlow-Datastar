package pt.isel

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.config.JavalinConfig
import io.javalin.http.ContentType
import io.javalin.http.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.eclipse.jetty.http.HttpStatus

fun demoClickToLoad(config: JavalinConfig) {
    config.router.apiBuilder {
        path("/click-to-load") {
            get(::handlerGet)
        }
    }
}

private fun handlerGet(context: Context) {
    context
        .status(HttpStatus.OK_200)
        .contentType(ContentType.TEXT_HTML)
    object {}.javaClass.classLoader.getResource("click-to-load.html")?.openStream()?.let {
        context.result(it)
    }
}

private fun handlerGetEvents(
    context: Context,
    count: MutableStateFlow<Int>,
) {
    val response = response(context)
    val generator = ServerSentEventGenerator(response)
    runBlocking {
        count.collect { event ->
            generator.patchSignals("{count: ${count.value}}")
        }
    }
}
