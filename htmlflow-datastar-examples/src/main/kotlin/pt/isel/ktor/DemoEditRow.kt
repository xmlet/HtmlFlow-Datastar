package pt.isel.ktor

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import htmlflow.div
import htmlflow.tr
import htmlflow.view
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.i
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn
import pt.isel.views.htmlflow.hfEditRow

private val html = loadResource("public/html/edit-row.html")

fun Route.demoEditRow() {
    val users = MutableStateFlow(EDIT_USERS)
    route("/edit-row") {
        get("/html", RoutingContext::getEditRowHtml)
        get("/htmlflow", RoutingContext::getEditRowHtmlFlow)
        get("/{index}") { editRow(users) }
        put("/reset") { resetUsers(users) }
        get("/cancel") { cancelEditRow(users) }
        patch("/{index}") { saveEditRow(users) }
    }
}

private val EDIT_USERS =
    listOf(
        EditRowSignals("Joe Smith", "joe@smith.org"),
        EditRowSignals("Angie MacDowell", "angie@macdowell.org"),
        EditRowSignals("Fuqua Tarkenton", "fuqua@tarkenton.org"),
        EditRowSignals("Kim Yee", "kim@yee.org"),
    )

private suspend fun RoutingContext.getEditRowHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getEditRowHtmlFlow() {
    call.respondText(hfEditRow, ContentType.Text.Html)
}

private suspend fun RoutingContext.editRow(users: MutableStateFlow<List<EditRowSignals>>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)

        val user = users.value.getOrNull(index) ?: return@respondTextWriter

        generator.patchSignals(
            """ { "name": "${user.name}", "email": "${user.email}" } """,
        )

        val html = buildEditRowHtml(users.value, index)
        generator.patchElements(html)
    }
}

private suspend fun RoutingContext.cancelEditRow(users: MutableStateFlow<List<EditRowSignals>>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))

        val html = buildViewRowHtml(users.value)
        generator.patchElements(html)
    }
}

private suspend fun RoutingContext.resetUsers(users: MutableStateFlow<List<EditRowSignals>>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        users.emit(EDIT_USERS)

        val html = buildViewRowHtml(users.value)
        generator.patchElements(html)
    }
}

private suspend fun RoutingContext.saveEditRow(users: MutableStateFlow<List<EditRowSignals>>) {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        val index = call.pathParameters["index"]?.toIntOrNull()
        requireNotNull(index)

        val datastarBodyArgs = call.request.call.receiveText()

        val editedUser = Json.decodeFromString<EditRowSignals>(datastarBodyArgs)

        if (index in users.value.indices) {
            val updatedUsers =
                users.value.toMutableList().apply {
                    this[index] = editedUser
                }
            users.emit(updatedUsers)
        }

        val htmlViewMode = buildViewRowHtml(users.value)
        generator.patchElements(htmlViewMode)
    }
}

private fun buildViewRowHtml(users: List<EditRowSignals>): String =
    view<List<EditRowSignals>> {
        div {
            attrId("demo")
            table {
                thead {
                    tr {
                        th { text("Name") }
                        th { text("Email") }
                        th { text("Actions") }
                    }
                }
                tbody {
                    users.forEachIndexed { index, user ->
                        tr {
                            td { text(user.name) }
                            td { text(user.email) }
                            td {
                                button {
                                    attrClass("info")
                                    dataOn("click", "@get('/edit-row/$index')")
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled", "$fetching")
                                    i { attrClass("pixelarticons:edit") }
                                    text("Edit")
                                }
                            }
                        }
                    }
                }
            }
            div {
                button {
                    attrClass("warning")
                    dataOn("click", "@put('/edit-row/reset')")
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled", "$fetching")
                    i { attrClass("pixelarticons:user-plus") }
                    text("Reset")
                }
            }
        }
    }.render(users)

private fun buildEditRowHtml(
    users: List<EditRowSignals>,
    editingIndex: Int,
): String =
    view<Pair<List<EditRowSignals>, Int>> {
        div {
            attrId("demo")
            table {
                thead {
                    tr {
                        th { text("Name") }
                        th { text("Email") }
                        th { text("Actions") }
                    }
                }
                tbody {
                    users.forEachIndexed { index, user ->
                        tr {
                            if (index == editingIndex) {
                                // Edit mode
                                td {
                                    input {
                                        attrType(EnumTypeInputType.TEXT)
                                        addAttr("data-bind", "name")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled", "$fetching")
                                    }
                                }
                                td {
                                    input {
                                        attrType(EnumTypeInputType.EMAIL)
                                        addAttr("data-bind", "email")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled", "$fetching")
                                    }
                                }
                                td {
                                    button {
                                        attrClass("success")
                                        dataOn("click", "@patch('/edit-row/$index')")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled", "$fetching")
                                        i { attrClass("pixelarticons:check") }
                                        text("Save")
                                    }
                                    button {
                                        attrClass("error")
                                        dataOn("click", "@get('/edit-row/cancel')")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled", "$fetching")
                                        i { attrClass("pixelarticons:close") }
                                        text("Cancel")
                                    }
                                }
                            } else {
                                // View mode
                                td { text(user.name) }
                                td { text(user.email) }
                                td {
                                    button {
                                        attrClass("info")
                                        dataOn("click", "@get('/edit-row/$index')")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled", "$fetching")
                                        i { attrClass("pixelarticons:edit") }
                                        text("Edit")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            div {
                button {
                    attrClass("warning")
                    dataOn("click", "@put('/edit-row/reset')")
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled", "$fetching")
                    i { attrClass("pixelarticons:user-plus") }
                    text("Reset")
                }
            }
        }
    }.render(users to editingIndex)

@Serializable
data class EditRowSignals(
    val name: String,
    val email: String,
)
