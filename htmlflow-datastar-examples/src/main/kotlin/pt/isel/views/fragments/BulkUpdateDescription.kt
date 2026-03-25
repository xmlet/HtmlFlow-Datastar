package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.br
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfBulkUpdateDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("Bulk Update — Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {
                        li {
                            text(
                                "PUT /bulk-update/activate — Activates all selected users. " +
                                    "Triggered by the 'Activate' button. " +
                                    "Request body (application/json): selections (list of booleans, one per row). " +
                                    "Response (text/event-stream): Signal patch resetting all selections to false " +
                                    "and HTML patch replacing the users tbody with updated rows reflecting ACTIVE status.",
                            )
                        }
                        li {
                            text(
                                "PUT /bulk-update/deactivate — Deactivates all selected users. " +
                                    "Triggered by the 'Deactivate' button. " +
                                    "Request body (application/json): selections (list of booleans, one per row). " +
                                    "Response (text/event-stream): Signal patch resetting all selections to false " +
                                    "and HTML patch replacing the users tbody with updated rows reflecting INACTIVE status.",
                            )
                        }
                    }
                }
            }
        }.toString()
