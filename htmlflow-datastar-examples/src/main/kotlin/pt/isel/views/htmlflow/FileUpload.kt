package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.label
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataOn
import pt.isel.datastar.extensions.dataSignal

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
                        label {
                            p { text("Pick anything less than 1MB") }
                            input {
                                attrType(EnumTypeInputType.FILE)
                                dataBind("files multiple")
                            }
                        }
                        button {
                            attrClass("warning")
                            val files = dataSignal("files")
                            dataOn("click", "$files.length && @post('/file-upload')")
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
