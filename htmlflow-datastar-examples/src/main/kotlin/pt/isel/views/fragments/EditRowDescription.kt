package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.code
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfEditRowDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Edit Row - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {

                        li {
                            text(
                                "GET /edit-row/{index} — Enters edit mode for a row. " +
                                    "Triggered by the 'Edit' button. " +
                                    "Path parameter: index (row identifier). " +
                                    "Response (text/event-stream): Signal patch with user data and HTML patch to replace the row with editable input fields.",
                            )
                        }

                        li {
                            text(
                                "PATCH /edit-row/{index} — Saves edited row data. " +
                                    "Triggered by the 'Save' button. " +
                                    "Path parameter: index (row identifier). " +
                                    "Request body (application/json): Updated user data. " +
                                    "Response (text/event-stream): HTML patch to replace the row back to display mode.",
                            )
                        }

                        li {
                            text(
                                "GET /edit-row/cancel — Cancels row editing. " +
                                    "Triggered by the 'Cancel' button. " +
                                    "Query parameter: datastar (contains user data: idx, name, email). " +
                                    "Response (text/event-stream): HTML patch to restore the row to its default non-editing state.",
                            )
                        }

                        li {
                            text(
                                "PUT /edit-row/reset — Resets the user table. " +
                                    "Triggered by the 'Reset' button. " +
                                    "Response (text/event-stream): HTML patches for all rows with initial data.",
                            )
                        }
                    }
                    p { text("For Http4k limitations use the following curl command:") }
                    text("Choose a row index from 0 to 3 and run: ")
                    code {
                        text(
                            "curl -N -H \"Accept: text/event-stream\" \"http://localhost:8070/edit-row/(0..3)\"",
                        )
                    }
                }
            }
        }.toString()
