package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfProgressiveLoadDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Progressive Load - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {

                        li {
                            text("GET ")
                            a {
                                attrHref("/progressive-load/updates")
                                text("/progressive-load/updates")
                            }
                            text(
                                " — Progressively loads page content. " +
                                    "Triggered when clicking the 'Load' button. " +
                                    "Response (text/event-stream): Streams multiple server-sent events " +
                                    "with intentional delays that patch different sections of the page (header," +
                                    "article, comments, footer) sequentially. " +
                                    "Some updates replace sections entirely, while others append content to existing elements, demonstrating incremental rendering.",
                            )
                        }
                    }
                }
            }
        }.toString()
