package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfFileUploadDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("File Upload - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {

                        li {
                            text(
                                "POST /file-upload — Uploads and validates files. " +
                                    "Triggered by the 'Submit' button. " +
                                    "Request body (application/json): Files signal containing name, Base64 contents, and MIME type. " +
                                    "Response (text/event-stream): Validates file sizes (max 1MB), logs invalid files via executeScript, and patches '#file-upload' with a table of valid files.",
                            )
                        }
                    }
                }
            }
        }.toString()
