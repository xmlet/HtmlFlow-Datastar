import org.xmlet.htmlflow.datastar.expressions.RegexSignalPatchFilter
import kotlin.test.Test
import kotlin.test.assertEquals

class SignalPatchFilterTest {
    @Test
    fun `renders include filter only`() {
        val filter =
            RegexSignalPatchFilter(
                include = Regex("^counter$"),
            )

        val result = filter.render()

        assertEquals(
            "{include: /^counter$/}",
            result,
        )
    }

    @Test
    fun `renders include and exclude filters`() {
        val filter =
            RegexSignalPatchFilter(
                include = Regex("user"),
                exclude = Regex("password"),
            )

        val result = filter.render()

        assertEquals(
            "{include: /user/, exclude: /password/}",
            result,
        )
    }
}
