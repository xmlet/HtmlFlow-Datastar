package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfCounterSignalsDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Counter via Signals - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {
                        li {
                            text("GET ")
                            a {
                                attrHref("/counter-signals/events")
                                text("/counter-signals/events")
                            }
                            text(
                                " — Opens a persistent SSE stream with the current counter value. " +
                                    "Triggered on page init (dataInit). " +
                                    "Response (text/event-stream): signal patch with the updated count value on every counter change. " +
                                    "The counter span updates reactively via the bound signal.",
                            )
                        }

                        li {
                            text(
                                "POST /counter-signals/increment — Increments the counter by 1. " +
                                    "Triggered by the '+' button. " +
                                    "Response: 204 No Content. The /events SSE stream picks up the change and patches the count signal.",
                            )
                        }

                        li {
                            text(
                                "POST /counter-signals/decrement — Decrements the counter by 1. " +
                                    "Triggered by the '−' button. " +
                                    "Response: 204 No Content. The /events SSE stream picks up the change and patches the count signal.",
                            )
                        }
                    }
                }
            }
        }.toString()
