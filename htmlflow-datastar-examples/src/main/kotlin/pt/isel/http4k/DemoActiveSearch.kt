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
import pt.isel.ktor.ActiveSearchSignals
import pt.isel.ktor.Contact
import pt.isel.utils.loadResource
import pt.isel.views.fragments.hfActiveSearchDescription
import pt.isel.views.htmlflow.contactRowsFragment
import pt.isel.views.htmlflow.hfActiveSearch

private val html = loadResource("public/html/active-search.html")

fun demoActiveSearch() =
    poly(
        "/html" bind Method.GET to ::getActiveSearchHtml,
        "/htmlflow" bind Method.GET to ::getActiveSearchHtmlFlow,
        "/search" bindSse Method.GET to ::getSearchContacts,
        "/description" bind Method.GET to ::getActiveSearchDescription,
    )

fun getActiveSearchHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html;")

fun getActiveSearchHtmlFlow(req: Request): Response =
    Response(OK).body(hfActiveSearch.render(initialContacts)).header("Content-Type", "text/html; charset=utf-8")

@Path("/active-search/search")
fun getSearchContacts(req: Request): SseResponse {
    val datastarQueryArg = req.query("datastar")
    requireNotNull(datastarQueryArg)

    val (search) = Json.decodeFromString<ActiveSearchSignals>(datastarQueryArg)

    val filteredContacts =
        if (search.isBlank()) {
            initialContacts
        } else {
            initialContacts.filter {
                it.firstName.contains(search, ignoreCase = true) ||
                    it.lastName.contains(search, ignoreCase = true)
            }
        }
    return SseResponse { sse ->
        sse.sendPatchElements(elements = listOf(Element.of(contactRowsFragment(filteredContacts))))
    }
}

@Path("/active-search/description")
fun getActiveSearchDescription(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(
            elements = listOf(Element.of(hfActiveSearchDescription)),
        )
    }

val initialContacts =
    listOf(
        Contact("Abraham", "Altenwerth"),
        Contact("Adan", "Padberg"),
        Contact("Aiden", "Haley"),
        Contact("Alec", "Kris"),
        Contact("Alfredo", "Nitzsche"),
        Contact("Alisha", "Rogahn"),
        Contact("Alvah", "Bins"),
        Contact("Anabel", "Lehner"),
        Contact("Angela", "Swift"),
        Contact("Annamarie", "Rippin"),
    )
