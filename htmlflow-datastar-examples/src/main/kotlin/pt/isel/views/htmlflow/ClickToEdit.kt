package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn

val hfClickToEdit =
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
                            p { text("First Name: John") }
                            p { text("Last Name: Doe") }
                            p { text("Email: joe@blow.com") }
                            div {
                                button {
                                    attrClass("info")
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled", "$fetching")
                                    dataOn("click", "@get('/click-to-edit/edit')")
                                    text("Edit")
                                }
                                button {
                                    attrClass("warning")
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled", "$fetching")
                                    dataOn("click", "@patch('/click-to-edit/reset')")
                                    text("Reset")
                                }
                            }
                        }
                    }
                }
            }
        }.toString()
