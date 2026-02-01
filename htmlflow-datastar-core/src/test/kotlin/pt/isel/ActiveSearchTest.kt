package pt.isel

import htmlflow.html
import htmlflow.view
import org.xmlet.htmlapifaster.*
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataOn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class ActiveSearchTest {
    @Test
    fun `Active Search of the Datastar Frontend Reactivity`() {
        val out = StringBuilder()
        demoDastarRx.setOut(out).write()
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
        view<Unit> {
            html {
                head {
                    script {
                        attrType(EnumTypeScriptType.MODULE)
                        attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js")
                    }
                }
                body {

                    div {
                        attrId("demo")
                        input {
                            attrType(EnumTypeInputType.TEXT)
                            attrPlaceholder("Search...")
                            dataBind("search")
                            dataOn("input", "@get('/active-search/search')") {
                                debounce(200.milliseconds)
                            }
                        }
                        table {
                            thead {
                                tr {
                                    th { text("First Name") }
                                    th { text("Last Name") }
                                }
                            }
                            tbody {
                                tr {
                                    td { text("Abraham") }
                                    td { text("Altenwerth") }
                                }
                                tr {
                                    td { text("Adan") }
                                    td { text("Padberg") }
                                }
                                tr {
                                    td { text("Aiden") }
                                    td { text("Haley") }
                                }
                                tr {
                                    td { text("Alec") }
                                    td { text("Kris") }
                                }
                                tr {
                                    td { text("Alfredo") }
                                    td { text("Nitzsche") }
                                }
                                tr {
                                    td { text("Alisha") }
                                    td { text("Rogahn") }
                                }
                                tr {
                                    td { text("Alvah") }
                                    td { text("Bins") }
                                }
                                tr {
                                    td { text("Anabel") }
                                    td { text("Lehner") }
                                }
                                tr {
                                    td { text("Angela") }
                                    td { text("Swift") }
                                }
                                tr {
                                    td { text("Annamarie") }
                                    td { text("Rippin") }
                                }
                            }
                        }
                    }
                }
            }
        }

    private val expectedDatastarRx = """
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js">
        </script>
    </head>
<body>
    <div id="demo">
        <input type="text" placeholder="Search..." data-bind:search="" data-on:input__debounce.200ms="@get('/active-search/search')">
        <table>   
            <thead>
                <tr>
                    <th>
                        First Name
                    </th>
                    <th>
                        Last Name
                    </th>
                </tr>
            </thead> 
            <tbody>
                <tr>
                    <td>
                        Abraham
                    </td>
                    <td>
                        Altenwerth
                    </td>
                </tr>
                <tr>
                    <td>
                        Adan
                    </td>
                    <td>
                        Padberg
                    </td>
                </tr>
                <tr>
                    <td>
                        Aiden
                    </td>
                    <td>
                        Haley
                    </td>
                </tr>
                <tr>
                    <td>
                        Alec
                    </td>
                    <td>
                        Kris
                    </td>
                </tr>
                <tr>
                    <td>
                        Alfredo
                    </td>
                    <td>
                        Nitzsche
                    </td>
                </tr>
                <tr>
                    <td>
                        Alisha
                    </td>
                    <td>
                        Rogahn
                    </td>
                </tr>
                <tr>
                    <td>
                        Alvah
                    </td>
                    <td>
                        Bins
                    </td>
                </tr>
                <tr>
                    <td>
                        Anabel
                    </td>
                    <td>
                        Lehner
                    </td>
                </tr>
                <tr>
                    <td>
                        Angela
                    </td>
                    <td>
                        Swift
                    </td>
                </tr>
                <tr>
                    <td>
                        Annamarie
                    </td>
                    <td>
                        Rippin
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</body>
</html>
    """
}
