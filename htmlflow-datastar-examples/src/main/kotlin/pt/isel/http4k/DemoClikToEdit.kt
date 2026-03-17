package pt.isel.http4k

import kotlinx.serialization.json.Json
import org.http4k.core.HttpHandler
import org.http4k.core.Method
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
import pt.isel.ktor.DEFAULT_USER
import pt.isel.ktor.DatastarClickToEdit
import pt.isel.ktor.Profile
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfClickToEdit
import pt.isel.views.htmlflow.hfDisplayFragment
import pt.isel.views.htmlflow.hfEditModeFragment

private val html = loadResource("public/html/click-to-edit.html")

private val state = UiState(DEFAULT_USER)

fun demoClickToEdit() =
    poly(
        "/html" bind Method.GET to getClickToEditHtml,
        "/htmlflow" bind Method.GET to getClickToEditHf,
        "/edit" bindSse Method.GET to edit,
        "/reset" bindSse reset,
        "/cancel" bindSse Method.GET to cancel,
        "" bindSse Method.PUT to save,
    )

val getClickToEditHtml: HttpHandler = { _ -> Response(OK).body(html).header("Content-Type", "text/html") }

val getClickToEditHf: HttpHandler = { _ ->
    Response(OK).body(hfClickToEdit.render(state.profile)).header("Content-Type", "text/html")
}

val edit: SseHandler = { _ ->
    SseResponse { sse ->
        sse.sendPatchElements(Element.of(hfEditModeFragment.render(state.profile))).close()
    }
}

val reset: SseHandler = { _ ->
    SseResponse { sse ->
        state.profile = DEFAULT_USER
        sse
            .sendPatchElements(
                Element.of(hfDisplayFragment.render(DEFAULT_USER)),
            ).close()
    }
}

val cancel: SseHandler = { _ ->
    SseResponse { sse ->
        sse.sendPatchElements(Element.of(hfDisplayFragment.render(state.profile))).close()
    }
}

val save: SseHandler = { req ->
    SseResponse { sse ->
        val body = req.bodyString()
        val signals = Json.decodeFromString<DatastarClickToEdit>(body)

        val currentUser = state.profile

        val newProfile =
            Profile(
                firstName = signals.firstName ?: currentUser.firstName,
                lastName = signals.lastName ?: currentUser.lastName,
                email = signals.email ?: currentUser.email,
            )
        state.profile = newProfile
        sse.sendPatchElements(Element.of(hfDisplayFragment.render(newProfile))).close()
    }
}

data class UiState(
    var profile: Profile,
)
