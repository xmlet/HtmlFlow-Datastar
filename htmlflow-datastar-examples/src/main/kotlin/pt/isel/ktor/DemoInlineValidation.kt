package pt.isel.ktor

import dev.datastar.kotlin.sdk.ElementPatchMode
import dev.datastar.kotlin.sdk.PatchElementsOptions
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
import pt.isel.views.fragments.hfInlineValidationDescription
import pt.isel.views.htmlflow.hfEmailErrorFragment
import pt.isel.views.htmlflow.hfFirstNameErrorFragment
import pt.isel.views.htmlflow.hfInlineValidation
import pt.isel.views.htmlflow.hfLastNameErrorFragment
import pt.isel.views.htmlflow.hfSignUpDoc

private val html = loadResource("public/html/inline-validation.html")

fun Route.demoInlineValidation() {
    route("/inline-validation") {
        get("/html", RoutingContext::getInlineValidationHtml)
        get("/htmlflow", RoutingContext::getInlineValidationHtmlFlow)
        get("/description", RoutingContext::getInlineValidationDescription)
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
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val datastarBodyArgs = call.request.call.receiveText()

        // Decode the signals from the request body
        val signals = Json.decodeFromString<InlineValidationSignals>(datastarBodyArgs)

        val validationResult = validateSignals(signals)

        clearSelectiveErrors(generator, signals, validationResult)

        if (validationResult.hasErrors && !allFieldsAreBlank(signals)) {
            generator.patchSignals(" { _error: true }")
            patchValidationErrors(generator, validationResult, signals)
        } else {
            if (!allFieldsAreBlank(signals)) {
                generator.patchSignals(" { _error: false }")
            }
        }
    }
}

fun validateSignals(signals: InlineValidationSignals): ValidationResult {
    val isEmailValid = signals.email == "test@test.com"
    val isFirstNameValid = signals.firstName.length >= 2
    val isLastNameValid = signals.lastName.length >= 2

    return ValidationResult(
        isEmailValid = isEmailValid,
        isFirstNameValid = isFirstNameValid,
        isLastNameValid = isLastNameValid,
    )
}

private fun shouldClearFieldError(
    fieldValue: String,
    isFieldValid: Boolean,
): Boolean = fieldValue.isBlank() || isFieldValid

private fun clearSelectiveErrors(
    generator: ServerSentEventGenerator,
    signals: InlineValidationSignals,
    validationResult: ValidationResult,
) {
    if (shouldClearFieldError(signals.email, validationResult.isEmailValid)) {
        generator.patchElements(
            options = PatchElementsOptions("#email-error-details", ElementPatchMode.Remove),
        )
    }

    if (shouldClearFieldError(signals.firstName, validationResult.isFirstNameValid)) {
        generator.patchElements(
            options = PatchElementsOptions("#first-name-error-details", ElementPatchMode.Remove),
        )
    }

    if (shouldClearFieldError(signals.lastName, validationResult.isLastNameValid)) {
        generator.patchElements(
            options = PatchElementsOptions("#last-name-error-details", ElementPatchMode.Remove),
        )
    }
}

private fun allFieldsAreBlank(signals: InlineValidationSignals): Boolean =
    signals.email.isBlank() && signals.firstName.isBlank() && signals.lastName.isBlank()

private fun patchValidationErrors(
    generator: ServerSentEventGenerator,
    result: ValidationResult,
    signals: InlineValidationSignals,
) {
    if (!result.isEmailValid && signals.email.isNotBlank()) {
        generator.patchElements(
            hfEmailErrorFragment.render(signals.email),
            PatchElementsOptions(
                selector = "#email-error",
                mode = ElementPatchMode.Replace,
            ),
        )
    }
    if (!result.isFirstNameValid && signals.firstName.isNotBlank()) {
        generator.patchElements(
            hfFirstNameErrorFragment,
            PatchElementsOptions(
                selector = "#first-name-error",
                mode = ElementPatchMode.Replace,
            ),
        )
    }
    if (!result.isLastNameValid && signals.lastName.isNotBlank()) {
        generator.patchElements(
            hfLastNameErrorFragment,
            PatchElementsOptions(
                selector = "#last-name-error",
                mode = ElementPatchMode.Replace,
            ),
        )
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
        generator.patchElements(hfInlineValidationDescription)
    }
}

@Serializable
data class InlineValidationSignals(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
)

data class ValidationResult(
    val isEmailValid: Boolean,
    val isFirstNameValid: Boolean,
    val isLastNameValid: Boolean,
) {
    val hasErrors: Boolean get() = !isEmailValid || !isFirstNameValid || !isLastNameValid
}
