package pt.isel.ktor

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.htmlflow.hfInlineValidationView
import pt.isel.views.htmlflow.hfInputFieldsDiv
import pt.isel.views.htmlflow.hfSignUpDoc

private val description = loadResource("pt/isel/views/fragments/inline-validation-description.html")
private val html = loadResource("public/html/inline-validation.html")

fun Route.demoInlineValidation() {
    route("/inline-validation") {
        get("/html", RoutingContext::getInlineValidationHtml)
        get("/htmlflow", RoutingContext::getInlineValidationHtmlFlow)
        post("/validate", RoutingContext::validateFields)
        post("", RoutingContext::signUp)
        get("/description", RoutingContext::getInlineValidationDescription)
    }
}

private suspend fun RoutingContext.getInlineValidationHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getInlineValidationHtmlFlow() {
    call.respondText(hfInlineValidationView.render(InlineValidationSignals()), ContentType.Text.Html)
}

private suspend fun RoutingContext.validateFields() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val datastarBodyArgs = call.request.call.receiveText()

        // Decode the signals from the request body
        val newSignals = Json.decodeFromString<InlineValidationSignals>(datastarBodyArgs)

        generator.patchElements(hfInputFieldsDiv.render(newSignals))
    }
}

private suspend fun RoutingContext.signUp() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))

        generator.patchElements(hfSignUpDoc)
    }
}

private suspend fun RoutingContext.getInlineValidationDescription() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        generator.patchElements(description)
    }
}

@Serializable
data class InlineValidationSignals(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
)
