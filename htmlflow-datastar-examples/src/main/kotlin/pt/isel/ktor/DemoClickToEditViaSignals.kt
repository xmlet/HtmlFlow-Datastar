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

private val html = loadResource("public/html/click-to-edit-signals.html")

fun Route.demoClickToEditViaSignals() {
    val clickToEditSignals = MutableStateFlow(ClickToEditSignals())
    route("/click-to-edit-signals") {
        get("/html", RoutingContext::getClickToEditSignalsHtml)
        get("/htmlflow", RoutingContext::getClickToEditSignalsHtmlFlow)

        get("/events") { getClickToEditEvents(clickToEditSignals) }

        get("/edit") { editClickToEdit(clickToEditSignals) }
        patch("/reset") { resetClickToEdit(clickToEditSignals) }
        get("/cancel") { cancelClickToEdit(clickToEditSignals) }
        put("") { saveClickToEdit(clickToEditSignals) }
    }
}

private suspend fun RoutingContext.getClickToEditSignalsHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getClickToEditSignalsHtmlFlow() {
    call.respondText(hfClickToEditSignals, ContentType.Text.Html)
}

private suspend fun RoutingContext.getClickToEditEvents(signals: MutableStateFlow<ClickToEditSignals>) {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        signals.collect { newSignals ->
            generator.patchSignals(
                " { 'firstName': '${newSignals.firstName}', 'lastName': '${newSignals.lastName}' , 'email': '${newSignals.email}', 'editMode': ${newSignals.editMode} }",
            )
        }
    }
}

private suspend fun RoutingContext.editClickToEdit(signals: MutableStateFlow<ClickToEditSignals>) {
    val datastarQueryArg = call.request.queryParameters["datastar"]
    requireNotNull(datastarQueryArg)

    // Decode the signals from the datastar query argument
    val (firstName, lastName, email) = Json.decodeFromString<ClickToEditSignals>(datastarQueryArg)

    signals.emit(signals.value.copy(firstName = firstName, lastName = lastName, email = email, editMode = true))
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.resetClickToEdit(clickToEditSignals: MutableStateFlow<ClickToEditSignals>) {
    clickToEditSignals.emit(
        clickToEditSignals.value.copy(firstName = DEFAULT_USER_NAME, lastName = DEFAULT_USER_LAST_NAME, email = DEFAULT_USER_EMAIL),
    )
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.cancelClickToEdit(clickToEditSignals: MutableStateFlow<ClickToEditSignals>) {
    clickToEditSignals.emit(clickToEditSignals.value.copy(editMode = false))
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.saveClickToEdit(clickToEditSignals: MutableStateFlow<ClickToEditSignals>) {
    val datastarBodyArgs = call.request.call.receiveText()

    // Decode the signals from the request body
    val (firstName, lastName, email) = Json.decodeFromString<ClickToEditSignals>(datastarBodyArgs)

    clickToEditSignals.emit(clickToEditSignals.value.copy(firstName = firstName, lastName = lastName, email = email, editMode = false))

    call.respond(HttpStatusCode.NoContent)
}

private const val DEFAULT_USER_NAME = "John"
private const val DEFAULT_USER_LAST_NAME = "Doe"
private const val DEFAULT_USER_EMAIL = "joe@blow.com"

@Serializable
data class ClickToEditSignals(
    val firstName: String = DEFAULT_USER_NAME,
    val lastName: String = DEFAULT_USER_LAST_NAME,
    val email: String = DEFAULT_USER_EMAIL,
    val editMode: Boolean = false,
)
