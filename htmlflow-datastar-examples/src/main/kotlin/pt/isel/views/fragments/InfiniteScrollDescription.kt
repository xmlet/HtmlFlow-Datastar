package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfInfiniteScrollDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Infinite Scroll - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {
                        li {
                            text(
                                "GET /infinite-scroll/more — Loads additional rows for infinite scroll. " +
                                    "Triggered automatically when the 'Loading...' element enters the viewport. " +
                                    "Query parameters: offset and limit for pagination. " +
                                    "Response (text/event-stream): HTML patch that appends additional rows to the table '#agents'.",
                            )
                        }
                    }
                }
            }
        }.toString()
