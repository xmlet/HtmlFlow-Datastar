package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.div
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.label
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignals
import org.xmlet.htmlflow.datastar.events.Click
import pt.isel.http4k.clickToEditCancel
import pt.isel.http4k.clickToEditReset
import pt.isel.http4k.clickToEditSave
import pt.isel.http4k.editProfile
import pt.isel.http4k.getClickToEditDescription
import pt.isel.ktor.Profile

val hfDisplayFragment: HtmlView<Profile> =
    view {
        div {
            attrId("demo")
            dyn { profile: Profile ->
                p { text("First Name: ${profile.firstName}") }
                p { text("Last Name: ${profile.lastName}") }
                p { text("Email: ${profile.email}") }
            }
            div {
                button {
                    attrId("edit")
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled") { +fetching }
                    dataOn(Click) {
                        +get(::editProfile)
                    }
                    text("Edit")
                }
                button {
                    attrId("reset")
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled") { +fetching }
                    dataOn(Click) {
                        +patch(::clickToEditReset)
                    }
                    text("Reset")
                }
            }
        }
    }

val hfEditModeFragment: HtmlView<Profile> =
    view {
        div {
            attrId("demo")
            dyn { profile: Profile ->
                dataSignals(
                    "firstName" to profile.firstName,
                    "lastName" to profile.lastName,
                    "email" to profile.email,
                )
            }
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
                    dataBind("email")
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled") { +fetching }
                }
            }
            div {
                addAttr("role", "group")
                button {
                    attrId("save")
                    attrClass("success")
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled") { +fetching }
                    dataOn(Click) {
                        +put(::clickToEditSave)
                    }
                    text("Save")
                }
                button {
                    attrId("cancel")
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled") { +fetching }
                    dataOn(Click) {
                        +get(::clickToEditCancel)
                    }
                    text("Cancel")
                }
            }
        }
    }

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
                    attrId("description")
                    dataInit { +get(::getClickToEditDescription) }
                }
                dyn { profile: Profile ->
                    raw(hfDisplayFragment.render(profile))
                }
            }
        }
    }
