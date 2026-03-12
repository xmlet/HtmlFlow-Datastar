package pt.isel.views.htmlflow

import htmlflow.div
import htmlflow.doc
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import pt.isel.datastar.expressions.get
import pt.isel.datastar.extensions.dataOn

val TAB_CONTENTS =
    listOf(
        "Iusto dignissimos distinctio soluta hic perferendis. Tempora nihil exercitationem dicta ut nesciunt. Qui sit est vel deleniti eveniet. Sed quia sunt qui illo occaecati.",
        "Doloribus beatae quam odio facere est. Amet et error qui quas quibusdam. Est voluptatum qui tempora minima aut. Eos libero similique ut nemo maiores. Aliquam et dolor aut voluptatibus quis. Veritatis voluptas dignissimos distinctio earum qui. Suscipit ullam aut qui nobis consequatur. Et repellendus cupiditate explicabo eaque illo.",
        "Recusandae architecto temporibus sapiente fugit in. Dicta dolorem explicabo earum magnam ea. Est tempore nesciunt non molestiae ut.",
        "Doloribus est accusamus eveniet cupiditate aperiam. Rem laboriosam incidunt doloribus architecto quae. Aut omnis quasi eos consequatur corporis. Vel eum qui ipsum dolorum sequi. Inventore iste nesciunt accusantium ullam velit. Quae non et tenetur ullam aut.",
        "Quia quis atque nostrum beatae sit. Dicta a sit doloremque fugit id. Impedit quia repellendus sit magnam delectus.",
        "Ducimus ut mollitia aperiam sed nostrum. Vel earum culpa unde fugit quisquam. Sequi sapiente deserunt iusto exercitationem in.",
        "Deleniti magni rerum voluptatum explicabo in. Cupiditate aliquam repellendus illo voluptatem molestiae.",
        "Dolore deserunt aut ducimus et est.",
    )

val hfLazyTabs: String =
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
                        div {
                            attrId("lazy-tabs")
                            div {
                                addAttr("role", "tablist")
                                (0..7).forEach { i ->
                                    button {
                                        addAttr("role", "tab")
                                        addAttr("aria-selected", if (i == 0) "true" else "false")
                                        dataOn("click", get("/lazy-tabs/$i"))
                                        text("Tab $i")
                                    }
                                }
                            }
                            div {
                                attrId("tabpanel")
                                addAttr("role", "tabpanel")
                                p { text(TAB_CONTENTS[0]) }
                            }
                        }
                    }
                }
            }
        }.toString()

val hfTabPanelView =
    view<String> {
        div {
            attrId("tabpanel")
            addAttr("role", "tabpanel")
            dyn { content: String ->
                p { text(content) }
            }
        }
    }
