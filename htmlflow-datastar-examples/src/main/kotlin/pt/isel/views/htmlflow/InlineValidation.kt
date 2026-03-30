package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.div
import htmlflow.doc
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
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
import pt.isel.datastar.expressions.get
import pt.isel.datastar.expressions.post
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataInit
import pt.isel.datastar.extensions.dataOn
import pt.isel.datastar.extensions.dataSignal
import pt.isel.http4k.getInlineValidationDescription
import pt.isel.http4k.submitForm
import pt.isel.http4k.validateFields
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
                            attrId("description")
                            dataInit(get(::getInlineValidationDescription))
                        }
                        div {
                            attrId("demo")
                            val error = dataSignal("_error", true)
                            label {
                                text("Email Address")
                                input {
                                    attrType(EnumTypeInputType.EMAIL)
                                    attrRequired(true)
                                    addAttr("aria-live", "polite")
                                    addAttr("aria-describedby", "email-info")
                                    dataBind("email")
                                    dataOn("keydown", post(::validateFields)) {
                                        mods { debounce(500.milliseconds) }
                                    }
                                }
                            }
                            div {
                                attrId("email-error")
                                attrClass("error")
                            }

                            p {
                                attrId("email-info-details")
                                raw("The only valid email address is \"test@test.com\".")
                            }

                            label {
                                text("First Name")
                                input {
                                    attrType(EnumTypeInputType.TEXT)
                                    attrRequired(true)
                                    addAttr("aria-live", "polite")
                                    dataBind("first-name")
                                    dataOn("keydown", post(::validateFields)) {
                                        mods { debounce(200.milliseconds) }
                                    }
                                }
                            }

                            div {
                                attrId("first-name-error")
                                attrClass("error")
                            }

                            label {
                                text("Last Name")
                                input {
                                    attrType(EnumTypeInputType.TEXT)
                                    attrRequired(true)
                                    addAttr("aria-live", "polite")
                                    dataBind("last-name")
                                    dataOn("keydown", post(::validateFields)) {
                                        mods { debounce(500.milliseconds) }
                                    }
                                }
                            }

                            div {
                                attrId("last-name-error")
                                attrClass("error")
                            }
                            button {
                                attrClass("success")
                                dataOn("click", post(::submitForm))
                                dataAttr("disabled", error)
                                text("Sign Up")
                            }
                        }
                    }
                }
            }
        }.toString()

val hfSignUpDoc =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("demo")
                    text("Thank you for signing up!")
                }
            }
        }.toString()

val hfFirstNameErrorFragment =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("first-name-error")
                    p {
                        attrId("first-name-error-details")
                        text("First name must be at least 2 characters.")
                    }
                }
            }
        }.toString()

val hfLastNameErrorFragment =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("last-name-error")
                    p {
                        attrId("last-name-error-details")
                        text("Last name must be at least 2 characters.")
                    }
                }
            }
        }.toString()

val hfEmailErrorFragment: HtmlView<String> =
    view<String> {
        div {
            attrClass("email-error")
            p {
                attrId("email-error-details")
                dyn { email: String ->
                    text("Email '$email' is already taken or invalid. Please enter another email.")
                }
            }
        }
    }
