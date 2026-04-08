package pt.isel.http4k

import jakarta.ws.rs.Path
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.datastar.MorphMode
import org.http4k.datastar.Selector
import org.http4k.datastar.Signal
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.Sse
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import org.http4k.sse.sendPatchSignals
import pt.isel.ktor.InlineValidationSignals
import pt.isel.ktor.ValidationResult
import pt.isel.ktor.validateSignals
import pt.isel.utils.loadResource
import pt.isel.views.fragments.hfInlineValidationDescription
import pt.isel.views.htmlflow.hfEmailErrorFragment
import pt.isel.views.htmlflow.hfFirstNameErrorFragment
import pt.isel.views.htmlflow.hfInlineValidation
import pt.isel.views.htmlflow.hfLastNameErrorFragment
import pt.isel.views.htmlflow.hfSignUpDoc

private val html = loadResource("public/html/inline-validation.html")

fun demoInlineValidation(): PolyHandler =
    poly(
        "/html" bind Method.GET to ::getInlineValidationHtml,
        "/htmlflow" bind Method.GET to ::getInlineValidationHtmlFlow,
        "/validate" bindSse Method.POST to ::validateFields,
        "/description" bind Method.GET to ::getInlineValidationDescription,
        "" bindSse Method.POST to ::submitForm,
    )

fun getInlineValidationHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html; charset=utf-8")

fun getInlineValidationHtmlFlow(req: Request): Response =
    Response(OK)
        .body(hfInlineValidation)
        .header("Content-Type", "text/html; charset=utf-8")

@Path("/inline-validation/validate")
fun validateFields(req: Request): SseResponse {
    val body = req.bodyString()
    val signals = Json.decodeFromString<InlineValidationSignals>(body)

    val validationResult = validateSignals(signals)

    return SseResponse { sse ->
        clearSelectiveErrors(sse, signals, validationResult)

        if (validationResult.hasErrors && !allFieldsAreBlank(signals)) {
            sse.sendPatchSignals(Signal.of(" { _error: true }"))
            patchValidationErrors(sse, validationResult, signals)
        } else {
            if (!allFieldsAreBlank(signals)) {
                sse.sendPatchSignals(Signal.of(" { _error: false }"))
            }
        }
    }
}

@Path("/inline-validation")
fun submitForm(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(Element.of(hfSignUpDoc))
    }

@Path("/inline-validation/description")
fun getInlineValidationDescription(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(Element.of(hfInlineValidationDescription))
    }

private fun patchValidationErrors(
    sse: Sse,
    result: ValidationResult,
    signals: InlineValidationSignals,
) {
    if (!result.isEmailValid) {
        sse.sendPatchElements(
            Element.of(hfEmailErrorFragment.render(signals.email)),
            selector = Selector.of("#email-error"),
            morphMode = MorphMode.replace,
        )
    }
    if (!result.isFirstNameValid) {
        sse.sendPatchElements(
            Element.of(hfFirstNameErrorFragment),
            selector = Selector.of("#first-name-error"),
            morphMode = MorphMode.replace,
        )
    }
    if (!result.isLastNameValid) {
        sse.sendPatchElements(
            Element.of(hfLastNameErrorFragment),
            selector = Selector.of("#last-name-error"),
            morphMode = MorphMode.replace,
        )
    }
}

private fun shouldClearFieldError(
    fieldValue: String,
    isFieldValid: Boolean,
): Boolean = fieldValue.isBlank() || isFieldValid

private fun clearSelectiveErrors(
    sse: Sse,
    signals: InlineValidationSignals,
    validationResult: ValidationResult,
) {
    if (shouldClearFieldError(signals.email, validationResult.isEmailValid)) {
        sse.sendPatchElements(
            selector = Selector.of("#email-error-details"),
            morphMode = MorphMode.remove,
        )
    }

    if (shouldClearFieldError(signals.firstName, validationResult.isFirstNameValid)) {
        sse.sendPatchElements(
            selector = Selector.of("#first-name-error-details"),
            morphMode = MorphMode.remove,
        )
    }

    if (shouldClearFieldError(signals.lastName, validationResult.isLastNameValid)) {
        sse.sendPatchElements(
            selector = Selector.of("#last-name-error-details"),
            morphMode = MorphMode.remove,
        )
    }
}

private fun allFieldsAreBlank(signals: InlineValidationSignals): Boolean =
    signals.email.isBlank() && signals.firstName.isBlank() && signals.lastName.isBlank()
