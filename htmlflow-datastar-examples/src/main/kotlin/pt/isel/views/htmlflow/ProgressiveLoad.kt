package pt.isel.views.htmlflow

import htmlflow.HtmlDoc
import htmlflow.div
import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.header
import org.xmlet.htmlapifaster.img
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.section
import pt.isel.datastar.Signal
import pt.isel.datastar.expressions.get
import pt.isel.datastar.expressions.semiColon
import pt.isel.datastar.expressions.setValue
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataClass
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn
import pt.isel.datastar.extensions.dataSignal
import pt.isel.utils.loadResource

private val description = loadResource("pt/isel/views/fragments/progressive-load-description.html")

val hfProgressiveLoad =
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
                            attrClass("actions")
                            var progressiveLoad: Signal<Boolean>? = null
                            button {
                                attrId("load-button")
                                val loadDisabled = dataSignal("load-disabled", false)
                                dataOn(
                                    "click",
                                    loadDisabled setValue true semiColon get(::progressiveLoadUpdates),
                                )
                                dataAttr("disabled", loadDisabled)
                                progressiveLoad = dataIndicator("progressive-load")
                                text("Load")
                            }
                            div {
                                attrClass("indicator")
                                checkNotNull(progressiveLoad) { "progressiveLoad signal should have been initialized by the dataIndicator" }
                                dataClass("loading", progressiveLoad)
                                img {
                                    attrAlt("Indicator")
                                    attrSrc("/images/rocket-animated.gif")
                                    attrWidth(32)
                                    attrHeight(32)
                                }
                            }
                        }
                        p { text("Each part is loaded progressively.") }
                        loadDiv()
                    }
                }
            }
        }.toString()

fun HtmlDoc.loadDiv() {
    div {
        attrId("Load")
        header { attrId("header") }
        section { attrId("article") }
        section { attrId("comments") }
        div { attrId("footer") }
    }
}

@Path("/progressive-load/updates")
private fun progressiveLoadUpdates() {}
