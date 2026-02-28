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
 *
 * Initializes one or more signals with their initial values.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signals attribute will be added
 * @param signals pairs of signal names and their corresponding values
 * @return a list of Signal instances with the given names and values
 */
fun <E : Element<*, *>, P : Element<*, *>, Any> Element<E, P>.dataSignals(vararg signals: Pair<String, Any?>): List<Signal<Any?>> {
    signals.toList().toJson().also {
        this.visitor.visitAttribute("data-signals", it)
    }
    return signals.map { (name, value) ->
        Signal(name, value)
    }
}

/**
 * Creates a data signal without an initial value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @return a Signal instance with the given name and a null value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataSignal(name: String): Signal<*> = dataSignal(name, null)

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
 * Runs an expression when the attribute is initialized.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-init attribute will be added
 * @param js a JavaScript expression that computes the value of the signal
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataInit(js: String) {
    this.visitor.visitAttribute("data-init", js)
}

/**
 *
 * Binds any HTML attribute to an expression, keeping it synchronized.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-attr attribute will be added
 * @param name the name of the HTML attribute to set the value to the expression
 * @param js the JavaScript expression that the attribute value will be set to
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataAttr(
    name: String,
    js: String,
) {
    this.visitor.visitAttribute("data-attr:$name", js)
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
 * @param js an expression on page load and whenever any signals in the expression change
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataEffect(js: String) {
    this.visitor.visitAttribute("data-effect", js)
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
 * @param js a JavaScript expression that computes the value of the signal
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataShow(js: String) {
    this.visitor.visitAttribute("data-show", js)
}

/**
 *
 * Toggles element visibility based on a Signal's value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-show attribute will be added
 * @param signal the Signal whose value will be used for the attribute
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataShow(signal: Signal<*>) {
    this.visitor.visitAttribute("data-show", "$signal")
}

/**
 *
 * Binds the text content of an element to an expression.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-text attribute will be added
 * @param js a JavaScript expression that computes the value of the signal
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataText(js: String) {
    this.visitor.visitAttribute("data-text", js)
}

/**
 *
 * Binds the text content of an element to a Signal's value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-text attribute will be added
 * @param signal the Signal whose value will be used for the attribute
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataText(signal: Signal<*>) {
    this.visitor.visitAttribute("data-text", "$signal")
}

/**
 *
 * Runs an expression when the element intersects with the viewport.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-intersect attribute will be added
 * @param js a JavaScript expression that is run when the element intersects the viewport
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnIntersect(js: String) {
    this.visitor.visitAttribute("data-on-intersect", js)
}

/**
 *
 * Runs an expression whenever any Signal is patched.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-signal-patch attribute will be added
 * @param js a JavaScript expression that is run when any signal is patched
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnSignalPatch(js: String) {
    this.visitor.visitAttribute("data-on-signal-patch", js)
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

/**
 *
 * Sets the text content of an element to a reactive JSON stringified version of signals.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-json-signals attribute will be added
 * @param jsObj a JavaScript object with include and/or exclude properties that are regular expressions, that filter which signals to watch.
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataJsonSignals(jsObj: String = "") {
    this.visitor.visitAttribute("data-json-signals", jsObj)
}

/**
 *
 * Adds or removes a class from an element based on an expression.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-class attribute will be added
 * @param className the name of the class from the element
 * @param predicate a JavaScript expression that if true adds the class to element otherwise removes it.
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataClass(
    className: String,
    predicate: String,
) {
    this.visitor.visitAttribute("data-class:$className", predicate)
}
