# ADR-005: Typifying Action Options

**Status:** Accepted

**Date:** 2026-05-07

## Context

Actions can receive options. These options were value parameters passed in the form of an optional string passed to the action functions. This made them not be strongly typed and against the principals of the DSL. So, the objective was to provide type-safety to the options passed to actions.

## Decision

Make action options strongly typed. To achieve this, the architecture chosen was for action functions, instead of receiving an optional string value parameter, to receive a function parameter. This function is a lambda with the class `ActionOptions` as the receiver. This class has the possible options for actions represented as variable properties. These can then be reassigned in the lambda in an action call. Inside the action an instance of `ActionOptions` will be created and applied the lambda block.

## Trigger

The need for strongly typed action options.

## Consequences

**Positive:**

- Strongly typed action options.

**Negative:**

- Complexity added to action functions;
- New class added;
- Properties of the new class are mutable.

**Neutral:**

- instead of passing the options in a value parameter, they are now passed inside of a lambda.

## Alternatives Considered

### Builder with functions for each action

This alternative follows the pattern of design of the other builder, `ExpressionBuilder` and `ModifierBuilder`, where, for each option there would be function available inside the lambda that add that option to an accumulator. This would remove mutability of publicly available properties and keep the builder patterns consistent. The reason to not adapt this design is that, providing functions that assign values to these options would not make it very explicit that the options can only have a single value, so multiple calls to the same option would, in the background, replace the old value, without this being explicit in use. This would also increase implementation complexity to maintain consistency on multiple calls. Additionally, in Datastar, action options are represented as n object with properties, so mirroring this in the DSL makes it more explicit and true to Datastar.
