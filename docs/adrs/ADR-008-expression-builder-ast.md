# ADR-008: AST for ExpressionBuilder

**Status:** Proposed

**Date:** 2026-11-16

## Context

`ExpressionBuilder` combines signals, actions, and operators into JavaScript.
Composing expressions as strings loses their grouping: `a and (b or c)` must
produce `$a && ($b || $c)`, not `$a && $b || $c`.

The solution must preserve the existing DSL and automatic registration introduced
in [ADR-002](./ADR-002-expression-registration.md).

## Decision

Represent DSL operations with an internal abstract syntax tree (AST), rather than
intermediate strings.

- `DataStarExpressionOp` retains the expression tree, extending the design in
  [ADR-004](./ADR-004-datastar-expression-restructure.md).
- `JsRenderer` generates JavaScript on demand, adding parentheses according to
  operator precedence and associativity and serializing literal values.
- A separate, builder-local registry tracks ordered statements. Composition
  replaces provisional operand statements with the combined expression, while
  explicitly registered statements remain.
- Raw JavaScript is emitted unchanged; callers remain responsible for its grouping.

## Trigger

The need to preserve expression grouping without mixing rendering rules with
statement registration.

## Consequences

**Positive:**

- Preserves grouping and centralizes rendering rules.
- Avoids repeatedly rendering intermediate expressions.
- Allows expression reuse across builders without sharing statement registration.

**Negative:**

- Adds internal types, allocations, and operator rules to maintain.
- Mutable literal values are read at render time, so changes can affect output.

**Neutral:**

- Public DSL syntax remains unchanged; generated parentheses may differ.
- The AST models supported DSL operations, not the full JavaScript language.

## Alternatives Considered

### Continue composing strings

Simpler internally, but loses structure and spreads grouping rules across operations.

### Parenthesize every operation eagerly

Preserves grouping, but keeps intermediate string construction and couples
composition to rendering.

### Introduce a complete JavaScript AST and parser

Supports arbitrary JavaScript, but adds unnecessary scope and maintenance cost
for the supported DSL.
