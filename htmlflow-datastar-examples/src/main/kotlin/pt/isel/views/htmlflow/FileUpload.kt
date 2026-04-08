package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.div
import htmlflow.doc
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.Tbody
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.label
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.events.Click
import pt.isel.http4k.getFileUploadDescription
import pt.isel.http4k.uploadFile
import pt.isel.ktor.FileInfo

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
                        div {
                            attrId("description")
                            dataInit { +get(::getFileUploadDescription) }
                        }
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
                            dataOn(Click) {
                                +"$files.length"
                                +post(::uploadFile)
                            }
                            dataAttr("aria-disabled") { +$$"`${!$$files.length}`" }
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

val fileUploadTable: HtmlView<List<FileInfo>> =
    view<List<FileInfo>> {
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
                    dyn { files: List<FileInfo> ->
                        files.forEach { file ->
                            fileRow(file)
                        }
                    }
                }
            }
        }
    }

fun Tbody<*>.fileRow(file: FileInfo) =
    tr {
        td { text(file.name) }
        td { text(file.textSize) }
        td { text(file.mime) }
        td { text(file.md5Hash) }
    }
