package pt.isel.views.htmlflow

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
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.span
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.Click
import pt.isel.http4k.counterEvents
import pt.isel.http4k.decrementCounter
import pt.isel.http4k.getCounterDescription
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
                                dataInit { +get(::getCounterDescription) }
                            }
                            div {
                                dataInit { +get(::counterEvents) }
                                span {
                                    attrId("counter")
                                    dataText { +count }
                                }
                            }
                            div {
                                button {
                                    attrId("decrement")
                                    dataOn(Click) {
                                        +post(::decrementCounter)
                                    }
                                    text("−")
                                }
                                button {
                                    attrId("increment")
                                    dataOn(Click) {
                                        +post(::incrementCounter)
                                    }
                                    text("+")
                                }
                            }
                        }
                    }
                }
            }
        }.toString()

fun hfCounterEventView(count: Int): String =
    view<Int> {
        span {
            attrId("counter")
            dyn { count: Int -> text(count.toString()) }
        }
    }.render(count)
