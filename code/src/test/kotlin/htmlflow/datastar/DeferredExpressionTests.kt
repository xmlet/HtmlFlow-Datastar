package htmlflow.datastar

import org.xmlet.htmlflow.datastar.builders.ExpressionBuilder
import org.xmlet.htmlflow.datastar.expressions.DataStarExpression
import org.xmlet.htmlflow.datastar.expressions.signal
import kotlin.test.Test
import kotlin.test.assertEquals

class DeferredExpressionTests {
    @Test
    fun `actions and signals retain syntax and string representation through the base type`() {
        val enabled = signal<Boolean>("enabled")
        val builder = ExpressionBuilder()
        val action = with(builder) { get("/users") }
        val expressions: List<DataStarExpression> = listOf(enabled, action)

        assertEquals(listOf($$"$enabled", "@get('/users')"), expressions.map { it.syntax })
        assertEquals(expressions.map { it.syntax }, expressions.map { it.toString() })
        assertEquals("enabled", enabled.name)
        assertEquals("@get('/users')", builder.getExpression())
    }

    @Test
    fun `composition renders current literal values when output is requested`() {
        val value = mutableMapOf("value" to 1)
        val state = signal<Map<String, Int>>("state")
        val enabled = signal<Boolean>("enabled")
        val builder = ExpressionBuilder()
        val expression = with(builder) { (state eq value) and enabled }

        value["value"] = 2
        assertEquals($$"$state == {value: 2} && $enabled", builder.getExpression())
        assertEquals($$"$state == {value: 2} && $enabled", expression.syntax)

        value["value"] = 3
        assertEquals($$"$state == {value: 3} && $enabled", expression.toString())
        assertEquals(expression.syntax, expression.toString())
    }

    @Test
    fun `long composition renders current literal values at output`() {
        val value = mutableMapOf("value" to 1)
        val state = signal<Map<String, Int>>("state")
        val enabled = signal<Boolean>("enabled")
        val builder = ExpressionBuilder()
        val operands = 200

        with(builder) {
            var expression: DataStarExpression = state eq value
            repeat(operands) { expression = expression and enabled }
        }

        value["value"] = 2
        assertEquals($$"$state == {value: 2}" + " && $enabled".repeat(operands), builder.getExpression())
    }

    @Test
    fun `reading syntax does not consume a statement or freeze its literal`() {
        val values = mutableListOf(1)
        val state = signal<List<Int>>("state")
        val enabled = signal<Boolean>("enabled")
        val builder = ExpressionBuilder()
        val expression = with(builder) { state eq values }

        assertEquals($$"$state == [1]", expression.syntax)
        values += 2
        with(builder) { expression and enabled }

        assertEquals($$"$state == [1, 2] && $enabled", builder.getExpression())
    }

    @Test
    fun `expressions reused across builders retain grouping and independent statement slots`() {
        val a = signal<Boolean>("a")
        val b = signal<Boolean>("b")
        val c = signal<Boolean>("c")
        val original = ExpressionBuilder()
        val condition = with(original) { a or b }
        val combined = ExpressionBuilder()

        with(combined) { condition and c }

        assertEquals($$"($a || $b) && $c", combined.getExpression())
        assertEquals($$"$a || $b", original.getExpression())
        assertEquals($$"$a || $b", condition.syntax)
    }

    @Test
    fun `rendering between builder operations preserves ordered statements`() {
        val builder = ExpressionBuilder()
        val a = signal<Boolean>("a")
        val b = signal<Boolean>("b")
        val condition =
            with(builder) {
                +"before()"
                a or b
            }

        assertEquals($$"before(); $a || $b", builder.getExpression())
        assertEquals($$"$a || $b", condition.toString())

        with(builder) {
            a and b
            +"after()"
        }

        assertEquals($$"before(); $a || $b; $a && $b; after()", builder.getExpression())
        assertEquals($$"before(); $a || $b; $a && $b; after()", builder.getExpression())
    }
}
