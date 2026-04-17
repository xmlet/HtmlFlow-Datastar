import jakarta.ws.rs.Path
import org.junit.jupiter.api.Test
import org.xmlet.htmlflow.datastar.Signal
import org.xmlet.htmlflow.datastar.builders.ExpressionBuilder
import kotlin.test.assertEquals

class ExpressionBuilderTests {
    @Test
    fun `Expression builder testing referencing a signal two times to see if its not removed from the expression`() {
        val fetching = Signal<Boolean>("fetching")
        val builder = ExpressionBuilder()
        with(builder) {
            +fetching
            fetching.setValue(false) and get(::someHandler)
        }

        assertEquals("$fetching; $fetching = false && @get('/some/url')", builder.getExpression())
    }

    @Path("/some/url")
    private fun someHandler() {}

    @Test
    fun `Expression builder testing referencing a signal two times to see if its removed from the expression`() {
        val fetching = Signal<Boolean>("fetching")
        val other = Signal<Boolean>("other")
        val builder = ExpressionBuilder()
        with(builder) {
            +fetching
            get(::someHandler) and post(::someHandler)
            other.setValue(fetching)
        }

        assertEquals("$fetching; @get('/some/url') && @post('/some/url'); $other = $fetching", builder.getExpression())
    }

    @Test
    fun `Expression builder testing referencing a DataStarCompoundExprElem two times to see if its removed from the expression`() {
        val fetching = Signal<Boolean>("fetching")
        val other = Signal<Boolean>("other")
        val builder = ExpressionBuilder()
        with(builder) {
            !fetching
            get(::someHandler)
            other.setValue(!fetching)
        }

        assertEquals("!$fetching; @get('/some/url'); $other = !$fetching", builder.getExpression())
    }

    @Test
    fun `Expression builder test with multiple js operators`() {
        val fetching = Signal<Boolean>("fetching")
        val other = Signal<Boolean>("other")
        val builder = ExpressionBuilder()
        with(builder) {
            fetching.setValue(!fetching) and get(::someHandler) or other.setValue(fetching)
            !fetching
        }
        assertEquals("$fetching = !$fetching && @get('/some/url') || $other = $fetching; !$fetching", builder.getExpression())
    }

    @Test
    fun `Expression builder test where both parameters of js operator are js operators`() {
        val count1 = Signal<Int>("count1")
        val count2 = Signal<Int>("count2")
        val count3 = Signal<Int>("count3")
        val builder = ExpressionBuilder()
        with(builder) {
            (count1 eq 1) or (count2 eq 2) and (count3 eq 3)
        }
        assertEquals("$count1 == 1 || $count2 == 2 && $count3 == 3", builder.getExpression())
    }
}
