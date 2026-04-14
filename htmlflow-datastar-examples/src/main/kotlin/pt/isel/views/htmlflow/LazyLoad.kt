package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.div
import htmlflow.doc
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.img
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlflow.datastar.attributes.dataInit
import pt.isel.http4k.getLazyLoadDescription
import pt.isel.http4k.loadGraph
import pt.isel.ktor.LazyLoadImage

val hfLazyLoadDoc: String =
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
                            dataInit { get(::getLazyLoadDescription) }
                        }
                        div {
                            attrId("graph")
                            dataInit { get(::loadGraph) }
                            text("Loading...")
                        }
                    }
                }
            }
        }.toString()

val hfLazyLoadView: HtmlView<LazyLoadImage> =
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
