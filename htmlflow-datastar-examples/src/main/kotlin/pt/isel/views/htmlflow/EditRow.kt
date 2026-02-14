package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.i
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn
import pt.isel.ktor.DEFAULT_USERS

val hfEditRow =
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
                            table {
                                thead {
                                    tr {
                                        th { text("Name") }
                                        th { text("Email") }
                                        th { text("Actions") }
                                    }
                                }
                                tbody {
                                    DEFAULT_USERS.forEachIndexed { index, user ->
                                        tr {
                                            td { text(user.name) }
                                            td { text(user.email) }
                                            td {
                                                button {
                                                    attrClass("info")
                                                    dataOn("click", "@get('/edit-row/$index')")
                                                    val fetching = dataIndicator("_fetching")
                                                    dataAttr("disabled", "$fetching")
                                                    text("Edit")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            div {
                                button {
                                    attrClass("warning")
                                    dataOn("click", "@put('/edit-row/reset')")
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled", "$fetching")
                                    i { attrClass("pixelarticons:user-plus") }
                                    text("Reset")
                                }
                            }
                        }
                    }
                }
            }
        }.toString()
