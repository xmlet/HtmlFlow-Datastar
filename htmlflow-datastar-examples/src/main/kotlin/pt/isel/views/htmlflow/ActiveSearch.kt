package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataOn
import kotlin.time.Duration.Companion.milliseconds

val hfActiveSearch =
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
                            attrId("demo")
                            input {
                                attrType(EnumTypeInputType.TEXT)
                                attrPlaceholder("Search...")
                                dataBind("search")
                                dataOn("input", "@get('/active-search/search')") {
                                    debounce(200.milliseconds)
                                }
                            }
                            table {
                                thead {
                                    tr {
                                        th { text("First Name") }
                                        th { text("Last Name") }
                                    }
                                }
                                tbody {
                                    tr {
                                        td { text("Abraham") }
                                        td { text("Altenwerth") }
                                    }
                                    tr {
                                        td { text("Adan") }
                                        td { text("Padberg") }
                                    }
                                    tr {
                                        td { text("Aiden") }
                                        td { text("Haley") }
                                    }
                                    tr {
                                        td { text("Alec") }
                                        td { text("Kris") }
                                    }
                                    tr {
                                        td { text("Alfredo") }
                                        td { text("Nitzsche") }
                                    }
                                    tr {
                                        td { text("Alisha") }
                                        td { text("Rogahn") }
                                    }
                                    tr {
                                        td { text("Alvah") }
                                        td { text("Bins") }
                                    }
                                    tr {
                                        td { text("Anabel") }
                                        td { text("Lehner") }
                                    }
                                    tr {
                                        td { text("Angela") }
                                        td { text("Swift") }
                                    }
                                    tr {
                                        td { text("Annamarie") }
                                        td { text("Rippin") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.toString()
