package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.code
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfCounterDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Counter - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {
                        li {
                            text("GET ")
                            a {
                                attrHref("/counter/events")
                                text("/counter/events")
                            }
                            text(
                                " — Opens a persistent SSE stream with the current counter value. " +
                                    "Triggered on page init (dataInit). " +
                                    "Response (text/event-stream): HTML patch replacing the counter span on every counter change. " +
                                    "Also executes a script alert when the counter reaches 3.",
                            )
                        }

                        li {
                            text(
                                "POST /counter/increment — Increments the counter by 1. " +
                                    "Triggered by the '+' button. " +
                                    "Response: 204 No Content. The /events SSE stream picks up the change and patches the counter span.",
                            )
                        }

                        li {
                            text(
                                "POST /counter/decrement — Decrements the counter by 1. " +
                                    "Triggered by the '−' button. " +
                                    "Response: 204 No Content. The /events SSE stream picks up the change and patches the counter span.",
                            )
                        }
                    }
                    p { text("For Http4k limitations use the following curl command:") }
                    code {
                        text(
                            "curl -N -H \"Accept: text/event-stream\" \"http://localhost:8070/counter/events\"",
                        )
                    }
                }
            }
        }.toString()
