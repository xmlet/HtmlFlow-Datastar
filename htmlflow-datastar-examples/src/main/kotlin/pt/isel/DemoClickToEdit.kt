@file:OptIn(ExperimentalSerializationApi::class)

package pt.isel

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
    call.respondText(hfClickToEdit, ContentType.Text.Html)
}

private val DEFAULT_USER =
    ClickToEditSignals(
        firstName = "John",
        lastName = "Doe",
        email = "joe@blow.com",
    )

private suspend fun RoutingContext.editClickToEdit(currentUser: MutableStateFlow<ClickToEditSignals>) {
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
                DEFAULT_USER
            }

        // Send the patches to enter edit mode with current values
        generator.patchSignals(
            """ { firstName: "$firstName", lastName: "$lastName", email: "$email" } """,
        )
        if (currentUser.value.firstName != firstName && currentUser.value.lastName != lastName && currentUser.value.email != email) {
            currentUser.emit(ClickToEditSignals(firstName, lastName, email))
        }

        val htmlEditMode =
            $$"""
            <div id="demo">
                    <label>First Name 
                        <input type="text" data-bind:first-name data-attr:disabled="$_fetching">
                    </label> 
                    <label>Last Name 
                        <input type="text" data-bind:last-name data-attr:disabled="$_fetching">
                    </label> 
                    <label>Email 
                        <input type="email" data-bind:email data-attr:disabled="$_fetching">
                    </label>
                    <div role="group">
                        <button class="success" data-indicator:_fetching data-attr:disabled="$_fetching" data-on:click="@put('/click-to-edit')">Save</button> 
                        <button class="error" data-indicator:_fetching data-attr:disabled="$_fetching" data-on:click="@get('/click-to-edit/cancel')">Cancel</button>
                    </div>
                </div>
            """.trimIndent().replace("\n", "")
        generator.patchElements(htmlEditMode)
    }
}

private suspend fun RoutingContext.resetClickToEdit(currentUser: MutableStateFlow<ClickToEditSignals>) {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val defaultHtml =
            defaultClickToEditHtml(
                DEFAULT_USER.firstName,
                DEFAULT_USER.lastName,
                DEFAULT_USER.email,
            )
        currentUser.emit(DEFAULT_USER)
        generator.patchSignals(
            """ { firstName: "${DEFAULT_USER.firstName}", lastName: "${DEFAULT_USER.lastName}", email: "${DEFAULT_USER.email}" } """,
        )
        generator.patchElements(defaultHtml)
    }
}

private suspend fun RoutingContext.cancelClickToEdit(currentUser: MutableStateFlow<ClickToEditSignals>) {
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

        val htmlViewMode = defaultClickToEditHtml(firstName, lastName, email)
        generator.patchElements(htmlViewMode)
    }
}

private suspend fun RoutingContext.saveClickToEdit(currentUser: MutableStateFlow<ClickToEditSignals>) {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val datastarBodyArgs = call.request.call.receiveText()

        // Decode the signals from the request body
        val (firstName, lastName, email) = Json.decodeFromString<ClickToEditSignals>(datastarBodyArgs)
        val htmlViewMode = defaultClickToEditHtml(firstName, lastName, email)
        currentUser.emit(currentUser.value.copy(firstName = firstName, lastName = lastName, email = email))
        generator.patchElements(htmlViewMode)
    }
}

@Serializable
data class ClickToEditSignals(
    val firstName: String,
    val lastName: String,
    val email: String,
)

private fun defaultClickToEditHtml(
    firstName: String,
    lastName: String,
    email: String,
) = $$"""
    <div id="demo">
        <p>First Name: $$firstName</p>
        <p>Last Name: $$lastName</p>
        <p>Email: $$email</p>
        <div>
            <button class="info" data
                data-indicator:_fetching=""
                data-attr:disabled="$_fetching"
                data-on:click="@get('/click-to-edit/edit')">
                Edit
            </button>
            <button class="warning"
                data-indicator:_fetching=""
                data-attr:disabled="$_fetching"
                data-on:click="@patch('/click-to-edit/reset')">
                Reset
            </button>
        </div>
    </div>
    """.trimIndent().replace("\n", "")
