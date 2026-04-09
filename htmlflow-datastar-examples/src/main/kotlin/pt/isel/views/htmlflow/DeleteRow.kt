package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.Tbody
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
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Click
import pt.isel.http4k.getDeleteRowDescription
import pt.isel.http4k.restoreRows
import pt.isel.ktor.DeleteRowsState
import pt.isel.ktor.TableUser

val hfDeleteRow: HtmlView<DeleteRowsState> =
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
                    dataInit { +get(::getDeleteRowDescription) }
                }
                div {
                    attrId("users-table")
                    hfDeleteRowTable()
                }
            }
        }
    }

fun Div<*>.hfDeleteRowTable() {
    table {
        thead {
            tr {
                th { text("Name") }
                th { text("Email") }
                th { text("Actions") }
            }
        }
        tbody {
            dyn { state: DeleteRowsState ->
                state.users.forEachIndexed { index, user ->
                    hfTableRow(index, user)
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
            dataAttr("disabled") { +fetching }
            i { attrClass("pixelarticons:user-plus") }
            text("Reset")
        }
    }
}

fun Tbody<*>.hfTableRow(
    index: Int,
    user: TableUser,
) = tr {
    attrId("row-$index")
    td { text(user.name) }
    td { text(user.email) }
    td {
        button {
            attrClass("error")
            dataOn(Click) {
                +("confirm('Are you sure?')" and delete("/delete-row/$index"))
            }
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled") { +fetching }
            text("Delete")
        }
    }
}
