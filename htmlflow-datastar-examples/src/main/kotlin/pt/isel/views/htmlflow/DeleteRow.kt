package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.i
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import pt.isel.datastar.events.Click
import pt.isel.datastar.expressions.delete
import pt.isel.datastar.expressions.patch
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn
import pt.isel.ktor.TableState

val hfDeleteRow: HtmlView<TableState> =
    view {
        html {
            head {
                script {
                    attrType(EnumTypeScriptType.MODULE)
                    attrSrc("/js/datastar.js")
                }
                link {
                    attrRel(EnumRelType.STYLESHEET)
                    attrHref("/css/styles.css")
                }
            }
            body {
                div {
                    hfDeleteRowTable()
                }
            }
        }
    }

fun Div<*>.hfDeleteRowTable() {
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
            dyn { state: TableState ->
                state.users.forEachIndexed { index, user ->
                    tr {
                        td { text(user.name) }
                        td { text(user.email) }
                        td {
                            button {
                                attrClass("error")
                                dataOn(Click, "confirm('Are you sure?') && ${delete("/delete-row/$index")}")
                                val fetching = dataIndicator("_fetching")
                                dataAttr("disabled", fetching)
                                text("Delete")
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
            dataOn(Click) {
                +patch(::restoreRows)
            }
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled", fetching)
            i { attrClass("pixelarticons:user-plus") }
            text("Reset")
        }
    }
}

@Path("/delete-row/reset")
private fun restoreRows() {}
