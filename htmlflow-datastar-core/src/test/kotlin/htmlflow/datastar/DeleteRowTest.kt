package htmlflow.datastar

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.EnumTypeScriptType
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
import pt.isel.datastar.expressions.delete
import pt.isel.datastar.expressions.patch
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteRowTest {
    @Test
    fun `Delete Row of the Datastar Frontend Reactivity`() {
        val out = demoDastarRx
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
        StringBuilder()
            .apply {
                doc {
                    html {
                        head {
                            script {
                                attrType(EnumTypeScriptType.MODULE)
                                attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js")
                            }
                        }
                        body {
                            div {
                                table {
                                    thead {
                                        tr {
                                            th { text("Name") }
                                            th { text("Email") }
                                            th { text("Actions") }
                                        }
                                    }
                                    tbody {
                                        tr {
                                            td { text("Joe Smith") }
                                            td { text("joe@smith.org") }
                                            td {
                                                button {
                                                    attrClass("error")
                                                    dataOn("click", "confirm('Are you sure?') && ${delete(::deleteRow0)}")
                                                    val fetching = dataIndicator("_fetching")
                                                    dataAttr("disabled", fetching)
                                                    text("Delete")
                                                }
                                            }
                                        }
                                        tr {
                                            td { text("Angie MacDowell") }
                                            td { text("angie@macdowell.org") }
                                            td {
                                                button {
                                                    attrClass("error")
                                                    dataOn("click", "confirm('Are you sure?') && ${delete(::deleteRow1)}")
                                                    val fetching = dataIndicator("_fetching")
                                                    dataAttr("disabled", fetching)
                                                    text("Delete")
                                                }
                                            }
                                        }
                                        tr {
                                            td { text("Fuqua Tarkenton") }
                                            td { text("fuqua@tarkenton.org") }
                                            td {
                                                button {
                                                    attrClass("error")
                                                    dataOn("click", "confirm('Are you sure?') && ${delete(::deleteRow2)}")
                                                    val fetching = dataIndicator("_fetching")
                                                    dataAttr("disabled", fetching)
                                                    text("Delete")
                                                }
                                            }
                                        }
                                        tr {
                                            td { text("Kim Yee") }
                                            td { text("kim@yee.org") }
                                            td {
                                                button {
                                                    attrClass("error")
                                                    dataOn("click", "confirm('Are you sure?') && ${delete(::deleteRow3)}")
                                                    val fetching = dataIndicator("_fetching")
                                                    dataAttr("disabled", fetching)
                                                    text("Delete")
                                                }
                                            }
                                        }
                                    }
                                }
                                div {
                                    button {
                                        attrClass("success")
                                        dataOn("click", patch(::restoreRows))
                                        i { attrClass("pixelarticons:user-plus") }
                                        text("Reset")
                                    }
                                }
                            }
                        }
                    }
                }
            }

    @Path("/examples/delete_row/reset")
    private fun restoreRows() {}

    @Path("/examples/delete_row/0")
    private fun deleteRow0() {}

    @Path("/examples/delete_row/1")
    private fun deleteRow1() {}

    @Path("/examples/delete_row/2")
    private fun deleteRow2() {}

    @Path("/examples/delete_row/3")
    private fun deleteRow3() {}

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
