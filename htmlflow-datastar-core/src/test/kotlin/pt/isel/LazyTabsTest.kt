package pt.isel

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.*
import pt.isel.datastar.extensions.dataOn
import kotlin.test.Test
import kotlin.test.assertEquals

class LazyTabsTest {
    @Test
    fun `Lazy Tabs of the Datastar Frontend Reactivity`() {
        val out = demoDastarRx
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
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
                                attrId("demo")
                                div {
                                    addAttr("role", "tablist")
                                    button {
                                        addAttr("role", "tab")
                                        addAttr("aria-selected", "true")
                                        dataOn("click", "@get('/examples/lazy_tabs/0')")
                                        text("Tab 0")
                                    }
                                    button {
                                        addAttr("role", "tab")
                                        addAttr("aria-selected", "false")
                                        dataOn("click", "@get('/examples/lazy_tabs/1')")
                                        text("Tab 1")
                                    }
                                    button {
                                        addAttr("role", "tab")
                                        addAttr("aria-selected", "false")
                                        dataOn("click", "@get('/examples/lazy_tabs/2')")
                                        text("Tab 2")
                                    }
                                }
                                div {
                                    addAttr("role", "tabpanel")
                                    p { text("Lorem ipsum dolor sit amet...") }
                                    p { text("Consectetur adipiscing elit...") }
                                }
                            }
                        }
                    }
                }
            }

    private val expectedDatastarRx = """
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js">
        </script>
    </head>
<body>
    <div id="demo"> 
        <div role="tablist">
            <button role="tab" aria-selected="true" data-on:click="@get('/examples/lazy_tabs/0')">
                Tab 0
            </button>
            <button role="tab" aria-selected="false" data-on:click="@get('/examples/lazy_tabs/1')">
                Tab 1
            </button>
            <button role="tab" aria-selected="false" data-on:click="@get('/examples/lazy_tabs/2')">
                Tab 2
            </button>
        </div>
        <div role="tabpanel">
            <p>
                Lorem ipsum dolor sit amet...
            </p>
            <p>
                Consectetur adipiscing elit...
            </p>
        </div>
    </div>
</body>
</html>
    """
}
