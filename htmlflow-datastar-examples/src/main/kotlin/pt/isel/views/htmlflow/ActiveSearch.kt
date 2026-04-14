package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.tbody
import htmlflow.view
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.Tbody
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Input
import pt.isel.http4k.getActiveSearchDescription
import pt.isel.http4k.getSearchContacts
import pt.isel.ktor.Contact
import kotlin.time.Duration.Companion.milliseconds

val hfActiveSearch: HtmlView<List<Contact>> =
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
                    dataInit { get(::getActiveSearchDescription) }
                }
                div {
                    hfActiveSearchTable()
                }
            }
        }
    }

fun Div<*>.hfActiveSearchTable() {
    attrId("demo")
    input {
        attrType(EnumTypeInputType.TEXT)
        attrPlaceholder("Search...")
        dataBind("search")
        dataOn(Input) {
            get(::getSearchContacts)
            modifiers { debounce(200.milliseconds) }
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
            attrId("contacts")
            hfContactRows()
        }
    }
}

fun Tbody<*>.hfContactRows() {
    dyn { contacts: List<Contact> ->
        contacts.forEach { cnt ->
            tr {
                td { text(cnt.firstName) }
                td { text(cnt.lastName) }
            }
        }
    }
}

val hfActiveSearchContactsRowsFragment: HtmlView<List<Contact>> =
    view {
        tbody {
            attrId("contacts")
            hfContactRows()
        }
    }
