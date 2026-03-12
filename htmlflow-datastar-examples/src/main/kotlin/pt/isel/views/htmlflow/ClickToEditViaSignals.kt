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
import org.xmlet.htmlapifaster.span
import pt.isel.datastar.expressions.not
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataClass
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataInit
import pt.isel.datastar.extensions.dataOn
import pt.isel.datastar.extensions.dataSignals
import pt.isel.datastar.extensions.dataText
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3

val hfClickToEditSignals =
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
                        val (firstName, lastName, email, editMode) =
                            dataSignals(
                                "firstName" to "John",
                                "lastName" to "Doe",
                                "email" to "joe@blow.com",
                                "editMode" to false,
                            )
                        dataInit("@get('/click-to-edit-signals/events')")
                        div {
                            attrId("Info")
                            dataClass("hidden", editMode)
                            p {
                                text("First Name: ")
                                span { dataText("$firstName") }
                            }
                            p {
                                text("Last Name: ")
                                span { dataText("$lastName") }
                            }
                            p {
                                text("Email: ")
                                span { dataText("$email") }
                            }
                            div {
                                button {
                                    attrClass("info")
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled", "$fetching")
                                    dataOn("click", "@get('/click-to-edit-signals/edit')")
                                    text("Edit")
                                }
                                button {
                                    attrClass("warning")
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled", "$fetching")
                                    dataOn("click", "@patch('/click-to-edit-signals/reset')")
                                    text("Reset")
                                }
                            }
                        }
                        div {
                            attrId("edit-form")
                            dataClass("hidden", !editMode)
                            label {
                                text("First Name")
                                input {
                                    attrType(EnumTypeInputType.TEXT)
                                    dataBind("first-name")
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled", "$fetching")
                                }
                            }
                            label {
                                text("Last Name")
                                input {
                                    attrType(EnumTypeInputType.TEXT)
                                    dataBind("last-name")
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled", "$fetching")
                                }
                            }
                            label {
                                text("Email")
                                input {
                                    attrType(EnumTypeInputType.EMAIL)
                                    dataBind(email)
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled", "$fetching")
                                }
                            }
                            div {
                                addAttr("role", "group")
                                button {
                                    attrClass("success")
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled", "$fetching")
                                    dataOn("click", "@put('/click-to-edit-signals')")
                                    text("Save")
                                }
                                button {
                                    attrClass("error")
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled", "$fetching")
                                    dataOn("click", "@get('/click-to-edit-signals/cancel')")
                                    text("Cancel")
                                }
                            }
                        }
                    }
                }
            }
        }.toString()
