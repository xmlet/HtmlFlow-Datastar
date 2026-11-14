package htmlflow.datastar

import htmlflow.div
import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Click
import org.xmlet.htmlflow.datastar.expressions.ast.BinaryOperator
import org.xmlet.htmlflow.datastar.expressions.ast.ExpressionRegistry
import org.xmlet.htmlflow.datastar.expressions.ast.JsExpr
import org.xmlet.htmlflow.datastar.expressions.ast.JsRenderer
import org.xmlet.htmlflow.datastar.expressions.ast.UnaryOperator
import org.xmlet.htmlflow.datastar.expressions.signal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsRendererTests {
    @Test
    fun `renderer preserves logical precedence and explicit grouping`() {
        val a = signalExpr("a")
        val b = signalExpr("b")
        val c = signalExpr("c")

        assertEquals($$"$a && $b || $c", JsRenderer.render(binary(binary(a, BinaryOperator.AND, b), BinaryOperator.OR, c)))
        assertEquals($$"$a && ($b || $c)", JsRenderer.render(binary(a, BinaryOperator.AND, binary(b, BinaryOperator.OR, c))))
        assertEquals($$"$a || $b && $c", JsRenderer.render(binary(a, BinaryOperator.OR, binary(b, BinaryOperator.AND, c))))
        assertEquals($$"($a || $b) && $c", JsRenderer.render(binary(binary(a, BinaryOperator.OR, b), BinaryOperator.AND, c)))
    }

    @Test
    fun `renderer negates any expression and respects associativity`() {
        val a = signalExpr("a")
        val b = signalExpr("b")

        assertEquals($$"!($a && $b)", JsRenderer.render(JsExpr.Unary(UnaryOperator.NOT, binary(a, BinaryOperator.AND, b))))
    }

    @Test
    fun `renderer appends to an existing buffer and preserves nested grouping`() {
        val a = signalExpr("a")
        val b = signalExpr("b")
        val c = signalExpr("c")
        val output = StringBuilder("prefix: ")

        JsRenderer.renderInto(output, binary(a, BinaryOperator.AND, binary(b, BinaryOperator.AND, c)))
        output.append("; ")
        JsRenderer.renderInto(output, JsExpr.Literal("O'Reilly"))

        assertEquals($$"prefix: $a && ($b && $c); 'O\\'Reilly'", output.toString())
    }

    @Test
    fun `renderer preserves assignment associativity and equality grouping`() {
        val a = signalExpr("a")
        val b = signalExpr("b")
        val c = signalExpr("c")

        assertEquals($$"$a = $b = $c", JsRenderer.render(JsExpr.Assignment(a, JsExpr.Assignment(b, c))))
        assertEquals($$"($a = $b) = $c", JsRenderer.render(JsExpr.Assignment(JsExpr.Assignment(a, b), c)))
        assertEquals($$"($a == $b) == $c", JsRenderer.render(binary(binary(a, BinaryOperator.EQUAL, b), BinaryOperator.EQUAL, c)))
        assertEquals($$"$a == ($b == $c)", JsRenderer.render(binary(a, BinaryOperator.EQUAL, binary(b, BinaryOperator.EQUAL, c))))
    }

    @Test
    fun `empty and consumed statement slots add no separators`() {
        val registry = ExpressionRegistry()
        assertEquals("", registry.render())

        val first = registry.unary(UnaryOperator.NOT, registry.source($$"$a"))
        registry.binary(first, BinaryOperator.AND, registry.source($$"$b"))
        registry.explicit(registry.source("after()"))

        assertEquals($$"!$a && $b; after()", registry.render())
    }

    @Test
    fun `program preserves sequence order and renderer escapes literals`() {
        val registry = ExpressionRegistry()

        registry.explicit(registry.source($$"$a"))
        registry.explicit(registry.source($$"$b"))
        registry.explicit(registry.source($$"$c"))

        assertEquals($$"$a; $b; $c", registry.render())
        assertEquals(
            "\'quote: \" slash: \\\\ newline: \\n tab: \\t\'",
            JsRenderer.render(JsExpr.Literal("quote: \" slash: \\ newline: \n tab: \t")),
        )
    }

    @Test
    fun `data on renders AST expression at attribute boundary`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                val a = signal<Boolean>("a")
                                val b = signal<Boolean>("b")
                                val c = signal<Boolean>("c")
                                dataOn(Click) {
                                    a and (b or c)
                                }
                            }
                        }
                    }
                }.toString()

        assertTrue(html.contains($$"data-on:click=\"$a && ($b || $c)\""))
    }

    private fun signalExpr(name: String) = JsExpr.Source($$"$$$name")

    private fun binary(
        left: JsExpr,
        operator: BinaryOperator,
        right: JsExpr,
    ) = JsExpr.Binary(left, operator, right)
}
