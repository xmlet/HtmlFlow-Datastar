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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.htmlflow.hfClickToEdit
import pt.isel.views.htmlflow.hfDisplayFragment
import pt.isel.views.htmlflow.hfEditModeFragment

private val html = loadResource("public/html/click-to-edit.html")
val DEFAULT_USER =
    Profile(
        firstName = "John",
        lastName = "Doe",
        email = "joe@blow.com",
    )
private val globalUser = MutableStateFlow(DEFAULT_USER)

fun Route.demoClickToEdit() {
    route("/click-to-edit") {
        get("/html", RoutingContext::getClickToEditHtml)
        get("/htmlflow") { getClickToEditHtmlFlow(globalUser) }
        get("/edit") { editClickToEdit(globalUser) }
        patch("/reset") { resetClickToEdit(globalUser) }
        get("/cancel") { cancelClickToEdit(globalUser) }
        put("") { saveClickToEdit(globalUser) }
    }
}

private suspend fun RoutingContext.getClickToEditHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getClickToEditHtmlFlow(currentUser: MutableStateFlow<Profile>) {
    call.respondText(
        hfClickToEdit.render(currentUser.value),
        ContentType.Text.Html,
    )
}

private suspend fun RoutingContext.editClickToEdit(currentUser: MutableStateFlow<Profile>) {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        generator.patchElements(hfEditModeFragment.render(currentUser.value))
    }
}

private suspend fun RoutingContext.resetClickToEdit(currentUser: MutableStateFlow<Profile>) {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))

        currentUser.emit(DEFAULT_USER)

        generator.patchElements(hfDisplayFragment.render(DEFAULT_USER))
    }
}

private suspend fun RoutingContext.cancelClickToEdit(currentUser: MutableStateFlow<Profile>) {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))

        generator.patchElements(hfDisplayFragment.render(currentUser.value))
    }
}

private suspend fun RoutingContext.saveClickToEdit(currentUser: MutableStateFlow<Profile>) {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val body = call.receiveText()
        val signals = Json.decodeFromString<DatastarClickToEdit>(body)

        val newProfile =
            Profile(
                firstName = signals.firstName ?: currentUser.value.firstName,
                lastName = signals.lastName ?: currentUser.value.lastName,
                email = signals.email ?: currentUser.value.email,
            )

        currentUser.emit(newProfile)

        generator.patchElements(hfDisplayFragment.render(newProfile))
    }
}

@Serializable
data class DatastarClickToEdit(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
)

data class Profile(
    val firstName: String,
    val lastName: String,
    val email: String,
)
