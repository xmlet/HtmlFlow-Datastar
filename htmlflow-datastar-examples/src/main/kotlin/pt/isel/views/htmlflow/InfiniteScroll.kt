package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import pt.isel.datastar.expressions.get
import pt.isel.datastar.extensions.dataOnIntersect
import pt.isel.datastar.extensions.dataSignals

val hfInfiniteScroll =
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
                            dataSignals(
                                "offset" to 10,
                                "limit" to 5,
                            )
                            text("Agents")
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
                                // Firt 10 rows (Agent Smith 0-9)
                                for (i in 0..9) {
                                    tr {
                                        td { text("Agent Smith $i") }
                                        td { text("void$i@null.org") }
                                        td { text(generateId(i)) }
                                    }
                                }
                            }
                        }
                        div {
                            dataOnIntersect(get(::getMore))
                            text("Loading...")
                        }
                    }
                }
            }
        }.toString()

@Path("/infinite-scroll/more")
private fun getMore() {}

private fun generateId(index: Int): String {
    val ids =
        listOf(
            "1982e3a7bb241055",
            "65cd25028f98f158",
            "7b95a7322f5da314",
            "7324dc1e7e9474f0",
            "628911027fcf803f",
            "5edb980100c87e72",
            "3564a48862bc4a0d",
            "6eed105b82285fa",
            "664f427c6b2c4bea",
            "28353a066812b268",
        )
    return ids.getOrElse(index) { "generated-id-$index" }
}
