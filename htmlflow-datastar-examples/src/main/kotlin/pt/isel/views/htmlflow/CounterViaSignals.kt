package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.h1
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.span
import pt.isel.datastar.expressions.get
import pt.isel.datastar.expressions.post
import pt.isel.datastar.extensions.dataInit
import pt.isel.datastar.extensions.dataOn
import pt.isel.datastar.extensions.dataSignal
import pt.isel.datastar.extensions.dataText
import pt.isel.http4k.decrementCounterViaSignals
import pt.isel.http4k.getClickToLoadDescription
import pt.isel.http4k.getCounterEventsSignals
import pt.isel.http4k.getCounterSignalsDescription
import pt.isel.http4k.incrementCounterViaSignals
import pt.isel.utils.loadResource

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
                            dataInit(get(::getCounterSignalsDescription))
                        }
                        div {
                            val count = dataSignal("count", 0)
                            h1 {
                                text("Counting Stars HtmlFlow - via Signals")
                            }
                            div {
                                dataInit(get(::getCounterEventsSignals))
                                span {
                                    attrId("counter")
                                    dataText(count)
                                }
                            }
                            div {
                                button {
                                    attrId("decrement")
                                    dataOn("click", post(::decrementCounterViaSignals))
                                    text("−")
                                }
                                button {
                                    attrId("increment")
                                    dataOn("click", post(::incrementCounterViaSignals))
                                    text("+")
                                }
                            }
                        }
                    }
                }
            }
        }.toString()
