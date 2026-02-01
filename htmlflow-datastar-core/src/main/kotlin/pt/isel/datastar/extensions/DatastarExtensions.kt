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
                null -> ""
                else -> "$value"
            }
        "$name: $res"
    }

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param signals pairs of signal names and their corresponding values
 * @return a list of Signal instances with the given names
 */
fun <E : Element<*, *>, P : Element<*, *>, Any> Element<E, P>.dataSignals(vararg signals: Pair<String, Any?>): List<Signal> {
    signals.toList().toJson().also {
        this.visitor.visitAttribute("data-signals", it)
    }
    return signals.map { (name) ->
        Signal(name)
    }
}

/**
 * Creates a data signal without an initial value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataSignal(name: String): Signal = dataSignal(name, null)

/**
 * Creates a signal (if one doesn’t already exist)
 * and sets up two-way data binding between it and an element’s value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-bind attribute will be added
 * @param name the name of the signal to bind
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataBind(name: String): Signal {
    require(this is Input || this is Select || this is Textarea) { "Element must be input, select or text area" }
    this.visitor.visitAttribute("data-bind:$name", "")
    return Signal(name)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-bind attribute will be added
 * @param signal the Signal to bind to (its value takes precedence)
 * @return the same Signal instance
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataBind(signal: Signal): Signal {
    require(this is Input || this is Select || this is Textarea) { "Element must be input, select or text area" }
    this.visitor.visitAttribute("data-bind:${signal.name}", "")
    return signal
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-init attribute will be added
 * @param js a JavaScript expression that computes the value of the signal
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataInit(js: String) {
    this.visitor.visitAttribute("data-init", js)
}

/**
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
 * data-attr="{title: $foo, disabled: $bar}
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-attr attribute will be added
 * @param attrs a JavaScript expression that computes the values of multiple attributes on an element using a set of key-value pairs
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataAttr(vararg attrs: Pair<String, Signal>) {
    attrs
        .joinToString(prefix = "{", postfix = "}", separator = ", ") { (name, value) ->
            "$name: $value"
        }.also {
            this.visitor.visitAttribute("data-attr", it)
        }
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-effect attribute will be added
 * @param js an expression on page load and whenever any signals in the expression change
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataEffect(js: String) {
    this.visitor.visitAttribute("data-effect", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-indicator attribute will be added
 * @param name the name of the indicator signal
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataIndicator(name: String): Signal {
    this.visitor.visitAttribute("data-indicator:$name", "")
    return Signal(name)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param js a JavaScript expression that computes the value of the signal
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataShow(js: String) {
    this.visitor.visitAttribute("data-show", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param signal the Signal whose value will be used for the attribute
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataShow(signal: Signal) {
    this.visitor.visitAttribute("data-show", signal.toString())
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param js a JavaScript expression that computes the value of the signal
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataText(js: String) {
    this.visitor.visitAttribute("data-text", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param signal the Signal whose value will be used for the attribute
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataText(signal: Signal) {
    this.visitor.visitAttribute("data-text", signal.toString())
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param js a JavaScript expression that is run when the element intersects the viewport
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnIntersect(js: String) {
    this.visitor.visitAttribute("data-on-intersect", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param js a JavaScript expression that is run when any signal is patched
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnSignalPatch(js: String) {
    this.visitor.visitAttribute("data-on-signal-patch", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param jsObj a JavaScript object with include and/or exclude properties that are regular expressions, that filter which signals to watch.
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnSignalPatchFilter(jsObj: String) {
    this.visitor.visitAttribute("data-on-signal-patch-filter", jsObj)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param jsObj a JavaScript object with include and/or exclude properties that are regular expressions, that filter which signals to watch.
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataJsonSignals(jsObj: String = "") {
    this.visitor.visitAttribute("data-json-signals", jsObj)
}
