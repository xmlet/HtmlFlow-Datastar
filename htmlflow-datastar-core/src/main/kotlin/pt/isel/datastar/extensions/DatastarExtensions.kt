/*
 * MIT License
 *
 * Copyright (c) 2025, xmlet HtmlFlow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pt.isel.datastar.extensions

import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.Input
import org.xmlet.htmlapifaster.Select
import org.xmlet.htmlapifaster.Textarea
import pt.isel.datastar.Signal
import pt.isel.datastar.builders.ExpressionBuilder
import kotlin.collections.joinToString

fun List<Pair<String, Any?>>.toJson(): String =
    this.joinToString(prefix = "{", postfix = "}", separator = ", ") { (name, value) ->
        val res =
            when (value) {
                is String -> "'$value'"
                is Function0<*> -> value()
                null -> ""
                else -> "$value"
            }
        "$name: $res"
    }

/**
 * Creates a signal and sets up two-way data binding between it and an element’s value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-bind attribute will be added
 * @param name the name of the signal to bind
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataBind(name: String): Signal<Any> {
    require(this is Input || this is Select || this is Textarea) { "Element must be input, select or text area" }
    this.visitor.visitAttribute("data-bind:$name", "")
    return Signal(name, null)
}

/**
 *
 * Creates a signal and sets up two-way data binding between it and an element’s value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-bind attribute will be added
 * @param signal the Signal to bind to (its value takes precedence)
 * @return the same Signal instance
 */
fun <E : Element<*, *>, P : Element<*, *>, R> Element<E, P>.dataBind(signal: Signal<R>): Signal<R> {
    require(this is Input || this is Select || this is Textarea) { "Element must be input, select or text area" }
    this.visitor.visitAttribute("data-bind:${signal.name}", "")
    return signal
}

/**
 *
 * Binds any HTML attribute to an expression, keeping it synchronized.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-attr attribute will be added
 * @param name the name of the HTML attribute to set the value to the expression
 * @param block Block with tha lambda containing the expression
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataAttr(
    name: String,
    block: ExpressionBuilder.() -> Unit,
) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-attr:$name", expression)
}

/**
 *
 * Binds multiple HTML attributes simultaneously through key-value pairs,
 * keeping them synchronized with their expressions.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-attr attribute will be added
 * @param attrs a JavaScript expression that computes the values of multiple attributes on an element using a set of key-value pairs
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataAttr(vararg attrs: Pair<String, Signal<*>>) {
    attrs
        .joinToString(prefix = "{", postfix = "}", separator = ", ") { (name, value) ->
            "$name: $value"
        }.also {
            this.visitor.visitAttribute("data-attr", it)
        }
}

/**
 *
 * Executes an expression on page load and whenever any signals in the expression change.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-effect attribute will be added
 * @param block Block with tha lambda containing the expression
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataEffect(block: ExpressionBuilder.() -> Unit) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-effect", expression)
}

/**
 *
 * Creates a signal and sets its value to true while a fetch request is in flight, otherwise false.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-indicator attribute will be added
 * @param name the name of the indicator signal
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataIndicator(name: String): Signal<Boolean> {
    this.visitor.visitAttribute("data-indicator:$name", "")
    return Signal(name, true)
}

/**
 *
 * Toggles element visibility based on an expression.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-show attribute will be added
 * @param block Block with tha lambda containing the expression
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataShow(block: ExpressionBuilder.() -> Unit) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-show", expression)
}

/**
 *
 * Binds the text content of an element to an expression.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-text attribute will be added
 * @param block Block with tha lambda containing the expression to be used for the attribute
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataText(block: ExpressionBuilder.() -> Unit) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-text", expression)
}

/**
 *
 * Runs an expression when the element intersects with the viewport.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-intersect attribute will be added
 * @param block Block with tha lambda containing the expression to be ran
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnIntersect(block: ExpressionBuilder.() -> Unit) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-on-intersect", expression)
}

/**
 *
 * Runs an expression whenever any Signal is patched.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-signal-patch attribute will be added
 * @param block Block with tha lambda containing the expression
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnSignalPatch(block: ExpressionBuilder.() -> Unit) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-on-signal-patch", expression)
}

/**
 *
 * Filters which signals to watch when using the data-on-signal-patch attribute.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-signal-patch-filter attribute will be added
 * @param jsObj a JavaScript object with include and/or exclude properties that are regular expressions, that filter which signals to watch.
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnSignalPatchFilter(jsObj: String) {
    this.visitor.visitAttribute("data-on-signal-patch-filter", jsObj)
}
