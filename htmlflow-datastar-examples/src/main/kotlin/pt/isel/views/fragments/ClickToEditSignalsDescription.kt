package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfClickToEditSignalsDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Click To Edit Signals — Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {
                        li {
                            text(
                                "GET /click-to-edit-signals/events — Opens a persistent SSE stream with the current user signals. " +
                                    "Triggered on page init (dataInit). " +
                                    "Response (text/event-stream): continuous signal patches with firstName, lastName and email, " +
                                    "emitting immediately on connect and again on every server-side state change.",
                            )
                        }
                        li {
                            text(
                                "PUT /click-to-edit-signals — Saves the edited user data. " +
                                    "Triggered by the 'Save' button. " +
                                    "Request body (application/json): updated signals (firstName, lastName, email). " +
                                    "Response: 204 No Content. The /events SSE stream picks up the change and patches the signals automatically.",
                            )
                        }
                        li {
                            text(
                                "PATCH /click-to-edit-signals/reset — Resets the user data to default values. " +
                                    "Triggered by the 'Reset' button. " +
                                    "Response: 204 No Content. The /events SSE stream emits the reset signal values.",
                            )
                        }
                        li {
                            text(
                                "GET /click-to-edit-signals/cancel — Re-emits the current signal values to discard unsaved edits. " +
                                    "Triggered by the 'Cancel' button. " +
                                    "Response: 204 No Content. Forces the /events SSE stream to re-emit the unchanged values.",
                            )
                        }
                    }
                }
            }
        }.toString()
