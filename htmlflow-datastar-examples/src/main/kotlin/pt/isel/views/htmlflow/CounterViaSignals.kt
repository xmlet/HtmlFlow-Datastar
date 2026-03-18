package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
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
import pt.isel.utils.loadResource

private val description = loadResource("public/html/fragments/counter-signals-description.html")

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
                        raw(description)
                        div {
                            val count = dataSignal("count", 0)
                            h1 {
                                text("Counting Stars HtmlFlow - via Signals")
                            }
                            div {
                                dataInit(get(::events))
                                span {
                                    attrId("counter")
                                    dataText(count)
                                }
                            }
                            div {
                                button {
                                    attrId("decrement")
                                    dataOn("click", post(::decrement))
                                    text("−")
                                }
                                button {
                                    attrId("increment")
                                    dataOn("click", post(::increment))
                                    text("+")
                                }
                            }
                        }
                    }
                }
            }
        }.toString()

@Path("/counter-signals/events")
private fun events() {}

@Path("/counter-signals/decrement")
private fun decrement() {}

@Path("/counter-signals/increment")
private fun increment() {}
