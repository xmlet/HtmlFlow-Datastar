import htmlflow.div
import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlflow.datastar.attributes.dataOnSignalPatchFilter
import org.xmlet.htmlflow.datastar.expressions.SignalPatchFilter
import kotlin.test.Test
import kotlin.test.assertContains

class SignalPatchFilterTest {
    @Test
    fun `Checks include filter only`() {
        val filter =
            object : SignalPatchFilter {
                override val include = Regex("^counter$")
                override val exclude = null
            }

        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                dataOnSignalPatchFilter(filter)
                            }
                        }
                    }
                }.toString()

        assertContains(html, """data-on-signal-patch-filter="{include: /^counter$/}"""")
    }

    @Test
    fun `renders include and exclude filters`() {
        val filter =
            object : SignalPatchFilter {
                override val include = Regex("user")
                override val exclude = Regex("password")
            }

        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                dataOnSignalPatchFilter(filter)
                            }
                        }
                    }
                }.toString()

        assertContains(html, """data-on-signal-patch-filter="{include: /user/, exclude: /password/}"""")
    }
}
