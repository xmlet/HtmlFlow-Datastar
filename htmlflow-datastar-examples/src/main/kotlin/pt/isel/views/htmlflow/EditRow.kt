package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.tr
import htmlflow.view
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.Tr
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.i
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import pt.isel.datastar.Signal
import pt.isel.datastar.expressions.get
import pt.isel.datastar.expressions.or
import pt.isel.datastar.expressions.patch
import pt.isel.datastar.expressions.put
import pt.isel.datastar.expressions.semiColon
import pt.isel.datastar.expressions.setValue
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn
import pt.isel.datastar.extensions.dataSignal
import pt.isel.ktor.TableState
import pt.isel.ktor.TableUser
import pt.isel.utils.loadResource

// Module-level signal accessible to all functions
private lateinit var editing: Signal<Boolean>
private val description = loadResource("public/html/fragments/edit-row-description.html")

val hfEditRow: HtmlView<TableState> =
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
                    hfEditRowTable()
                }
                raw(description)
            }
        }
    }

fun Div<*>.hfEditRowTable() {
    attrId("demo")
    val editing = dataSignal("_editing", false).also { editing = it }
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
                        attrId("row-$index")
                        td { text(user.name) }
                        td { text(user.email) }
                        td {
                            button {
                                attrId("edit-row-$index")
                                dataOn("click", editing setValue true semiColon get("/edit-row/$index"))
                                val fetching = dataIndicator("_fetching")
                                dataAttr("disabled", fetching or editing)
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
            attrId("reset")
            dataOn("click", editing setValue false semiColon put(::editRowReset))
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled", fetching)
            i { attrClass("pixelarticons:user-plus") }
            text("Reset")
        }
    }
}

fun defaultRowView(idx: Int): HtmlView<TableUser> =
    view {
        tr {
            attrId("row-$idx")
            dyn { user: TableUser ->
                td { text(user.name) }
                td { text(user.email) }
                td {
                    button {
                        attrId("edit-row-$idx")
                        dataOn("click", editing setValue true semiColon get("/edit-row/$idx"))
                        val fetching = dataIndicator("_fetching")
                        dataAttr("disabled", fetching or editing)
                        text("Edit")
                    }
                }
            }
        }
    }

fun Tr<*>.editRow(index: Int) {
    td {
        input {
            attrType(EnumTypeInputType.TEXT)
            dataBind("name")
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled", fetching)
        }
    }
    td {
        input {
            attrType(EnumTypeInputType.EMAIL)
            dataBind("email")
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled", fetching)
        }
    }
    td {
        button {
            attrId("save-row-$index")
            dataOn("click", editing setValue false semiColon patch("/edit-row/$index"))
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled", fetching)
            i { attrClass("pixelarticons:check") }
            text("Save")
        }
        button {
            attrId("cancel-row-$index")
            dataOn("click", editing setValue false semiColon get(::editRowCancel))
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled", fetching)
            i { attrClass("pixelarticons:close") }
            text("Cancel")
        }
    }
}

@Path("/edit-row/reset")
private fun editRowReset() {}

@Path("/edit-row/cancel")
private fun editRowCancel() {}
