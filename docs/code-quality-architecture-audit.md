# Code Quality and Architecture Audit

Date: 2026-05-29

This report summarizes the repository audit and separates what is already implemented from actions that are not implemented yet. The original audit made no code changes; follow-up resolutions are tracked inline.

## Current Architecture

The project is a Kotlin JVM DSL library organized under `code/src/main/kotlin/org/xmlet/htmlflow/datastar`.

Implemented areas:

- `attributes`: HtmlFlow extension functions that emit Datastar `data-*` attributes.
- `builders`: expression and modifier builders used by the fluent DSL.
- `expressions`: `Signal`, `DataStarAction`, `DataStarExpression`, action options, and filters.
- `events`: typed DOM event objects exposed through `dataOn`.
- `modifiers`: Datastar modifier accumulators and attribute-specific modifier scopes.
- `tests`: unit and example-style tests that mostly assert generated HTML or expression strings.

Relevant ADRs:

- `docs/adrs/ADR-001-gradle-plugin-expressions.md`: rejected Kotlin compiler/Gradle plugin transpilation.
- `docs/adrs/ADR-002-expression-registration.md`: accepted mutable expression accumulator model.
- `docs/adrs/ADR-003-signal-value.md`: accepted removal of `Signal.value`.
- `docs/adrs/ADR-004-datastar-expression-restructure.md`: accepted sealed `DataStarExpression` hierarchy.

## Actions Already Implemented

These are design or feature decisions that are already present in the codebase.

| Area | Status | Evidence |
| --- | --- | --- |
| Kotlin DSL instead of compiler plugin | Implemented | ADR-001 rejects transpilation; expressions are built explicitly with builders. |
| Expression builder accumulator | Implemented | `ExpressionBuilder` stores `MutableList<DataStarExpression>` and normalizes composed expressions. |
| Action functions inside builder scope | Implemented | `get`, `post`, `put`, `patch`, `delete`, `peek`, `setAll`, and `toggleAll` live in `ExpressionBuilder` / `ExpressionScope`. |
| Unary plus for raw strings and signals | Implemented | `Signal<*>.unaryPlus()` and `String.unaryPlus()` append expressions. |
| Sealed expression hierarchy | Implemented | `DataStarExpression` is sealed; `Signal`, `DataStarAction`, and `DataStarExpressionOp` extend it. |
| Removed `Signal.value` | Implemented | `Signal` only stores `name` and generated `syntax`. |
| Strongly typed event access in `dataOn` | Partially implemented | `EventExpressionBuilder` exposes `evt`; event classes expose typed properties. |
| Attribute-specific modifier scopes | Implemented | `DataOnModifiers`, `DataInitModifiers`, `DataBindModifiers`, etc. expose only selected modifier functions. |
| Complex data-class signal serialization | Partially implemented | Data classes are serialized reflectively in `serializeValue`. |
| Centralized JavaScript literal and object serialization | Implemented | `JavaScriptSerialization` now centralizes string literals, object keys, object literals, and regex literals. |
| Explicit modifier duration formatting | Implemented | Duration-based modifiers now serialize finite, non-negative, whole-millisecond values explicitly with an `ms` suffix. |
| Test suite | Implemented | `./gradlew test` passes. |

## Actions Not Implemented Yet

These are proposed follow-up actions. They are not implemented in the repository at the time of this report.

| Priority | Action | Reason |
| --- | --- | --- |
| High | Add precedence-aware expression composition | Current `and`, `or`, `eq`, and assignment composition concatenate strings without grouping. Some generated expressions are ambiguous or invalid JavaScript. |
| High | Fix incorrect event inheritance for `Click` | `Click` currently extends `FocusEvent`, which prevents click handlers from exposing mouse properties such as `clientX`, `button`, and `offsetX`. |
| Medium | Improve signal-name validation | Object literal keys are now escaped centrally, but signal names can still contain characters that make direct `$signal-name` expression references awkward or invalid. |
| Medium | Revisit heterogeneous `dataSignals` ergonomics | `dataSignals(vararg Pair<String, T>)` returns `List<Signal<T>>`, which becomes awkward for mixed signal types. |
| Medium | Reduce repeated attribute builder boilerplate | Many attribute functions repeat `ExpressionBuilder().apply(block).getExpression()` or modifier-builder setup. |
| Medium | Add semantic tests for expression validity | Current tests mostly verify strings and sometimes lock in questionable output. |
| Low | Refresh README examples | README contains stale or misleading examples for `Signal` typing and `data-indicator` output. |
| Low | Update KDoc around `Signal` | Current docs still mention reactive/value semantics after `Signal.value` was removed. |

## Ranked Findings

### Critical

No critical issue was found that blocks compilation or test execution. The repository builds and tests pass.

### High Severity

1. Unsafe manual JavaScript serialization

   Files:

   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/attributes/AttributesUtils.kt`
   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/expressions/ActionOptions.kt`
   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/expressions/DataStarAction.kt`
   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/expressions/SignalPatchFilter.kt`

   Problem:

   Strings, paths, payloads, headers, selectors, regex patterns, and object keys are assembled manually. Escaping is incomplete and inconsistent.

   Recommended action:

   Completed. A small internal serializer now handles JavaScript string literals, object literal keys, regex literals, and object literal assembly. Remaining risk: raw expression snippets are still intentionally passed through unchanged; full expression safety belongs to the separate expression composition/modeling action.

2. Expression composition has no precedence model

   Files:

   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/builders/ExpressionBuilder.kt`
   - `code/src/test/kotlin/htmlflow/datastar/ExpressionBuilderTests.kt`

   Problem:

   Expressions such as assignments chained with logical operators are emitted without parentheses. Tests currently accept output that is likely invalid or semantically fragile JavaScript.

   Recommended action:

   Represent generated expressions with precedence metadata or grouped expression nodes instead of raw string concatenation.

3. `Click` is modeled as a focus event

   File:

   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/events/Events.kt`

   Problem:

   `Click` extends `FocusEvent`, while `DblClick` extends `MouseEvent`. This makes `evt.clientX`, `evt.button`, and similar mouse properties unavailable for normal click handlers.

   Recommended action:

   Change `Click` to extend `MouseEvent` and add a test that `dataOn(Click)` exposes mouse event properties.

### Medium Severity

1. Accumulator normalization is fragile

   File:

   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/builders/ExpressionBuilder.kt`

   Problem:

   `removeIfPresent` relies on object identity for `DataStarExpressionOp` and `DataStarAction`, while preserving signals. The output depends on evaluation order and exact object reuse.

   Recommended action:

   Prefer a simple expression AST or a dedicated expression segment model so registration and composition do not fight each other.

2. Signal type safety is narrower than the README implies

   Files:

   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/expressions/Signal.kt`
   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/builders/ExpressionBuilder.kt`
   - `README.md`

   Problem:

   `Signal<T>` stores only a name and generated syntax. This is consistent with ADR-003, but documentation overstates runtime/value safety.

   Recommended action:

   Clarify docs and keep examples typed as `Signal<Int>`, `Signal<User>`, etc.

3. Duration modifiers depend on Kotlin string formatting

   Files:

   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/modifiers/kinds/DelayModifiers.kt`
   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/modifiers/kinds/DebounceModifiers.kt`
   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/modifiers/kinds/ThrottleModifiers.kt`
   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/modifiers/kinds/DurationModifiers.kt`

   Problem:

   Duration values are emitted using `$time`, which depends on Kotlin `Duration.toString()`.

   Recommended action:

   Completed. Duration-based modifiers now use a shared formatter that emits explicit whole-millisecond values with an `ms` suffix and rejects finite-invalid inputs instead of delegating to Kotlin `Duration.toString()`.

4. Attribute helpers duplicate builder flow

   Files:

   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/attributes/DatastarExtensions.kt`
   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/attributes/DatastarExtensionsModifiers.kt`

   Problem:

   Many functions duplicate builder creation, expression extraction, modifier extraction, and `visitAttribute`.

   Recommended action:

   Add small internal helper functions after behavior is covered by tests.

### Low Severity

1. README examples need refresh

   File:

   - `README.md`

   Problem:

   Some examples use broad `Signal` typing and show output that does not match current implementation.

   Recommended action:

   Update examples after deciding whether implementation or docs should be the source of truth.

2. KDoc has stale wording

   File:

   - `code/src/main/kotlin/org/xmlet/htmlflow/datastar/expressions/Signal.kt`

   Problem:

   KDoc still discusses value/reactive behavior even though ADR-003 removed `Signal.value`.

   Recommended action:

   Rewrite the KDoc to say `Signal` is a typed reference to generated Datastar signal syntax.

## Test Coverage Gaps

Current test suite status:

- `./gradlew test` passes.

Missing or weak coverage:

- Escaping for strings, paths, selectors, headers, payloads, and regex patterns.
- Invalid or reserved JavaScript object keys in signal names.
- Operator precedence and grouping for assignments, equality, `and`, and `or`.
- Event type access for `Click` and other common DOM events.
- Heterogeneous `dataSignals` usage.
- README examples compiled or snapshot-tested against current output.
- Additional edge cases for unsupported duration inputs.

## Suggested Implementation Order

1. Fix `Click` event inheritance and add a focused test.
2. Add failing tests for expression grouping before changing expression internals.
3. Add precedence/grouping to expression composition.
4. Refresh README and KDoc.
5. Refactor repeated attribute builder boilerplate.

## Decisions Needed

Please decide which actions should be implemented and which should be skipped:

| Action | Implement? |
| --- | --- |
| Fix `Click` to be a `MouseEvent` | TBD |
| Centralize JavaScript serialization | Done |
| Add expression precedence/grouping | TBD |
| Make duration formatting explicit | Done |
| Validate signal names | TBD |
| Improve heterogeneous `dataSignals` ergonomics | TBD |
| Refactor repeated attribute boilerplate | TBD |
| Refresh README examples | TBD |
| Update stale KDoc | TBD |
| Add stronger semantic tests | TBD |
