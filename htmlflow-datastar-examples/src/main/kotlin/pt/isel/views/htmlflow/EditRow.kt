package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
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
import pt.isel.datastar.events.Click
import pt.isel.datastar.expressions.get
import pt.isel.datastar.expressions.patch
import pt.isel.datastar.expressions.put
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn
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
            dyn { state: TableState ->
                state.users.forEachIndexed { index, user ->
                    tr {
                        if (state.editingIndex == index) {
                            editRow(index)
                        } else {
                            viewRow(user, index)
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
                +put(::editRowReset)
            }
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled", fetching)
            i { attrClass("pixelarticons:user-plus") }
            text("Reset")
        }
    }
}

private fun Tr<*>.viewRow(
    tableUser: TableUser,
    index: Int,
) {
    td { text(tableUser.name) }
    td { text(tableUser.email) }
    td {
        button {
            attrClass("info")
            dataOn(Click) {
                +get("/edit-row/$index")
            }
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled", fetching)
            text("Edit")
        }
    }
}

private fun Tr<*>.editRow(index: Int) {
    td {
        input {
            attrType(EnumTypeInputType.TEXT)
            addAttr("data-bind", "name")
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled", fetching)
        }
    }
    td {
        input {
            attrType(EnumTypeInputType.EMAIL)
            addAttr("data-bind", "email")
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled", fetching)
        }
    }
    td {
        button {
            attrClass("success")
            dataOn(Click) {
                +patch("/edit-row/$index")
            }
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled", fetching)
            i { attrClass("pixelarticons:check") }
            text("Save")
        }
        button {
            attrClass("error")
            dataOn(Click) {
                +get(::editRowCancel)
            }
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
