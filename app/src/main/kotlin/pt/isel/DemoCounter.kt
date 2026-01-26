package pt.isel

import dev.datastar.kotlin.sdk.Response
import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.config.JavalinConfig
import io.javalin.http.ContentType
import io.javalin.http.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.eclipse.jetty.http.HttpStatus

fun demoCounter(config: JavalinConfig) {
    val counter: MutableStateFlow<Int> = MutableStateFlow(0)

    config.router.apiBuilder {
        path("/counter") {
            get(::handlerGet)
            get("/events") { handlerGetEvents(it, counter) }
            post("/increment") { context ->
                counter.value++
                context.status(HttpStatus.NO_CONTENT_204)
            }
            post("/decrement") {
                counter.value--
                it.status(HttpStatus.NO_CONTENT_204)
            }
        }
    }
}

private fun handlerGet(context: Context) {
    context
        .status(HttpStatus.OK_200)
        .contentType(ContentType.TEXT_HTML)
    object {}.javaClass.classLoader.getResource("counter.html")?.openStream()?.let {
        context.result(it)
    }
}

private fun handlerGetEvents(
    context: Context,
    counter: MutableStateFlow<Int>,
) {
    val response = response(context)
    val generator = ServerSentEventGenerator(response)
    runBlocking {
        counter.collect { event ->
            generator.patchElements(
                """<span id="counter">$event</span>""",
            )
            if (event == 3) {
                generator.executeScript("""alert('Thanks for trying Datastar!')""")
            }
        }
    }
}