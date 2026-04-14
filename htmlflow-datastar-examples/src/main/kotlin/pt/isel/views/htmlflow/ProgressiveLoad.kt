package pt.isel.views.htmlflow

import htmlflow.HtmlDoc
import htmlflow.div
import htmlflow.doc
import htmlflow.html
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
import org.xmlet.htmlflow.datastar.Signal
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataClass
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.events.Click
import pt.isel.http4k.getProgressiveLoadDescription
import pt.isel.http4k.getProgressiveLoadUpdates

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
                        div {
                            attrId("description")
                            dataInit { get(::getProgressiveLoadDescription) }
                        }
                        div {
                            attrClass("actions")
                            var progressiveLoad: Signal<Boolean>? = null
                            button {
                                attrId("load-button")
                                val loadDisabled = dataSignal("load-disabled", false)
                                dataOn(Click) {
                                    loadDisabled.setValue(true)
                                    get(::getProgressiveLoadUpdates)
                                }
                                dataAttr("disabled") { +loadDisabled }
                                progressiveLoad = dataIndicator("progressive-load")
                                text("Load")
                            }
                            div {
                                attrClass("indicator")
                                checkNotNull(progressiveLoad) { "progressiveLoad signal should have been initialized by the dataIndicator" }
                                dataClass("loading") { +progressiveLoad }
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
