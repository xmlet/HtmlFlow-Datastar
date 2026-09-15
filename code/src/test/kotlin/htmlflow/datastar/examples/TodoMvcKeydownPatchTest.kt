package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Keydown
import kotlin.test.Test
import kotlin.test.assertEquals

class TodoMvcKeydownPatchTest {
    @Test
    fun `Todo MVC keydown patch renders expected html`() {
        val out = demoDatastarRx
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDatastarRx =
        StringBuilder()
            .apply {
                doc {
                    html {
                        head {
                            script {
                                attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                            }
                        }
                        body {
                            div {
                                input {
                                    attrType(EnumTypeInputType.TEXT)
                                    val input = dataBind("input")
                                    dataOn(Keydown) {
                                        +$$"if (evt.key !== 'Enter' || !$input.trim()) return"
                                        patch("/todo-mvc/-1")
                                        input.setValue(" ")
                                    }
                                }
                            }
                        }
                    }
                }
            }

    private val expectedDatastarRx =
        """
        <!DOCTYPE html>
        <html>
            <head>
                <script src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div>
                    <input type="text" data-bind="input" data-on:keydown="if (evt.key !== 'Enter' || !${'$'}input.trim()) return; @patch('/todo-mvc/-1'); ${'$'}input = " "">
                </div>
            </body>
        </html>
        """.trimIndent()
}
