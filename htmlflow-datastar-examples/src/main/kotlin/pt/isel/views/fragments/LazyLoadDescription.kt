package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfLazyLoadDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Lazy Load - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {
                        li {
                            text(
                                "GET /lazy-load/graph — Loads a lazy-loaded graph image. " +
                                    "Triggered on page load. " +
                                    "Response (text/event-stream): Intentionally delayed to simulate a slow resource, then replaces the 'Loading...' placeholder with an image element rendered on the server.",
                            )
                        }
                    }
                }
            }
        }.toString()
