package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.div
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.img
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import pt.isel.datastar.expressions.get
import pt.isel.datastar.extensions.dataInit
import pt.isel.ktor.LazyLoadImage
import pt.isel.utils.loadResource

private val description = loadResource("public/html/fragments/lazy-load-description.html")

val hfLazyLoad: HtmlView<Unit> =
    view {
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
                    hfLazyLoadGraph()
                }
                raw(description)
            }
        }
    }

fun Div<*>.hfLazyLoadGraph() {
    attrId("graph")
    dataInit(get(::loadGraph))
    text("Loading...")
}

val hfLazyLoadDoc =
    view<LazyLoadImage> {
        div {
            attrId("graph")
            dyn { image: LazyLoadImage ->
                img {
                    attrSrc(image.src)
                    attrAlt(image.alt)
                }
            }
        }
    }

@Path("/lazy-load/graph")
private fun loadGraph() {}
