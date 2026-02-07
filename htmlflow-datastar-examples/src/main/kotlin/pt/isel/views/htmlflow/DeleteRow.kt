package pt.isel.views.htmlflow

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.i
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn

val hfDeleteRow =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            table {
                                thead {
                                    tr {
                                        th { text("Name") }
                                        th { text("Email") }
                                        th { text("Actions") }
                                    }
                                }
                                tbody {
                                    tr {
                                        td { text("Joe Smith") }
                                        td { text("joe@smith.org") }
                                        td {
                                            button {
                                                attrClass("error")
                                                dataOn("click", "confirm('Are you sure?') && @delete('/delete-row/0')")
                                                val fetching = dataIndicator("_fetching")
                                                dataAttr("disabled", "$fetching")
                                                text("Delete")
                                            }
                                        }
                                    }
                                    tr {
                                        td { text("Angie MacDowell") }
                                        td { text("angie@macdowell.org") }
                                        td {
                                            button {
                                                attrClass("error")
                                                dataOn("click", "confirm('Are you sure?') && @delete('/delete-row/1')")
                                                val fetching = dataIndicator("_fetching")
                                                dataAttr("disabled", "$fetching")
                                                text("Delete")
                                            }
                                        }
                                    }
                                    tr {
                                        td { text("Fuqua Tarkenton") }
                                        td { text("fuqua@tarkenton.org") }
                                        td {
                                            button {
                                                attrClass("error")
                                                dataOn("click", "confirm('Are you sure?') && @delete('/delete-row/2')")
                                                val fetching = dataIndicator("_fetching")
                                                dataAttr("disabled", "$fetching")
                                                text("Delete")
                                            }
                                        }
                                    }
                                    tr {
                                        td { text("Kim Yee") }
                                        td { text("kim@yee.org") }
                                        td {
                                            button {
                                                attrClass("error")
                                                dataOn("click", "confirm('Are you sure?') && @delete('/delete-row/3')")
                                                val fetching = dataIndicator("_fetching")
                                                dataAttr("disabled", "$fetching")
                                                text("Delete")
                                            }
                                        }
                                    }
                                }
                            }
                            div {
                                button {
                                    attrClass("warning")
                                    dataOn("click", "@patch('/delete-row/reset')")
                                    i { attrClass("pixelarticons:user-plus") }
                                    text("Reset")
                                }
                            }
                        }
                    }
                }
            }
        }.toString()
