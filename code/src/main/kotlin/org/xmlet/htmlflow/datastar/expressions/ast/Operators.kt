package org.xmlet.htmlflow.datastar.expressions.ast

/** JavaScript prefix operators supported by the DataStar expression DSL. */
internal enum class UnaryOperator(
    val symbol: String,
) {
    NOT("!"),
}

/**
 * The direction in which equal-precedence binary expressions associate.
 *
 * The renderer uses this to parenthesize only the operand whose omission would change the AST
 * grouping.
 */
internal enum class Associativity {
    LEFT,
    RIGHT,
    NONE,
}

/**
 * Binary operators supported by the DSL, with the JavaScript metadata needed by [JsRenderer].
 *
 * Keeping precedence and associativity here makes operators, rather than expression instances,
 * responsible for their rendering semantics.
 */
internal enum class BinaryOperator(
    val symbol: String,
    val precedence: Int,
    val associativity: Associativity,
) {
    OR("||", 4, Associativity.LEFT),
    AND("&&", 5, Associativity.LEFT),
    EQUAL("==", 8, Associativity.NONE),
}
