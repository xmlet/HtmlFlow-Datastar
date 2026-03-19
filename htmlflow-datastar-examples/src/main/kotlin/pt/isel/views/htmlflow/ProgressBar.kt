package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.div
import htmlflow.doc
import htmlflow.dyn
import htmlflow.html
import htmlflow.l
import htmlflow.view
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.svg
import pt.isel.datastar.extensions.dataInit
import pt.isel.datastar.extensions.dataOn
import pt.isel.ktor.ProgressBarState
import pt.isel.utils.loadResource
import kotlin.math.roundToInt

private val description = loadResource("public/html/fragments/progress-bar-description.html")

val hfProgressBar: String =
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
                            attrId("progress-bar")
                            dataInit("@get('/progress-bar/updates', {openWhenHidden: true})")
                            renderProgressSvg(0)
                        }
                        raw(description)
                    }
                }
            }
        }.toString()

val renderProgressBarFragment: HtmlView<ProgressBarState> =
    view<ProgressBarState> {
        div {
            dyn { state: ProgressBarState ->
                attrId("progress-bar")

                if (!state.completed) {
                    dataInit("@get('/progress-bar/updates', {openWhenHidden: true})")
                }

                renderProgressSvg(state.progress)

                if (state.completed) {
                    div {
                        dataOn("click", "@get('/progress-bar/updates', {openWhenHidden: true})")
                        button {
                            text("Completed! Try again?")
                        }
                    }
                }
            }
        }
    }

private fun Div<*>.renderProgressSvg(progress: Int) {
    val dashOffset = calculateDashOffset(progress)
    svg {
        attrWidth(200)
        attrHeight(200)
        addAttr("viewbox", "-25 -25 250 250")
        attrStyle("transform: rotate(-90deg)")

        // Background circle
        custom("circle")
            .addAttr("r", "90")
            .addAttr("cx", "100")
            .addAttr("cy", "100")
            .addAttr("fill", "transparent")
            .addAttr("stroke", "#e0e0e0")
            .addAttr("stroke-width", "16px")
            .addAttr("stroke-dasharray", "565.48px")
            .addAttr("stroke-dashoffset", "565px")
            .l

        // Progress circle
        custom("circle")
            .addAttr("r", "90")
            .addAttr("cx", "100")
            .addAttr("cy", "100")
            .addAttr("fill", "transparent")
            .addAttr("stroke", "#6bdba7")
            .addAttr("stroke-width", "16px")
            .addAttr("stroke-linecap", "round")
            .addAttr("stroke-dashoffset", "${dashOffset}px")
            .addAttr("stroke-dasharray", "565.48px")
            .l

        // Progress text
        custom("text")
            .addAttr("x", "44")
            .addAttr("y", "115")
            .addAttr("fill", "#6bdba7")
            .addAttr("font-size", "52")
            .addAttr("font-weight", "bold")
            .addAttr("style", "transform:rotate(90deg) translate(0px, -196px)")
            .text("$progress%")
            .l
    }
}

private fun calculateDashOffset(progress: Int): Int = (565.48 * (1 - progress / 100.0)).roundToInt()
