package pt.isel.ktor

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.htmlflow.hfClickToEditSignals

private val description = loadResource("public/html/fragments/click-to-edit-signals-description.html")
private val html =
    loadResource("public/html/click-to-edit-signals.html")
        .replace("<!-- DESCRIPTION -->", description)

private val clickToEditSignals = MutableStateFlow(ClickToEditSignals())

fun Route.demoClickToEditViaSignals() {
    route("/click-to-edit-signals") {
        get("/html", RoutingContext::getClickToEditSignalsHtml)
        get("/htmlflow", RoutingContext::getClickToEditSignalsHtmlFlow)

        get("/events", RoutingContext::getClickToEditEvents)
        patch("/reset", RoutingContext::resetClickToEdit)
        get("/cancel", RoutingContext::cancelClickToEdit)
        put("", RoutingContext::saveClickToEdit)
    }
}

private suspend fun RoutingContext.getClickToEditSignalsHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getClickToEditSignalsHtmlFlow() {
    call.respondText(hfClickToEditSignals, ContentType.Text.Html)
}

private suspend fun RoutingContext.getClickToEditEvents() {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val currentSignals = clickToEditSignals.value
        generator.patchSignals(
            " { firstName: '${currentSignals.firstName}', lastName: '${currentSignals.lastName}' , email: '${currentSignals.email}' }",
        )
        clickToEditSignals.collect { newSignals ->
            generator.patchSignals(
                " { firstName: '${newSignals.firstName}', lastName: '${newSignals.lastName}' , email: '${newSignals.email}' }",
            )
        }
    }
}

private suspend fun RoutingContext.resetClickToEdit() {
    clickToEditSignals.emit(ClickToEditSignals())
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.cancelClickToEdit() {
    clickToEditSignals.emit(
        ClickToEditSignals(
            firstName = clickToEditSignals.value.firstName,
            lastName = clickToEditSignals.value.lastName,
            email = clickToEditSignals.value.email,
        ),
    )
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.saveClickToEdit() {
    val datastarBodyArgs = call.request.call.receiveText()

    // Decode the signals from the request body
    val updatedSignals = Json.decodeFromString<ClickToEditSignals>(datastarBodyArgs)

    clickToEditSignals.emit(updatedSignals)

    call.respond(HttpStatusCode.NoContent)
}

private const val DEFAULT_USER_NAME = "John"
private const val DEFAULT_USER_LAST_NAME = "Doe"
private const val DEFAULT_USER_EMAIL = "joe@blow.com"

// ClickToEditSignals must be a class (not a data class) because MutableStateFlow only emits
// distinct values. When a data class with the same property values is emitted, the StateFlow
// won't emit it as it's considered a duplicate. This demo needs to emit the same signal values
// again when the user cancels an edit, which requires using a regular class so the object
// reference changes even when the properties remain the same.
@Serializable
class ClickToEditSignals(
    val firstName: String = DEFAULT_USER_NAME,
    val lastName: String = DEFAULT_USER_LAST_NAME,
    val email: String = DEFAULT_USER_EMAIL,
)
