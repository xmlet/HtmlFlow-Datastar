package htmlflow.datastar

import htmlflow.div
import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlflow.datastar.attributes.dataComputed
import org.xmlet.htmlflow.datastar.attributes.dataEffect
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataShow
import org.xmlet.htmlflow.datastar.builders.ExpressionBuilder
import org.xmlet.htmlflow.datastar.events.Click
import org.xmlet.htmlflow.datastar.expressions.ast.BinaryOperator
import org.xmlet.htmlflow.datastar.expressions.ast.JsExpr
import org.xmlet.htmlflow.datastar.expressions.ast.JsRenderer
import org.xmlet.htmlflow.datastar.expressions.ast.UnaryOperator
import org.xmlet.htmlflow.datastar.expressions.signal
import org.xmlet.htmlflow.datastar.serialization.RawJS
import org.xmlet.htmlflow.datastar.serialization.asRawJS
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RawJsExpressionTests {
    @Test
    fun `string operands preserve only the grouping supplied by the caller`() {
        val a = signal<Boolean>("a")
        val b = signal<Boolean>("b")
        val c = signal<Boolean>("c")

        assertEffect($$"$a || $b && $c") { "$a || $b" and c }
        assertEffect($$"$a && $b || $c") { "$a && $b" or c }
        assertEffect($$"($a || $b) && $c") { "($a || $b)" and c }
        assertEffect($$"($a && $b) || $c") { "($a && $b)" or c }
    }

    @Test
    fun `two raw operands compose through the AST`() {
        val left = "a() || b()".asRawJS()
        val right = "c() ? d() : e()".asRawJS()
        assertEquals(
            "a() || b() && c() ? d() : e()",
            JsRenderer.render(JsExpr.Binary(JsExpr.Raw(left), BinaryOperator.AND, JsExpr.Raw(right))),
        )
        assertEquals(
            "a() || b() || c() ? d() : e()",
            JsRenderer.render(JsExpr.Binary(JsExpr.Raw(left), BinaryOperator.OR, JsExpr.Raw(right))),
        )
        assertEquals(
            "a() || b() == c() ? d() : e()",
            JsRenderer.render(JsExpr.Binary(JsExpr.Raw(left), BinaryOperator.EQUAL, JsExpr.Raw(right))),
        )
    }

    @Test
    fun `raw negation uses caller grouping while DSL composite negation uses AST grouping`() {
        assertEquals(
            "!a() || b()",
            JsRenderer.render(JsExpr.Unary(UnaryOperator.NOT, JsExpr.Raw("a() || b()".asRawJS()))),
        )
        assertEquals(
            "!(a() || b())",
            JsRenderer.render(JsExpr.Unary(UnaryOperator.NOT, JsExpr.Raw("(a() || b())".asRawJS()))),
        )

        val enabled = signal<Boolean>("enabled")
        assertEffect($$"!(a() || b() && $enabled)") {
            !("a() || b()" and enabled)
        }
        assertEffect($$"!((a() || b()) && $enabled)") {
            !("(a() || b())" and enabled)
        }
    }

    @Test
    fun `existing generic value API accepts raw values without new overloads`() {
        val count = signal<Any>("count")
        assertEffect($$"$count = 1, 2") { count.setValue("1, 2".asRawJS()) }
        assertEffect($$"$count = (1, 2)") { count.setValue("(1, 2)".asRawJS()) }

        val name = signal<Any>("name")
        assertEffect($$"$name = evt.target.value") { name.setValue("evt.target.value".asRawJS()) }
        val stringName = signal<String>("name")
        assertEffect($$"$name = 'evt.target.value'") { stringName.setValue("evt.target.value") }
    }

    @Test
    fun `equality distinguishes raw expressions from string values`() {
        val name = signal<Any>("name")
        assertEffect($$"$name == evt.target.value") { name eq "evt.target.value".asRawJS() }
        val stringName = signal<String>("name")
        assertEffect($$"$name == 'evt.target.value'") { stringName eq "evt.target.value" }
    }

    @Test
    fun `boxed raw values also use opaque AST operands`() {
        val state = signal<Any>("state")
        val raw: Any = "a(), b()".asRawJS()
        assertEffect($$"$state = a(), b()") { state.setValue(raw) }
        assertEffect($$"$state == a(), b()") { state eq raw }

        val rawSignal = signal<RawJS>("raw")
        assertEffect($$"$raw = a(), b()") { rawSignal.setValue("a(), b()".asRawJS()) }
        assertEffect($$"$raw == a(), b()") { rawSignal eq "a(), b()".asRawJS() }
        assertEffect($$"$raw = (a(), b())") { rawSignal.setValue("(a(), b())".asRawJS()) }
        assertEffect($$"$raw == (a(), b())") { rawSignal eq "(a(), b())".asRawJS() }
    }

    @Test
    fun `raw standalone statements preserve source and sequence order`() {
        val enabled = signal<Boolean>("enabled")
        assertEffect($$"before(); log('hello'); count++; ready() && $enabled; after()") {
            +"before()"
            +"log('hello'); count++"
            "ready()" and enabled
            +"after()"
        }
    }

    @Test
    fun `raw source is not escaped or serialized`() {
        val raw = "log('O\\'Reilly', \"quoted\", `template`)".asRawJS()
        assertEffect(raw.js) { +raw.js }
        assertEquals("!${raw.js}", JsRenderer.render(JsExpr.Unary(UnaryOperator.NOT, JsExpr.Raw(raw))))
    }

    @Test
    fun `computed raw expressions return reusable signal references`() {
        val a = signal<Boolean>("a")
        val b = signal<Boolean>("b")
        val c = signal<Boolean>("c")
        val output =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                val computed = dataComputed("computed") { "$a || $b" and c }
                                dataShow { computed and c }
                            }
                        }
                    }
                }.toString()

        assertTrue(output.contains($$"data-computed=\"{computed: () => $a || $b && $c}\""), output)
        assertTrue(output.contains($$"data-show=\"$computed && $c\""), output)
    }

    @Test
    fun `raw action condition consumes its provisional action statement`() {
        val output =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                dataOn(Click) { "ready() || retry()" and get("/users") }
                            }
                        }
                    }
                }.toString()

        assertTrue(output.contains("data-on:click=\"ready() || retry() && @get('/users')\""), output)
    }

    @Test
    fun `existing string APIs remain available without explicit raw conversion`() {
        val enabled = signal<Boolean>("enabled")
        assertEffect($$"ready() && $enabled") { "ready()" and enabled }
        assertEffect($$"ready() || $enabled") { "ready()" or enabled }
        assertEffect("log('hello')") { +"log('hello')" }
    }

    @Test
    fun `simple calls and member paths do not receive redundant parentheses`() {
        val enabled = signal<Boolean>("enabled")
        assertEffect($$"confirm('Are you sure?') && $enabled") { "confirm('Are you sure?')" and enabled }
        assertEffect($$"person.isReady(check(a, b), 'text ) (') && $enabled") {
            "person.isReady(check(a, b), 'text ) (')" and enabled
        }
        assertEffect($$"evt.target.value || $enabled") { "evt.target.value" or enabled }
        assertEffect($$"(a || b) && $enabled") { "(a || b)" and enabled }
    }

    @Test
    fun `compound and unfamiliar raw source is not inspected or rewritten`() {
        val compound =
            listOf(
                "ready() || retry()",
                "ready() || (retry())",
                "ready(')') || retry()",
                "ready() ? yes() : no()",
                "ready(), retry()",
                "ready(/* comment */) || retry()",
                "ready(/[()]/) || retry()",
                "ready(`template`) || retry()",
            )

        for (source in compound) {
            val enabled = signal<Boolean>("enabled")
            assertEffect("$source && $enabled") { source and enabled }
        }
    }

    @Test
    fun `simple calls with escaped quotes retain their source`() {
        val enabled = signal<Boolean>("enabled")
        val source = "confirm('O\\'Reilly (test)', \"quoted )\")"
        assertEffect("$source && $enabled") { source and enabled }
    }

    private fun assertEffect(
        expected: String,
        block: ExpressionBuilder.() -> Unit,
    ) {
        val output =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div { dataEffect(block) }
                        }
                    }
                }.toString()

        assertTrue(output.contains("data-effect=\"$expected\""), output)
    }
}
