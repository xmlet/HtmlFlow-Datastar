package pt.isel.http4k

import jakarta.ws.rs.Path
import kotlinx.serialization.json.Json
import org.http4k.core.Method
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
import pt.isel.ktor.DEFAULT_USER
import pt.isel.ktor.DatastarClickToEdit
import pt.isel.ktor.Profile
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfClickToEdit
import pt.isel.views.htmlflow.hfDisplayFragment
import pt.isel.views.htmlflow.hfEditModeFragment

private val description = loadResource("pt/isel/views/fragments/click-to-edit-description.html")
private val html = loadResource("public/html/click-to-edit.html")

private val state = UiState(DEFAULT_USER)

fun demoClickToEdit() =
    poly(
        "/html" bind Method.GET to ::getClickToEditHtml,
        "/htmlflow" bind Method.GET to ::getClickToEditHf,
        "/edit" bindSse Method.GET to ::editProfile,
        "/reset" bindSse ::clickToEditReset,
        "/cancel" bindSse Method.GET to ::clickToEditCancel,
        "" bindSse Method.PUT to ::clickToEditSave,
        "/description" bindSse Method.GET to ::getClickToEditDescription,
    )

fun getClickToEditHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html")

fun getClickToEditHf(req: Request): Response = Response(OK).body(hfClickToEdit.render(state.profile)).header("Content-Type", "text/html")

@Path("/click-to-edit/edit")
fun editProfile(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(Element.of(hfEditModeFragment.render(state.profile))).close()
    }

@Path("/click-to-edit/reset")
fun clickToEditReset(req: Request): SseResponse =
    SseResponse { sse ->
        state.profile = DEFAULT_USER
        sse.sendPatchElements(Element.of(hfDisplayFragment.render(DEFAULT_USER))).close()
    }

@Path("/click-to-edit/cancel")
fun clickToEditCancel(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(Element.of(hfDisplayFragment.render(state.profile))).close()
    }

@Path("/click-to-edit")
fun clickToEditSave(req: Request): SseResponse =
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

@Path("/click-to-edit/description")
fun getClickToEditDescription(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(Element.of(description))
    }

data class UiState(
    var profile: Profile,
)
