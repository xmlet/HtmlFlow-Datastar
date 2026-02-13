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
import pt.isel.Profile
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn

val hfClickToEdit: HtmlView<Profile> =
    view {
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
                    dyn { profile: Profile ->
                        p { text("First Name: ${profile.firstName}") }
                        p { text("Last Name: ${profile.lastName}") }
                        p { text("Email: ${profile.email}") }
                    }
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

val hfEditModeDoc =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("demo")
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
                            dataBind("email")
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
                            dataOn("click", "@put('/click-to-edit')")
                            text("Save")
                        }
                        button {
                            attrClass("error")
                            val fetching = dataIndicator("_fetching")
                            dataAttr("disabled", "$fetching")
                            dataOn("click", "@get('/click-to-edit/cancel')")
                            text("Cancel")
                        }
                    }
                }
            }
        }.toString()
