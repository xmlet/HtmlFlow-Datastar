package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.dyn
import htmlflow.html
import htmlflow.tr
import htmlflow.view
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
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
import org.xmlet.htmlflow.datastar.attributes.dataSignals
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.Click
import org.xmlet.htmlflow.datastar.expressions.not
import pt.isel.http4k.getClickToLoadDescription
import pt.isel.http4k.getMore
import pt.isel.ktor.Agent

val hfClickToLoad: String =
    StringBuilder()
        .apply {
            doc {
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
                            dataInit { +get(::getClickToLoadDescription) }
                        }
                        div {
                            attrId("demo")
                            dataSignals("offset" to 0, "limit" to 5)
                        }
                        table {
                            thead {
                                tr {
                                    th { text("Name") }
                                    th { text("Email") }
                                    th { text("ID") }
                                }
                            }
                            tbody {
                                attrId("agents")
                            }
                        }
                        button {
                            attrClass("info wide")
                            val fetching = dataIndicator("_fetching")
                            dataAttr("disabled") { +fetching }
                            dataOn(Click) {
                                +(!fetching and get(::getMore))
                            }
                            dataText { +"$fetching ? 'Loading...' : 'Load More'" }
                            text("Load More")
                        }
                    }
                }
            }
        }.toString()

val hfAgentRowView =
    view<Agent> {
        tr {
            dyn { agent: Agent ->
                td { text(agent.name) }
                td { text(agent.email) }
                td { text(agent.id) }
            }
        }
    }
