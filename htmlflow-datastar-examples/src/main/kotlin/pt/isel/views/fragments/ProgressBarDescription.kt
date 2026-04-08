package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfProgressBarDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Progress Bar - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {
                        li {
                            text("GET ")
                            a {
                                attrHref("/progress-bar/updates")
                                text("/progress-bar/updates")
                            }
                            text(
                                " — Starts progress bar updates. " +
                                    "Triggered on page load via or clicking button to restart the progress bar. " +
                                    "Response (text/event-stream): Streams server-sent events that patch the progress bar with updated values until completion (100%).",
                            )
                        }
                    }
                }
            }
        }.toString()
