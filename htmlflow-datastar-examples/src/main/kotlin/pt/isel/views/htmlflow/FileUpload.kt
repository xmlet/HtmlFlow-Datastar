@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.div
import htmlflow.doc
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.*
import pt.isel.datastar.expressions.post
import pt.isel.datastar.extensions.*
import pt.isel.ktor.FileSignal
import pt.isel.ktor.md5
import pt.isel.utils.loadResource
import kotlin.io.encoding.Base64

private val description = loadResource("public/html/fragments/file-upload-description.html")

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
                            dataOn("click", "$files.length && ${post("/file-upload")}")
                            dataAttr("aria-disabled", $$"`${!$$files.length}`")
                            text("Submit")
                        }
                        div {
                            attrId("file-upload")
                            attrHidden(true)
                        }
                        raw(description)
                    }
                }
            }
        }.toString()

fun fileUploadTable(): HtmlView<List<FileSignal>> =
    view {
        div {
            attrId("file-upload")
            table {
                attrId("files")
                thead {
                    tr {
                        th { text("Name") }
                        th { text("Size") }
                        th { text("MIME Type") }
                        th { text("MD5 Hash") }
                    }
                }
                tbody {
                    dyn { files: List<FileSignal> ->
                        files.forEach { (name, contents, mime) ->
                            tr {
                                val plainText = Base64.decode(contents).decodeToString()
                                val textSize = plainText.length
                                val md5Hash = contents.md5()
                                td { text(name) }
                                td { text(textSize) }
                                td { text(mime) }
                                td { text(md5Hash) }
                            }
                        }
                    }
                }
            }
        }
    }

@Path("/file-upload")
private fun uploadFiles() {}
