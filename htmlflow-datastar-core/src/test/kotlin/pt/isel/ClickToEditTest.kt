@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel

import htmlflow.html
import htmlflow.view
import org.xmlet.htmlapifaster.*
import pt.isel.datastar.extensions.dataAttr
import pt.isel.datastar.extensions.dataIndicator
import pt.isel.datastar.extensions.dataOn
import kotlin.test.Test
import kotlin.test.assertEquals

class ClickToEditTest {
    @Test
    fun `Click To Edit of the Datastar Frontend Reactivity`() {
        val out = StringBuilder()
        demoDastarRx.setOut(out).write()
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
        view<Unit> {
            html {
                head {
                    script {
                        attrType(EnumTypeScriptType.MODULE)
                        attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js")
                    }
                }
                body {
                    div {
                        attrId("demo")
                        p { text("First Name: John") }
                        p { text("Last Name: Doe") }
                        p { text("Email: joe@blow.com") }
                        div {
                            button {
                                attrClass("info")
                                val fetching = dataIndicator("_fetching")
                                dataAttr("disabled", "$fetching")
                                dataOn("click", "@get('/examples/click_to_edit/edit')")
                                text("Edit")
                            }
                            button {
                                attrClass("warning")
                                val fetching = dataIndicator("_fetching")
                                dataAttr("disabled", "$fetching")
                                dataOn("click", "@patch('/examples/click_to_edit/reset')")
                                text("Reset")
                            }
                        }
                    }
                }
            }
        }

    private val expectedDatastarRx = $$"""
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js">
        </script>
    </head>
<body>
    <div id="demo">
        <p>
            First Name: John
        </p>
        <p>
            Last Name: Doe
        </p>
        <p>
            Email: joe@blow.com
        </p>
        <div>
            <button class="info" data-indicator:_fetching="" data-attr:disabled="$fetching" data-on:click="@get('/examples/click_to_edit/edit')">
                Edit
            </button>
            <button class="warning" data-indicator:_fetching="" data-attr:disabled="$fetching" data-on:click="@patch('/examples/click_to_edit/reset')">
                Reset
            </button>
        </div>
    </div>
</body>
</html>
    """
}
