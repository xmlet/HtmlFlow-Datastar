package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.label
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataOn
import kotlin.time.Duration.Companion.milliseconds

val hfInlineValidation =
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
                            label {
                                text("Email Address")
                                input {
                                    attrType(EnumTypeInputType.EMAIL)
                                    attrRequired(true)
                                    addAttr("aria-live", "polite")
                                    addAttr("aria-describedby", "email-info")
                                    dataBind("email")
                                    dataOn("keydown", "@post('/inline-validation/validate')") {
                                        debounce(500.milliseconds)
                                    }
                                }
                            }
                            p {
                                attrId("email-info")
                                attrClass("info")
                                raw("The only valid email address is \"test@test.com\".")
                            }
                            label {
                                text("First Name")
                                input {
                                    attrType(EnumTypeInputType.TEXT)
                                    attrRequired(true)
                                    addAttr("aria-live", "polite")
                                    dataBind("first-name")
                                    dataOn("keydown", "@post('/inline-validation/validate')") {
                                        debounce(500.milliseconds)
                                    }
                                }
                            }
                            label {
                                text("Last Name")
                                input {
                                    attrType(EnumTypeInputType.TEXT)
                                    attrRequired(true)
                                    addAttr("aria-live", "polite")
                                    dataBind("last-name")
                                    dataOn("keydown", "@post('/inline-validation/validate')") {
                                        debounce(500.milliseconds)
                                    }
                                }
                            }
                            button {
                                attrClass("success")
                                dataOn("click", "@post('/inline-validation')")
                                text("Sign Up")
                            }
                        }
                    }
                }
            }
        }.toString()
