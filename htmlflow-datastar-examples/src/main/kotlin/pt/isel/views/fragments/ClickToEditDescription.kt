package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfClickToEditDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Click To Edit — Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {
                        li {
                            text(
                                "GET /click-to-edit/edit — Enters edit mode. " +
                                    "Triggered by the 'Edit' button. " +
                                    "Response (text/event-stream): HTML patch replacing the demo div " +
                                    "with an edit form pre-filled with the current user data and signals for firstName, lastName and email.",
                            )
                        }
                        li {
                            text(
                                "PUT /click-to-edit — Saves the edited user data. " +
                                    "Triggered by the 'Save' button. " +
                                    "Request body (application/json): updated signals (firstName, lastName, email). " +
                                    "Response (text/event-stream): HTML patch replacing the demo div with the display view showing the updated profile.",
                            )
                        }
                        li {
                            text(
                                "GET /click-to-edit/cancel — Cancels editing without saving. " +
                                    "Triggered by the 'Cancel' button. " +
                                    "Response (text/event-stream): HTML patch replacing the demo div with the display view showing the unchanged profile.",
                            )
                        }
                        li {
                            text(
                                "PATCH /click-to-edit/reset — Resets the profile to default values. " +
                                    "Triggered by the 'Reset' button. " +
                                    "Response (text/event-stream): HTML patch replacing the demo div with the display view showing the default profile.",
                            )
                        }
                    }
                }
            }
        }.toString()
