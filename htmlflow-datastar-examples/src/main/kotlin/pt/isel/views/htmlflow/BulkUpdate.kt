package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.tbody
import htmlflow.view
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeButtonType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.Tbody
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
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataEffect
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignals
import org.xmlet.htmlflow.datastar.events.Change
import org.xmlet.htmlflow.datastar.events.Click
import pt.isel.http4k.activateUsers
import pt.isel.http4k.deactivateUsers
import pt.isel.http4k.getBulkUpdateDescription
import pt.isel.ktor.User
import kotlin.collections.forEach

private const val FETCHING_SIGNAL = $$"$_fetching"

fun Tbody<*>.hfUserRows() {
    dyn { users: List<User> ->
        users.forEach { user ->
            tr {
                td {
                    input {
                        attrType(EnumTypeInputType.CHECKBOX)
                        dataBind("selections")
                        dataAttr("disabled") { +FETCHING_SIGNAL }
                    }
                }
                td { text(user.name) }
                td { text(user.email) }
                td { text(user.status.syntax) }
            }
        }
    }
}

val userRowsFragment: HtmlView<List<User>> =
    view {
        tbody {
            attrId("users")
            hfUserRows()
        }
    }

val hfBulkUpdate: HtmlView<List<User>> =
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
                    dataInit { get(::getBulkUpdateDescription) }
                }
                div {
                    attrId("demo")
                    val (fetching, selections) =
                        dataSignals(
                            "_fetching" to false,
                            "selections" to { "Array(4).fill(false)" },
                        ) { modifiers { ifMissing() } }
                    table {
                        thead {
                            tr {
                                th {
                                    input {
                                        attrType(EnumTypeInputType.CHECKBOX)
                                        dataOn(Change) {
                                            setAll("el.checked", "{include: /^selections/}")
                                        }
                                        dataEffect { +$$"el.checked = $selections.every(Boolean)" }
                                        dataAttr("disabled") { +fetching }
                                    }
                                }
                                th { text("Name") }
                                th { text("Email") }
                                th { text("Status") }
                            }
                        }
                        tbody {
                            attrId("users")
                            hfUserRows()
                        }
                    }
                    div {
                        button {
                            attrClass("success")
                            attrType(EnumTypeButtonType.BUTTON)
                            dataOn(Click) {
                                put(::activateUsers)
                            }
                            dataIndicator(fetching.name)
                            dataAttr("disabled") { +fetching }
                            i { attrClass("pixelarticons:user-plus") }
                            text("Activate")
                        }
                        button {
                            attrClass("error")
                            attrType(EnumTypeButtonType.BUTTON)
                            dataOn(Click) {
                                put(::deactivateUsers)
                            }
                            dataIndicator(fetching.name)
                            dataAttr("disabled") { +fetching }
                            i { attrClass("pixelarticons:user-x") }
                            text("Deactivate")
                        }
                    }
                }
            }
        }
    }
