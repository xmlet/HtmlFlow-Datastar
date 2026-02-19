@file:OptIn(ExperimentalSerializationApi::class)

package pt.isel.ktor

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.views.htmlflow.hfClickToEdit
import pt.isel.views.htmlflow.hfEditModeDoc

private val html = loadResource("public/html/click-to-edit.html")

fun Route.demoClickToEdit() {
    val currentUser = MutableStateFlow(DEFAULT_USER)
    route("/click-to-edit") {
        get("/html", RoutingContext::getClickToEditHtml)
        get("/htmlflow", RoutingContext::getClickToEditHtmlFlow)
        get("/edit") { editClickToEdit(currentUser) }
        patch("/reset") { resetClickToEdit(currentUser) }
        get("/cancel") { cancelClickToEdit(currentUser) }
        put("") { saveClickToEdit(currentUser) }
    }
}

private suspend fun RoutingContext.getClickToEditHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getClickToEditHtmlFlow() {
    call.respondText(
        hfClickToEdit.render(DEFAULT_USER),
        ContentType.Text.Html,
    )
}

private val DEFAULT_USER =
    Profile(
        firstName = "John",
        lastName = "Doe",
        email = "joe@blow.com",
    )

private suspend fun RoutingContext.editClickToEdit(currentUser: MutableStateFlow<Profile>) {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val datastarQueryArg = call.request.queryParameters["datastar"]
        requireNotNull(datastarQueryArg)

        // Decode the signals from the datastar query argument
        val (firstName, lastName, email) =
            try {
                Json.decodeFromString<ClickToEditSignals>(datastarQueryArg)
            } catch (_: MissingFieldException) {
                ClickToEditSignals(DEFAULT_USER.firstName, DEFAULT_USER.lastName, DEFAULT_USER.email)
            }

        // Send the patches to enter edit mode with current values
        generator.patchSignals(
            """ { firstName: "$firstName", lastName: "$lastName", email: "$email" } """,
        )
        if (currentUser.value != Profile(firstName, lastName, email)) {
            currentUser.emit(Profile(firstName, lastName, email))
        }
        generator.patchElements(hfEditModeDoc)
    }
}

private suspend fun RoutingContext.resetClickToEdit(currentUser: MutableStateFlow<Profile>) {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val defaultHtml = hfClickToEdit.render(DEFAULT_USER)
        currentUser.emit(DEFAULT_USER)
        generator.patchSignals(
            """ { firstName: "${DEFAULT_USER.firstName}", lastName: "${DEFAULT_USER.lastName}", email: "${DEFAULT_USER.email}" } """,
        )
        generator.patchElements(defaultHtml)
    }
}

private suspend fun RoutingContext.cancelClickToEdit(currentUser: MutableStateFlow<Profile>) {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))

        // Retrieve the last saved user details
        val (firstName, lastName, email) = currentUser.value
        generator.patchSignals(
            """ { firstName: "$firstName", lastName: "$lastName", email: "$email" } """,
        )

        generator.patchElements(hfClickToEdit.render(Profile(firstName, lastName, email)))
    }
}

private suspend fun RoutingContext.saveClickToEdit(currentUser: MutableStateFlow<Profile>) {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val datastarBodyArgs = call.request.call.receiveText()

        // Decode the signals from the request body
        val (firstName, lastName, email) = Json.decodeFromString<ClickToEditSignals>(datastarBodyArgs)
        val newProfile = Profile(firstName, lastName, email)

        currentUser.emit(newProfile)
        generator.patchElements(hfClickToEdit.render(newProfile))
    }
}

@Serializable
data class ClickToEditSignals(
    val firstName: String,
    val lastName: String,
    val email: String,
)

data class Profile(
    val firstName: String,
    val lastName: String,
    val email: String,
)
