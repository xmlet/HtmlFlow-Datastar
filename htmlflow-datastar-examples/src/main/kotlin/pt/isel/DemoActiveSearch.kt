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
        get("/search") { searchUsers() }
    }
}

private suspend fun RoutingContext.getActiveSearchHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getActiveSearchHtmlFlow() {
    call.respondText(hfActiveSearch, ContentType.Text.Html)
}

private suspend fun RoutingContext.searchUsers() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val datastarQueryArg = call.request.queryParameters["datastar"]
        requireNotNull(datastarQueryArg)

        val (search) = Json.decodeFromString<ActiveSearchSignals>(datastarQueryArg)

        val filteredUsers =
            if (search.isBlank()) {
                initialUsers
            } else {
                initialUsers.filter {
                    it.firstName.contains(search, ignoreCase = true) ||
                        it.lastName.contains(search, ignoreCase = true)
                }
            }
        val html = """<div id="demo"> <input type="text" placeholder="Search..." data-bind:search data-on:input__debounce.200ms="@get('/active-search/search')"><table><thead><tr><th>First Name</th><th>Last Name</th></tr></thead><tbody>${foundUsersRows(
            filteredUsers,
        )}</tbody></table></div>"""
        generator.patchElements(html)
    }
}

private const val ROW = """<tr><td>%s</td><td>%s</td></tr>"""

private fun foundUsersRows(users: List<User>): String =
    users
        .joinToString("\n") { user ->
            ROW.format(user.firstName, user.lastName)
        }

@Serializable
data class ActiveSearchSignals(
    val search: String,
)

data class User(
    val firstName: String,
    val lastName: String,
)

private val initialUsers =
    listOf(
        User("Abraham", "Altenwerth"),
        User("Adan", "Padberg"),
        User("Aiden", "Haley"),
        User("Alec", "Kris"),
        User("Alfredo", "Nitzsche"),
        User("Alisha", "Rogahn"),
        User("Alvah", "Bins"),
        User("Anabel", "Lehner"),
        User("Angela", "Swift"),
        User("Annamarie", "Rippin"),
    )
