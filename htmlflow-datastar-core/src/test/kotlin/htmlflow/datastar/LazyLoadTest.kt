@file:Suppress("ktlint:standard:no-wildcard-imports")

package htmlflow.datastar

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.*
import pt.isel.datastar.extensions.dataInit
import kotlin.test.Test
import kotlin.test.assertEquals

class LazyLoadTest {
    @Test
    fun `Lazy Load of the Datastar Frontend Reactivity`() {
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
                                attrId("graph")
                                dataInit("@get('/examples/lazy_load/graph')")
                                text("Loading...")
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
    <div id="graph" data-init="@get('/examples/lazy_load/graph')"> 
        Loading...
    </div>
</body>
</html>
    """
}
