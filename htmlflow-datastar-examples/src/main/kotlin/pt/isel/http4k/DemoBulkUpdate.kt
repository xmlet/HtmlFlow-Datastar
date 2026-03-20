package pt.isel.http4k

import jakarta.ws.rs.Path
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.datastar.Signal
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import org.http4k.sse.sendPatchSignals
import pt.isel.ktor.BulkUpdateSignals
import pt.isel.ktor.User
import pt.isel.ktor.UserStatus
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.hfBulkUpdate
import pt.isel.views.htmlflow.userRowsFragment

private val description = loadResource("pt/isel/views/fragments/bulk-update-description.html")
private val html = loadResource("public/html/bulk-update.html")

fun demoBulkUpdate() =
    poly(
        "/html" bind Method.GET to ::getBulkUpdateHtml,
        "/htmlflow" bind Method.GET to ::getBulkUpdateHtmlFlow,
        "/activate" bindSse Method.PUT to ::activateUsers,
        "/deactivate" bindSse Method.PUT to ::deactivateUsers,
        "/description" bindSse Method.GET to ::getBulkUpdateDescription,
    )

fun getBulkUpdateHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html;")

fun getBulkUpdateHtmlFlow(req: Request): Response =
    Response(OK).body(hfBulkUpdate.render(users)).header("Content-Type", "text/html; charset=utf-8")

@Path("/bulk-update/activate")
fun activateUsers(req: Request): SseResponse =
    SseResponse { sse ->
        val (selections) = Json.decodeFromString<BulkUpdateSignals>(req.bodyString())
        sse.sendPatchSignals(Signal.of("""{"selections": ${List(selections.size) { false }}}"""))
        selections.forEachIndexed { index, selected ->
            if (selected) users[index] = users[index].copy(status = UserStatus.ACTIVE)
        }
        sse.sendPatchElements(elements = listOf(Element.of(userRowsFragment(users))))
    }

@Path("/bulk-update/deactivate")
fun deactivateUsers(req: Request): SseResponse =
    SseResponse { sse ->
        val (selections) = Json.decodeFromString<BulkUpdateSignals>(req.bodyString())
        sse.sendPatchSignals(Signal.of("""{"selections": ${List(selections.size) { false }}}"""))
        selections.forEachIndexed { index, selected ->
            if (selected) users[index] = users[index].copy(status = UserStatus.INACTIVE)
        }
        sse.sendPatchElements(elements = listOf(Element.of(userRowsFragment(users))))
    }

@Path("/bulk-update/description")
fun getBulkUpdateDescription(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(
            elements = listOf(Element.of(description)),
        )
    }

private val users =
    mutableListOf(
        User("Joe Smith", "joe@smith.org", UserStatus.ACTIVE),
        User("Angie MacDowell", "angie@macdowell.org", UserStatus.ACTIVE),
        User("Fuqua Tarkenton", "fuqua@tarkenton.org", UserStatus.INACTIVE),
        User("Kim Yee", "kim@yee.org", UserStatus.INACTIVE),
    )
