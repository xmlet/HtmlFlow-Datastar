package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.code
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfClickToLoadDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Click To Load - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {
                        li {
                            text("GET ({\"offset\":0,\"limit\":5})")
                            a {
                                attrHref("/click-to-load/more?datastar=%7B%22offset%22%3A0%2C%22limit%22%3A5%7D")
                                text("/click-to-load/more")
                            }
                            text(
                                " — Loads the next page of agents and appends them to the table. " +
                                    "Triggered by the 'Load More' button (disabled while fetching). " +
                                    "Query parameter: datastar (contains signals: offset and limit). " +
                                    "Response (text/event-stream): signal patch updating the offset to offset + limit, " +
                                    "and HTML patch appending the new agent rows to the agents tbody. " +
                                    "Includes a simulated delay of 1 second.",
                            )
                        }
                    }
                    p { text("For Http4k limitations use the following curl command:") }
                    code {
                        text(
                            "curl -N -H \"Accept: text/event-stream\" \"http://localhost:8070/click-to-load/more?datastar=%7B%22offset%22%3A0%2C%22limit%22%3A5%7D\"",
                        )
                    }
                }
            }
        }.toString()
