package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.code
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfActiveSearchDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")
                    h2 { text("Active Search — Description") }
                    p { text("This page performs the following fetch requests:") }
                    ul {
                        li {
                            text("GET ")
                            a {
                                attrHref("/active-search/search")
                                text("/active-search/search")
                            }
                            text(
                                " — Searches and filters the contacts table. " +
                                    "Triggered by the search input field (debounced 200ms). " +
                                    "Query parameter: datastar (contains signal: search string). " +
                                    "Response (text/event-stream): HTML patch replacing the contacts tbody " +
                                    "with the filtered rows matching first or last name.",
                            )
                        }
                    }
                    p { text("For Http4k limitations use the following curl command:") }
                    code {
                        text(
                            "curl -N -H \"Accept: text/event-stream\" \"http://localhost:8070/active-search/search?datastar=%7B%22search%22%3A%22A%22%7D\"",
                        )
                    }
                }
            }
        }.toString()
