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
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataClass
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignals
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.Click
import pt.isel.http4k.clickToEditSignalsCancel
import pt.isel.http4k.clickToEditSignalsReset
import pt.isel.http4k.clickToEditSignalsSave
import pt.isel.http4k.getClickToEditEvents
import pt.isel.http4k.getClickToEditSignalsDescription

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
                            attrId("description")
                            dataInit { get(::getClickToEditSignalsDescription) }
                        }
                        div {
                            val (firstName, lastName, email, editing) =
                                dataSignals(
                                    "firstName" to "John",
                                    "lastName" to "Doe",
                                    "email" to "joe@blow.com",
                                    "_editing" to false,
                                )
                            dataInit { get(::getClickToEditEvents) }
                            div {
                                attrId("Info")
                                dataClass("hidden") { +editing }
                                p {
                                    text("First Name: ")
                                    span { dataText { +firstName } }
                                }
                                p {
                                    text("Last Name: ")
                                    span { dataText { +lastName } }
                                }
                                p {
                                    text("Email: ")
                                    span { dataText { +email } }
                                }
                                div {
                                    button {
                                        attrId("edit")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled") { +fetching }
                                        dataOn(Click) {
                                            editing.setValue(true)
                                        }
                                        text("Edit")
                                    }
                                    button {
                                        attrId("reset")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled") { +fetching }
                                        dataOn(Click) {
                                            patch(::clickToEditSignalsReset)
                                        }
                                        text("Reset")
                                    }
                                }
                            }
                            div {
                                attrId("edit-form")
                                dataClass("hidden") { !editing }
                                label {
                                    text("First Name")
                                    input {
                                        attrType(EnumTypeInputType.TEXT)
                                        dataBind("first-name")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled") { +fetching }
                                    }
                                }
                                label {
                                    text("Last Name")
                                    input {
                                        attrType(EnumTypeInputType.TEXT)
                                        dataBind("last-name")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled") { +fetching }
                                    }
                                }
                                label {
                                    text("Email")
                                    input {
                                        attrType(EnumTypeInputType.EMAIL)
                                        dataBind(email)
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled") { +fetching }
                                    }
                                }
                                div {
                                    addAttr("role", "group")
                                    button {
                                        attrId("save")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled") { +fetching }
                                        dataOn(Click) {
                                            editing.setValue(false)
                                            put(::clickToEditSignalsSave)
                                        }
                                        text("Save")
                                    }
                                    button {
                                        attrId("cancel")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled") { +fetching }
                                        dataOn(Click) {
                                            editing.setValue(false)
                                            get(::clickToEditSignalsCancel)
                                        }
                                        text("Cancel")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.toString()
