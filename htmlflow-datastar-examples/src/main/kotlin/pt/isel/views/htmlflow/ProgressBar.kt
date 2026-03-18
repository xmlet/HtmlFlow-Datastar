package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import htmlflow.l
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.svg
import pt.isel.datastar.extensions.dataInit
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

                            div {
                                attrId("progress-bar")
                                dataInit("@get('/progress-bar/updates', {openWhenHidden: true})")
                                renderProgressSvg(0)
                                div {}
                            }
                        }
                        raw(description)
                    }
                }
            }
        }.toString()

fun renderProgressBarFragment(state: ProgressBarState): String {
    val dashOffset = (565.48 * (1 - state.progress / 100.0)).roundToInt()
    return if (!state.completed) {
        """
        <div id="progress-bar" data-init="@get('/progress-bar/updates', {openWhenHidden: true})">
            ${renderSvgHtml(state.progress, dashOffset)}
            <div></div>
        </div>
        """.trimIndent()
    } else {
        """
        <div id="progress-bar">
            ${renderSvgHtml(state.progress, dashOffset)}
            <div data-on:click="@get('/progress-bar/updates', {openWhenHidden: true})">
                <button>
                    Completed! Try again?
                </button>
            </div>
        </div>
        """.trimIndent()
    }
}

private fun renderSvgHtml(
    progress: Int,
    dashOffset: Int,
): String =
    """
    <svg width="200" height="200" viewbox="-25 -25 250 250" style="transform: rotate(-90deg)">
        <circle r="90" cx="100" cy="100" fill="transparent" stroke="#e0e0e0"
            stroke-width="16px" stroke-dasharray="565.48px" stroke-dashoffset="565px"></circle>
        <circle r="90" cx="100" cy="100" fill="transparent" stroke="#6bdba7"
            stroke-width="16px" stroke-linecap="round"
            stroke-dashoffset="${dashOffset}px" stroke-dasharray="565.48px"></circle>
        <text x="44" y="115" fill="#6bdba7" font-size="52" font-weight="bold"
            style="transform:rotate(90deg) translate(0px, -196px)">$progress%</text>
    </svg>
    """.trimIndent()

private fun Div<*>.renderProgressSvg(progress: Int) {
    val dashOffset = (565.48 * (1 - progress / 100.0)).roundToInt()
    svg {
        attrWidth(200)
        attrHeight(200)
        addAttr("viewbox", "-25 -25 250 250")
        attrStyle("transform: rotate(-90deg)")

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
