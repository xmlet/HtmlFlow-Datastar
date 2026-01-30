package pt.isel.views.htmlflow

import htmlflow.dataAttr
import htmlflow.dataIndicator
import htmlflow.dataOn
import htmlflow.dataSignal
import htmlflow.dataText
import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.h1
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr

val hfClickToLoad: String =
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
                        h1 {
                            text("Click to Load")
                        }
                        div {
                            attrId("demo")
                            dataSignal("offset", 0)
                            dataSignal("limit", 5)
                        }
                        table {
                            thead {
                                tr {
                                    th { text("Name") }
                                    th { text("Email") }
                                    th { text("ID") }
                                }
                            }
                            tbody {
                                attrId("agents")
                            }
                        }
                        button {
                            attrClass("info wide")
                            val fetching = dataIndicator("_fetching")
                            dataAttr("disabled", fetching)
                            dataOn("click", "!$fetching && @get('/click-to-load/more')")
                            dataText("$fetching ? 'Loading...' : 'Load More'")
                            +"Load More"
                        }
                    }
                }
            }
        }.toString()
