package pt.isel

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.views.htmlflow.hfActiveSearch

private val html = loadResource("public/html/active-search.html")

fun Route.demoActiveSearch() {
    route("/active-search") {
        get("/html", RoutingContext::getActiveSearchHtml)
        get("/htmlflow", RoutingContext::getActiveSearchHtmlFlow)
        get("/search", RoutingContext::searchContacts)
    }
}

private suspend fun RoutingContext.getActiveSearchHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getActiveSearchHtmlFlow() {
    call.respondText(hfActiveSearch, ContentType.Text.Html)
}

private suspend fun RoutingContext.searchContacts() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val datastarQueryArg = call.request.queryParameters["datastar"]
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
        val html = """<div id="demo"> <input type="text" placeholder="Search..." data-bind:search data-on:input__debounce.200ms="@get('/active-search/search')"><table><thead><tr><th>First Name</th><th>Last Name</th></tr></thead><tbody>${foundContactsRows(
            filteredContacts,
        )}</tbody></table></div>"""
        generator.patchElements(html)
    }
}

private const val ROW = """<tr><td>%s</td><td>%s</td></tr>"""

private fun foundContactsRows(contacts: List<Contact>): String =
    contacts
        .joinToString("\n") { cnt ->
            ROW.format(cnt.firstName, cnt.lastName)
        }

@Serializable
data class ActiveSearchSignals(
    val search: String,
)

data class Contact(
    val firstName: String,
    val lastName: String,
)

private val initialContacts =
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
