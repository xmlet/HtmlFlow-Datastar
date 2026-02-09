package pt.isel

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import htmlflow.div
import htmlflow.doc
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
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
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.label
import org.xmlet.htmlapifaster.p
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataOn
import pt.isel.views.htmlflow.hfInlineValidation
import kotlin.time.Duration.Companion.milliseconds

private val html = loadResource("public/html/inline-validation.html")

fun Route.demoInlineValidation() {
    route("/inline-validation") {
        get("/html", RoutingContext::getInlineValidationHtml)
        get("/htmlflow", RoutingContext::getInlineValidationHtmlFlow)
        post("/validate", RoutingContext::validateFields)
        post("", RoutingContext::signUp)
    }
}

private suspend fun RoutingContext.getInlineValidationHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getInlineValidationHtmlFlow() {
    call.respondText(hfInlineValidation, ContentType.Text.Html)
}

private suspend fun RoutingContext.validateFields() {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val datastarBodyArgs = call.request.call.receiveText()

        // Decode the signals from the request body
        val (email, firstName, lastName) = Json.decodeFromString<InlineValidationSignals>(datastarBodyArgs)

        val isEmailValid = email == "test@test.com"
        val isFirstNameValid = firstName.length >= 2
        val isLastNameValid = lastName.length >= 2
        val canSubmit = isEmailValid && isFirstNameValid && isLastNameValid

        val html =
            StringBuilder()
                .apply {
                    doc {
                        div {
                            attrId("demo")
                            label {
                                text("Email Address")
                                input {
                                    attrType(EnumTypeInputType.EMAIL)
                                    attrRequired(true)
                                    addAttr("aria-live", "polite")
                                    addAttr("aria-describedby", "email-info")
                                    dataBind("email")
                                    dataOn("keydown", "@post('/inline-validation/validate')") {
                                        debounce(500.milliseconds)
                                    }
                                }
                            }
                            if (!isEmailValid && email.isNotBlank()) {
                                p {
                                    attrId("email-error")
                                    attrClass("error")
                                    text("Email $email is already taken or invalid. Please enter another email.")
                                }
                            }
                            p {
                                attrId("email-info")
                                attrClass("info")
                                raw("The only valid email address is \"test@test.com\".")
                            }
                            label {
                                text("First Name")
                                input {
                                    attrType(EnumTypeInputType.TEXT)
                                    attrRequired(true)
                                    addAttr("aria-live", "polite")
                                    dataBind("first-name")
                                    dataOn("keydown", "@post('/inline-validation/validate')") {
                                        debounce(500.milliseconds)
                                    }
                                }
                            }
                            if (firstName.isNotBlank() && !isFirstNameValid) {
                                p {
                                    attrId("first-name-error")
                                    attrClass("error")
                                    text("First name must be at least 2 characters.")
                                }
                            }
                            label {
                                text("Last Name")
                                input {
                                    attrType(EnumTypeInputType.TEXT)
                                    attrRequired(true)
                                    addAttr("aria-live", "polite")
                                    dataBind("last-name")
                                    dataOn("keydown", "@post('/inline-validation/validate')") {
                                        debounce(500.milliseconds)
                                    }
                                }
                            }
                            if (lastName.isNotBlank() && !isLastNameValid) {
                                p {
                                    attrId("last-name-error")
                                    attrClass("error")
                                    text("Last name must be at least 2 characters.")
                                }
                            }
                            button {
                                attrClass("success")
                                dataOn("click", "@post('/inline-validation')")
                                if (!canSubmit) addAttr("aria-disabled", "true")
                                text(" Sign Up")
                            }
                        }
                    }
                }.toString()
                .trimIndent()
                .replace("\n", "")

        generator.patchElements(html)
    }
}

private suspend fun RoutingContext.signUp() {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))

        val html =
            """
            <div id="demo">
            	Thank you for signing up!
            </div>
            """.trimIndent().replace("\n", "")

        generator.patchElements(html)
    }
}

@Serializable
data class InlineValidationSignals(
    val email: String,
    val firstName: String,
    val lastName: String,
)
