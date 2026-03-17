package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
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
import pt.isel.datastar.expressions.get
import pt.isel.datastar.expressions.not
import pt.isel.datastar.expressions.put
import pt.isel.datastar.expressions.semiColon
import pt.isel.datastar.expressions.setValue
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataClass
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataInit
import pt.isel.datastar.extensions.dataOn
import pt.isel.datastar.extensions.dataSignals
import pt.isel.datastar.extensions.dataText

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
                        div {
                            val (firstName, lastName, email, editing) =
                                dataSignals(
                                    "firstName" to "John",
                                    "lastName" to "Doe",
                                    "email" to "joe@blow.com",
                                    "_editing" to false,
                                )
                            dataInit("@get('/click-to-edit-signals/events')")
                            div {
                                attrId("Info")
                                dataClass("hidden", editing)
                                p {
                                    text("First Name: ")
                                    span { dataText(firstName) }
                                }
                                p {
                                    text("Last Name: ")
                                    span { dataText(lastName) }
                                }
                                p {
                                    text("Email: ")
                                    span { dataText(email) }
                                }
                                div {
                                    button {
                                        attrId("edit")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled", fetching)
                                        dataOn("click", editing setValue true)
                                        text("Edit")
                                    }
                                    button {
                                        attrId("reset")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled", "$fetching")
                                        dataOn("click", "@patch('/click-to-edit-signals/reset')")
                                        text("Reset")
                                    }
                                }
                            }
                            div {
                                attrId("edit-form")
                                dataClass("hidden", !editing)
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
                                        attrId("save")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled", "$fetching")
                                        dataOn("click", editing setValue false semiColon put(::save))
                                        text("Save")
                                    }
                                    button {
                                        attrId("cancel")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled", "$fetching")
                                        dataOn("click", editing setValue false semiColon get(::cancelEdit))
                                        text("Cancel")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.toString()

@Path("/click-to-edit-signals/edit")
fun edit() {}

@Path("/click-to-edit-signals/cancel")
fun cancelEdit() {}

@Path("/click-to-edit-signals")
fun save() {}
