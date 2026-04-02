package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.tr
import htmlflow.view
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
import pt.isel.datastar.events.Click
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataInit
import pt.isel.datastar.extensions.dataOn
import pt.isel.datastar.extensions.dataSignal
import pt.isel.http4k.cancelEditRow
import pt.isel.http4k.getEditRowDescription
import pt.isel.http4k.resetTable
import pt.isel.ktor.TableState
import pt.isel.ktor.TableUser

// Module-level signal accessible to all functions
private lateinit var editing: Signal<Boolean>

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
                    attrId("description")
                    dataInit { +get(::getEditRowDescription) }
                }
                div {
                    hfEditRowTable()
                }
            }
        }
    }

fun Div<*>.hfEditRowTable() {
    attrId("demo")
    val editing = dataSignal("_editing", false).also { editing = it as Signal<Boolean> }
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
                state.users.forEach { user ->
                    tr {
                        attrId("row-${user.idx}")
                        td { text(user.name) }
                        td { text(user.email) }
                        td {
                            button {
                                attrId("edit-row-${user.idx}")
                                dataOn(Click) {
                                    +editing.setValue(true)
                                    +get("/edit-row/${user.idx}")
                                }
                                val fetching = dataIndicator("_fetching")
                                dataAttr("disabled") { +fetching.or(editing) }
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
            dataOn(Click) {
                +editing.setValue(false)
                +put(::resetTable)
            }
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled") { +fetching }
            i { attrClass("pixelarticons:user-plus") }
            text("Reset")
        }
    }
}

val defaultRowView: HtmlView<TableUser> =
    view {
        tr {
            dyn { user: TableUser ->
                attrId("row-${user.idx}")
                td { text(user.name) }
                td { text(user.email) }
                td {
                    button {
                        attrId("edit-row-${user.idx}")
                        dataOn(Click) {
                            +editing.setValue(true)
                            +get("/edit-row/${user.idx}")
                        }
                        val fetching = dataIndicator("_fetching")
                        dataAttr("disabled") { +fetching.or(editing) }
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
            dataAttr("disabled") { +fetching }
        }
    }
    td {
        input {
            attrType(EnumTypeInputType.EMAIL)
            dataBind("email")
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled") { +fetching }
        }
    }
    td {
        button {
            attrId("save-row-$index")
            dataOn(Click) {
                +editing.setValue(false)
                +patch("/edit-row/$index")
            }
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled") { +fetching }
            i { attrClass("pixelarticons:check") }
            text("Save")
        }
        button {
            attrId("cancel-row-$index")
            dataOn(Click) {
                +editing.setValue(false)
                +get(::cancelEditRow)
            }
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled") { +fetching }
            i { attrClass("pixelarticons:close") }
            text("Cancel")
        }
    }
}

val hfPartialEditRowView: HtmlView<TableUser> =
    view {
        tr {
            dyn { row: TableUser ->
                attrId("row-${row.idx}")
                editRow(row.idx)
            }
        }
    }
