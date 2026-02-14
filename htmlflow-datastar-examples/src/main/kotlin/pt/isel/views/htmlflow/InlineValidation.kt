package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.div
import htmlflow.doc
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import org.xmlet.htmlapifaster.Div
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
import pt.isel.ktor.InlineValidationSignals
import kotlin.time.Duration.Companion.milliseconds

val hfInlineValidationView: HtmlView<InlineValidationSignals> =
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
                    inputFields()
                }
            }
        }
    }

fun Div<*>.inputFields() {
    dyn { (email, firstName, lastName): InlineValidationSignals ->
        val isEmailValid = email == "test@test.com"
        val isFirstNameValid = firstName.length >= 2
        val isLastNameValid = lastName.length >= 2
        val canSubmit = isEmailValid && isFirstNameValid && isLastNameValid

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
        if (!isEmailValid && email.isNotBlank()) {
            p {
                attrId("email-error")
                attrClass("error")
                text("Email $email is already taken or invalid. Please enter another email.")
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
        if (firstName.isNotBlank() && !isFirstNameValid) {
            p {
                attrId("first-name-error")
                attrClass("error")
                text("First name must be at least 2 characters.")
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
        if (lastName.isNotBlank() && !isLastNameValid) {
            p {
                attrId("last-name-error")
                attrClass("error")
                text("Last name must be at least 2 characters.")
            }
        }
        button {
            attrClass("success")
            dataOn("click", "@post('/inline-validation')")
            if (!canSubmit) addAttr("aria-disabled", "true")
            text(" Sign Up")
        }
    }
}

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
