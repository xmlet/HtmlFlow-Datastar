package kotlinx.html.datastar

import jakarta.ws.rs.Path
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.input
import kotlinx.html.script
import kotlinx.html.stream.appendHTML
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import org.xmlet.htmlflow.datastar.events.Input
import org.xmlet.kotlinx.html.datastar.dataBind
import org.xmlet.kotlinx.html.datastar.dataOn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class ActiveSearchTest {
    @Test
    fun `Active Search of the Datastar Frontend Reactivity`() {
        val out = demoDastarRx.render(initialContacts)
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.toString().trim().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
        object {
            fun render(contacts: List<Contact>): StringBuilder =
                StringBuilder().apply {
                    appendHTML(prettyPrint = true).html {
                        head {
                            script {
                                attributes["type"] = "module"
                                attributes["src"] = "https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js"
                            }
                        }
                        body {
                            div {
                                attributes["id"] = "demo"
                                input {
                                    attributes["type"] = "text"
                                    attributes["placeholder"] = "Search..."
                                    dataBind("search")
                                    dataOn(Input) {
                                        get(::search)
                                        modifiers { debounce(200.milliseconds) }
                                    }
                                }
                                table {
                                    thead {
                                        tr {
                                            th { +"First Name" }
                                            th { +"Last Name" }
                                        }
                                    }
                                    tbody {
                                        attributes["id"] = "contacts"
                                        contacts.forEach { cnt ->
                                            tr {
                                                td { +cnt.firstName }
                                                td { +cnt.lastName }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        }

    private data class Contact(
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

    @Path("/active-search/search")
    fun search() {}

    private val expectedDatastarRx =
        """
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js"></script>
    </head>
<body>
    <div id="demo"><input type="text" placeholder="Search..." data-bind="search" data-on:input__debounce.200ms="@get('/active-search/search')">
        <table>   
            <thead>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                </tr>
            </thead> 
            <tbody id="contacts">
                <tr>
                    <td>Abraham</td>
                    <td>Altenwerth</td>
                </tr>
                <tr>
                    <td>Adan</td>
                    <td>Padberg</td>
                </tr>
                <tr>
                    <td>Aiden</td>
                    <td>Haley</td>
                </tr>
                <tr>
                    <td>Alec</td>
                    <td>Kris</td>
                </tr>
                <tr>
                    <td>Alfredo</td>
                    <td>Nitzsche</td>
                </tr>
                <tr>
                    <td>Alisha</td>
                    <td>Rogahn</td>
                </tr>
                <tr>
                    <td>Alvah</td>
                    <td>Bins</td>
                </tr>
                <tr>
                    <td>Anabel</td>
                    <td>Lehner</td>
                </tr>
                <tr>
                    <td>Angela</td>
                    <td>Swift</td>
                </tr>
                <tr>
                    <td>Annamarie</td>
                    <td>Rippin</td>
                </tr>
            </tbody>
        </table>
    </div>
</body>
</html>""".trim()
}
