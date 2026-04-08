package pt.isel.ktor

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
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.fragments.hfActiveSearchDescription
import pt.isel.views.htmlflow.hfActiveSearch
import pt.isel.views.htmlflow.hfActiveSearchContactsRowsFragment

private val html = loadResource("public/html/active-search.html")

fun Route.demoActiveSearch() {
    route("/active-search") {
        get("/html", RoutingContext::getActiveSearchHtml)
        get("/htmlflow", RoutingContext::getActiveSearchHtmlFlow)
        get("/search", RoutingContext::searchContacts)
        get("/description", RoutingContext::getActiveSearchDescription)
    }
}

private suspend fun RoutingContext.getActiveSearchHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getActiveSearchHtmlFlow() {
    call.respondText(hfActiveSearch.render(initialContacts), ContentType.Text.Html)
}

private suspend fun RoutingContext.searchContacts() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val datastarQueryArg = call.request.queryParameters["datastar"] ?: """{"search":""}"""

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
        generator.patchElements(hfActiveSearchContactsRowsFragment.render(filteredContacts))
    }
}

private suspend fun RoutingContext.getActiveSearchDescription() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        generator.patchElements(hfActiveSearchDescription)
    }
}

@Serializable
data class ActiveSearchSignals(
    val search: String,
)

data class Contact(
    val firstName: String,
    val lastName: String,
)

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
