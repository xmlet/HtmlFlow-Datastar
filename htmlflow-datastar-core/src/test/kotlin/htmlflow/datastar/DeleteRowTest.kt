package htmlflow.datastar

import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.Tbody
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.i
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Click
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteRowTest {
    class Dude(
        val name: String,
        val email: String,
    )

    val dudes: List<Dude> =
        listOf(
            Dude(name = "Joe Smith", email = "joe@smith.org"),
            Dude(name = "Angie MacDowell", email = "angie@macdowell.org"),
            Dude(name = "Fuqua Tarkenton", email = "fuqua@tarkenton.org"),
            Dude(name = "Kim Yee", email = "kim@yee.org"),
        )

    @Test
    fun `Delete Row of the Datastar Frontend Reactivity`() {
        val out = demoDastarRx
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.render(dudes).split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
        view<List<Dude>> {
            html {
                head {
                    script {
                        attrType(EnumTypeScriptType.MODULE)
                        attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js")
                    }
                }
                body {
                    div {
                        dyn { dudes: List<Dude> -> buildTable(dudes) }
                        div {
                            button {
                                attrClass("success")
                                dataOn(Click) {
                                    +patch(::restoreRows)
                                }
                                i { attrClass("pixelarticons:user-plus") }
                                text("Reset")
                            }
                        }
                    }
                }
            }
        }

    fun Div<*>.buildTable(dudes: List<Dude>) {
        table {
            thead {
                tr {
                    th { text("Name") }
                    th { text("Email") }
                    th { text("Actions") }
                }
            }
            tbody {
                dudes.forEachIndexed { index, dude -> buildRow(dude, index) }
            }
        }
    }

    fun Tbody<*>.buildRow(
        dude: Dude,
        index: Int,
    ) {
        tr {
            td { text(dude.name) }
            td { text(dude.email) }
            td {
                button {
                    attrClass("error")
                    dataOn(Click) {
                        +"confirm('Are you sure?') && @delete('/examples/delete_row/$index')"
                    }
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled") { +fetching }
                    text("Delete")
                }
            }
        }
    }

    @Path("/examples/delete_row/reset")
    private fun restoreRows() {
    }

    private val expectedDatastarRx =
        $$"""
         <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div>
                    <table>
                        <thead>
                            <tr>
                                <th>
                                    Name
                                </th>
                                <th>
                                    Email
                                </th>
                                <th>
                                    Actions
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>
                                    Joe Smith
                                </td>
                                <td>
                                    joe@smith.org
                                </td>
                                <td>
                                    <button class="error" data-on:click="confirm('Are you sure?') && @delete('/examples/delete_row/0')" data-indicator:_fetching="" data-attr:disabled="$_fetching">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Angie MacDowell
                                </td>
                                <td>
                                    angie@macdowell.org
                                </td>
                                <td>
                                    <button class="error" data-on:click="confirm('Are you sure?') && @delete('/examples/delete_row/1')" data-indicator:_fetching="" data-attr:disabled="$_fetching">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Fuqua Tarkenton
                                </td>
                                <td>
                                    fuqua@tarkenton.org
                                </td>
                                <td>
                                    <button class="error" data-on:click="confirm('Are you sure?') && @delete('/examples/delete_row/2')" data-indicator:_fetching="" data-attr:disabled="$_fetching">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Kim Yee
                                </td>
                                <td>
                                    kim@yee.org
                                </td>
                                <td>
                                    <button class="error" data-on:click="confirm('Are you sure?') && @delete('/examples/delete_row/3')" data-indicator:_fetching="" data-attr:disabled="$_fetching">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div>
                        <button class="success" data-on:click="@patch('/examples/delete_row/reset')">
                            <i class="pixelarticons:user-plus">
                            </i>
                            Reset
                        </button>
                    </div>
                </div>
            </body>
        </html>
        """
}
