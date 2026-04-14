package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.span
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.Click
import pt.isel.http4k.decrementCounterViaSignals
import pt.isel.http4k.getCounterEventsSignals
import pt.isel.http4k.getCounterSignalsDescription
import pt.isel.http4k.incrementCounterViaSignals

val hfCounterViaSignals: String =
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
                            dataInit { get(::getCounterSignalsDescription) }
                        }
                        div {
                            val count = dataSignal("count", 0)
                            div {
                                dataInit { get(::getCounterEventsSignals) }
                                span {
                                    attrId("counter")
                                    dataText { +count }
                                }
                            }
                            div {
                                button {
                                    attrId("decrement")
                                    dataOn(Click) {
                                        post(::decrementCounterViaSignals)
                                    }
                                    text("−")
                                }
                                button {
                                    attrId("increment")
                                    dataOn(Click) {
                                        post(::incrementCounterViaSignals)
                                    }
                                    text("+")
                                }
                            }
                        }
                    }
                }
            }
        }.toString()
