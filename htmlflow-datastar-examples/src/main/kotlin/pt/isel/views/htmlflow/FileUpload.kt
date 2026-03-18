@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.*
import pt.isel.datastar.events.Click
import pt.isel.datastar.expressions.post
import pt.isel.datastar.extensions.*

val hfFileUpload: String =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("/js/datastar.js")
                        }
                        link {
                            attrRel(EnumRelType.STYLESHEET)
                            attrHref("/css/styles.css")
                        }
                    }
                    body {
                        val files = dataSignal("files")
                        label {
                            p { text("Pick anything less than 1MB") }
                            input {
                                attrType(EnumTypeInputType.FILE)
                                dataBind(files)
                                attrMultiple(true)
                            }
                        }
                        button {
                            attrClass("warning")
                            dataOn(Click, "$files.length && ${post("/file-upload")}")
                            dataAttr("aria-disabled", $$"`${!$$files.length}`")
                            text("Submit")
                        }
                        div {
                            attrId("file-upload")
                            attrHidden(true)
                        }
                    }
                }
            }
        }.toString()

@Path("/file-upload")
private fun uploadFiles() {}
