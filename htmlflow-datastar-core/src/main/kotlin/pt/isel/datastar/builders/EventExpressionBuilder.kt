package pt.isel.datastar.builders

import pt.isel.datastar.events.Event
import pt.isel.datastar.modifiers.attributes.DataOnModifiers

/**
 * A [EventExpressionBuilder] is the same as the [ExpressionModifierBuilder] but an [Event] available in the
 * lambdas passed to data attributes.
 *
 * @property evt The event that is being called in dataOn.
 */
class EventExpressionBuilder<EVT : Event>(
    val evt: EVT,
) : ExpressionModifierBuilder<DataOnModifiers>(::DataOnModifiers)
