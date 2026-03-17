package pt.isel.http4k

import kotlinx.serialization.json.Json
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseHandler
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
        "/html" bind Method.GET to getInlineValidationHtml,
        "/htmlflow" bind Method.GET to getEditRowHtmlFlow,
        "/validate" bindSse Method.POST to validateFields,
        "" bindSse Method.POST to signUp,
    )

private val getInlineValidationHtml: HttpHandler = { _ ->
    Response(OK).body(html).header("Content-Type", "text/html; charset=utf-8")
}

private val getEditRowHtmlFlow: HttpHandler = { _ ->
    Response(OK)
        .body(hfInlineValidationView.render(InlineValidationSignals()))
        .header("Content-Type", "text/html; charset=utf-8")
}

private val validateFields: SseHandler = { req ->
    val body = req.bodyString()
    val newSignals = Json.decodeFromString<InlineValidationSignals>(body)
    SseResponse { sse ->
        sse.sendPatchElements(
            Element.of(hfInputFieldsDiv.render(newSignals)),
        )
    }
}
private val signUp: SseHandler = { _ ->
    SseResponse { sse ->
        sse.sendPatchElements(Element.of(hfSignUpDoc))
    }
}
