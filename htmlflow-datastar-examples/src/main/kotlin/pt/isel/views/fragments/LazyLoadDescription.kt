package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.code
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
                            text("GET ")
                            a {
                                attrHref("/lazy-load/graph")
                                text("/lazy-load/graph")
                            }
                            text(
                                " — Loads a lazy-loaded graph image. " +
                                    "Triggered on page load. " +
                                    "Response (text/event-stream): Intentionally delayed to simulate a slow resource, then replaces the 'Loading...' placeholder with an image element rendered on the server.",
                            )
                        }
                        p { text("For Http4k limitations use the following curl command:") }
                        code {
                            text(
                                "curl -N -H \"Accept: text/event-stream\" \"http://localhost:8070/lazy-load/graph\"",
                            )
                        }
                    }
                }
            }
        }.toString()
