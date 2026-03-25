package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.doc
import htmlflow.dyn
import htmlflow.html
import htmlflow.span
import htmlflow.view
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
import pt.isel.datastar.expressions.post
import pt.isel.datastar.extensions.dataInit
import pt.isel.datastar.extensions.dataOn
import pt.isel.datastar.extensions.dataSignal
import pt.isel.datastar.extensions.dataText
import pt.isel.http4k.counterEvents
import pt.isel.http4k.decrementCounter
import pt.isel.http4k.getCounterDescription
import pt.isel.http4k.getCounterSignalsDescription
import pt.isel.http4k.incrementCounter

val hfCounter: String =
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
                            val count = dataSignal("count", 0)
                            div {
                                attrId("description")
                                dataInit(
                                    pt.isel.datastar.expressions
                                        .get(::getCounterDescription),
                                )
                            }
                            div {
                                dataInit(
                                    pt.isel.datastar.expressions
                                        .get(::counterEvents),
                                )
                                span {
                                    attrId("counter")
                                    dataText(count)
                                }
                            }
                            div {
                                button {
                                    attrId("decrement")
                                    dataOn("click", post(::decrementCounter))
                                    text("−")
                                }
                                button {
                                    attrId("increment")
                                    dataOn("click", post(::incrementCounter))
                                    text("+")
                                }
                            }
                        }
                    }
                }
            }
        }.toString()

val hfCounterEventView: HtmlView<Int> =
    view {
        span {
            attrId("counter")
            dyn { count: Int ->
                text(count.toString())
            }
        }
    }
