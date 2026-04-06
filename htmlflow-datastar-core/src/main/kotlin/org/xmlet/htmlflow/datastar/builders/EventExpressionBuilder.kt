package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.events.Event
import org.xmlet.htmlflow.datastar.modifiers.attributes.DataOnModifiers

/**
 * An event-aware builder that extends [ExpressionModifierBuilder] with access to a specific [Event].
 *
 * [EventExpressionBuilder] is a specialized builder used within `data-on` event handler lambdas.
 * It provides:
 * - All expression-building capabilities from [ExpressionBuilder] (signals, actions, operators)
 * - All modifier-accumulation capabilities through [DataOnModifiers] (event-specific modifiers)
 * - Direct access to the triggering event via the [evt] property
 *
 * This allows you to build event-driven expressions that can reference and react to the specific
 * event that triggered the handler, while also configuring how the event should be processed.
 *
 * **Example usage:**
 * ```kotlin
 * dataOn(click) {
 *     +signal1.setValue(evt.detail)  // Access event data
 *     and action1                      // Chain actions
 *     modifiers {
 *         debounce = "300ms"          // Configure event modifiers
 *     }
 * }
 * ```
 *
 * @param EVT the type of [Event] available in this builder context
 * @property evt the event instance that triggered this builder's lambda, providing access to event data and properties
 */
class EventExpressionBuilder<EVT : Event>(
    val evt: EVT,
) : ExpressionModifierBuilder<DataOnModifiers>(::DataOnModifiers)
