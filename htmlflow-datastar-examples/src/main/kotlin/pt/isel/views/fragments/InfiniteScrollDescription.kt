package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.code
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
                            text("GET ({\"offset\":10,\"limit\":5}) ")
                            a {
                                attrHref("/infinite-scroll/more?datastar=%7B%22offset%22%3A10%2C%22limit%22%3A5%7D")
                                text("/infinite-scroll/more")
                            }
                            text(
                                " — Loads additional rows for infinite scroll. " +
                                    "Triggered automatically when the 'Loading...' element enters the viewport. " +
                                    "Query parameters: offset and limit for pagination. " +
                                    "Response (text/event-stream): HTML patch that appends additional rows to the table '#agents'.",
                            )
                        }
                    }
                    p { text("For Http4k limitations use the following curl command:") }
                    code {
                        text(
                            "curl -N -H \"Accept: text/event-stream\" \"http://localhost:8070/infinite-scroll/more?datastar=%7B%22offset%22%3A10%2C%22limit%22%3A5%7D\"",
                        )
                    }
                }
            }
        }.toString()
