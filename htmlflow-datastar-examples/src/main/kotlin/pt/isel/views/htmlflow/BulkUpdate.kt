package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
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
import pt.isel.datastar.events.Change
import pt.isel.datastar.events.Click
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataEffect
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn
import pt.isel.datastar.extensions.dataSignals
import pt.isel.ktor.User
import kotlin.collections.component1
import kotlin.collections.component2

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
                                            +setAll("el.checked", "{include: /^selections/}")
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
                            dyn { users: List<User> ->
                                users.forEach { user: User ->
                                    tr {
                                        td {
                                            input {
                                                attrType(EnumTypeInputType.CHECKBOX)
                                                dataBind("selections")
                                                dataAttr("disabled") { +fetching }
                                            }
                                        }
                                        td { text(user.name) }
                                        td { text(user.email) }
                                        td { text(user.status.syntax) }
                                    }
                                }
                            }
                        }
                    }
                    div {
                        button {
                            attrClass("success")
                            dataOn(Click) {
                                +put(::activate)
                            }
                            dataIndicator(fetching.name)
                            dataAttr("disabled") { +fetching }
                            i { attrClass("pixelarticons:user-plus") }
                            text("Activate")
                        }
                        button {
                            attrClass("error")
                            dataOn(Click) {
                                +put(::deactivate)
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

@Path("/bulk-update/activate")
private fun activate() {}

@Path("/bulk-update/deactivate")
private fun deactivate() {}
