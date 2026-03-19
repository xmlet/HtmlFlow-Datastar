package pt.isel.http4k

import jakarta.ws.rs.Path
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import pt.isel.ktor.InlineValidationSignals
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfInlineValidationView
import pt.isel.views.htmlflow.hfInputFieldsDiv
import pt.isel.views.htmlflow.hfSignUpDoc

private val html = loadResource("public/html/inline-validation.html")

fun demoInlineValidation(): PolyHandler =
    poly(
        "/html" bind Method.GET to ::getInlineValidationHtml,
        "/htmlflow" bind Method.GET to ::getInlineValidationHtmlFlow,
        "/validate" bindSse Method.POST to ::validateFields,
        "" bindSse Method.POST to ::submitForm,
    )

fun getInlineValidationHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html; charset=utf-8")

fun getInlineValidationHtmlFlow(req: Request): Response =
    Response(OK)
        .body(hfInlineValidationView.render(InlineValidationSignals()))
        .header("Content-Type", "text/html; charset=utf-8")

@Path("/inline-validation/validate")
fun validateFields(req: Request): SseResponse {
    val body = req.bodyString()
    val newSignals = Json.decodeFromString<InlineValidationSignals>(body)
    return SseResponse { sse ->
        sse.sendPatchElements(
            Element.of(hfInputFieldsDiv.render(newSignals)),
        )
    }
}

@Path("/inline-validation")
fun submitForm(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(Element.of(hfSignUpDoc))
    }
