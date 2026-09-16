
package org.xmlet.htmlflow.datastar.expressions.ast

/**
 * Minimal structural representation for JavaScript expressions composed by the DataStar DSL.
 *
 * Existing DataStar concepts, such as signals and actions, are kept as [Source] nodes. This
 * package only models syntax that needs structural grouping during rendering.
 */
internal sealed interface JsExpr {
    /** An already-defined DataStar expression, such as a signal, action, event property, or raw JavaScript. */
    data class Source(
        val value: String,
    ) : JsExpr

    /** A Kotlin value that must be serialized as a JavaScript literal by [JsRenderer]. */
    data class Literal(
        val value: Any?,
    ) : JsExpr

    /** A prefix unary operation applied to [operand]. */
    data class Unary(
        val operator: UnaryOperator,
        val operand: JsExpr,
    ) : JsExpr

    /** A binary operation whose operand grouping is preserved by [JsRenderer]. */
    data class Binary(
        val left: JsExpr,
        val operator: BinaryOperator,
        val right: JsExpr,
    ) : JsExpr

    /** A JavaScript assignment from [value] to [target]. */
    data class Assignment(
        val target: JsExpr,
        val value: JsExpr,
    ) : JsExpr
}
