package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.code
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfLazyTabsDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Lazy Tabs - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {
                        li {
                            text(
                                "GET /lazy-tabs/{index} — Loads tab content on demand. " +
                                    "Triggered when clicking a tab button. " +
                                    "Path parameter: index (tab identifier). " +
                                    "Response (text/event-stream): HTML patch that replaces the '#tabpanel' content with the selected tab panel.",
                            )
                        }
                    }
                    p { text("For Http4k limitations use the following curl command:") }
                    text("Choose a tab index from 0 to 7 and run: ")
                    code {
                        text(
                            "curl -N -H \"Accept: text/event-stream\" \"http://localhost:8070/lazy-tabs/(0..7)\"",
                        )
                    }
                }
            }
        }.toString()
