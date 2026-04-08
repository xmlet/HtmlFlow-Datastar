package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.a
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
                            text("GET ")
                            a {
                                attrHref("/click-to-load/more")
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
                }
            }
        }.toString()
