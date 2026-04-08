package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfInlineValidationDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Inline Validation - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {

                        li {
                            text(
                                "POST /inline-validation/validate — Validates form input per field. " +
                                    "Triggered after every 500 ms of a key debounce. " +
                                    "Request body (application/json): Form data (email, first name, last name). " +
                                    "Response (text/event-stream): Only patches the errors for each individual field. " +
                                    "Removes error messages singularly for each field that is blank or passes validation, " +
                                    "instead of updating or clearing the entire form at once.",
                            )
                        }

                        li {
                            text(
                                "POST /inline-validation — Sign up form input. " +
                                    "Triggered when clicking 'Submit'. " +
                                    "Request body : Nothing " +
                                    "Response (text/event-stream): HTML patch with Thank you message after signup.",
                            )
                        }
                    }
                }
            }
        }.toString()
