package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import pt.isel.datastar.extensions.dataInit

val hfLazyLoad =
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
                            attrId("graph")
                            dataInit("@get('/lazy-load/graph')")
                            text("Loading...")
                        }
                    }
                }
            }
        }.toString()
