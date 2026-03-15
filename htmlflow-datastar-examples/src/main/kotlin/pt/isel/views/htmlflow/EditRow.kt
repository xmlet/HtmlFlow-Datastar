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
import pt.isel.datastar.expressions.get
import pt.isel.datastar.expressions.or
import pt.isel.datastar.expressions.patch
import pt.isel.datastar.expressions.put
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn
import pt.isel.datastar.extensions.dataSignal
import pt.isel.ktor.TableState
import pt.isel.ktor.TableUser

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
            }
        }
    }

fun Div<*>.hfEditRowTable() {
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
            val editing = dataSignal("_editing", false)

            dyn { state: TableState ->
                state.users.forEachIndexed { index, user ->
                    tr {
                        attrId("row-$index")
                        td { text(user.name) }
                        td { text(user.email) }
                        td {
                            button {
                                attrClass("info")
                                dataOn("click", get("/edit-row/$index"))
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
            attrClass("warning")
            dataOn("click", put(::editRowReset))
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
                        attrClass("info")
                        dataOn("click", get("/edit-row/$idx"))
                        val fetching = dataIndicator("_fetching")
                        dataAttr("disabled", $$"$$fetching || $_editing")
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
            attrClass("success")
            dataOn("click", patch("/edit-row/$index"))
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled", fetching)
            i { attrClass("pixelarticons:check") }
            text("Save")
        }
        button {
            attrClass("error")
            dataOn("click", get(::editRowCancel))
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
