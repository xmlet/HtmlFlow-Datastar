package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfDeleteRowDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Delete Row - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {
                        li {
                            text(
                                "DELETE /delete-row/{index} — Deletes a row from the table. " +
                                    "Triggered by the 'Delete' button (with a confirmation dialog). " +
                                    "Path parameter: index (row identifier). " +
                                    "Response (text/event-stream): HTML patch removing the row element with id row-{index} from the DOM.",
                            )
                        }

                        li {
                            text(
                                "PATCH /delete-row/reset — Resets the table to its default state with all rows restored. " +
                                    "Triggered by the 'Reset' button. " +
                                    "Response (text/event-stream): HTML patch replacing the users-table div with the full default table.",
                            )
                        }
                    }
                }
            }
        }.toString()
