# ADR-006: Signal Casing Consistency

**Status:** Accepted

**Date:** 2026-05-07

## Context

Signal casing was being inconsistent as, before, this example:
```kotlin
div {
    val birthDate = dataSignals("birthDate")
    input {
        dataBind(birthDate)
    }
    p { dataText { +birthDate} }
}
```
Would generate this HTML:
```HTML
<div data-signals:birthdate="">
	<input data-bind:birthdate></input>
	<p data-text="$birthDate"></p>
</div> 
```
As the DSL would treat signal names with case modifier by default, but would not
change the name in case there was no separator (e.g.: `birth-date` would
automatically be converted to `birthDate`). This would cause an inconsistency as
the name that the DSL would use in expressions was camelCase, as passed in the
parameter. In contrast, the HTML generated, that creates the signal, would be
converted to lowerCase, as custom HTML attributes are case-insensitive.

## Decision

Change the way signal name is created, from, in certain attributes functions
such as `dataSignal`, passing it in the key of the attribute, e.g.:
```HTML
<input data-bind:foo />
```
To passing it in the value, where the case is maintained, e.g.:
```HTML
<input data-bind="foo" />
```

## Trigger

Detected inconsistency in signal naming and referencing across expressions, this
causes errors to be generated.

## Consequences

**Positive:**

- Signal name casing consistent across all calls, from naming itself, to
  referencing in expressions and HTTP payloads;
- The use of the DSL remains the same, with no changes required to existing code
  written using it.

**Negative:**

- This produces HTML code less intuitive, specifically in `dataComputed`
  attribute, where the value is an expression, going from:
  ```HTML
  <div data-computed:foo="$bar + $baz"></div>
  ```
  Using the key naming notation, to:
  ```HTML
  <div data-computed="{foo: () => $bar + $baz}"></div>
  ```
  Using the value notation, as this attribute receives a function. The Kotlin
  code stays the same, but the generated HTML becomes more complex;
- Datastar notes that "This can be useful depending on the templating language
  you are using" (https://data-star.dev/reference/attributes#data-bind), but as
  we did not find use cases relevant to this, we will not investigate this
  further for the time being.

**Neutral:**

- No neutral changes.

## Alternatives Considered

### Signal Name Conversion Inside The DSL

The option of always converting the name received to camelCase, in order to
maintain consistency across calls, was considered, but deemed that it was too
complex and inefficient, as Datastar engine already does this. Also, to ensure
this, the transformation would generate HTML where the signal name would be
different from the one used in JavaScript expressions and HTTP payload. This,
although functional, does not achieve the consistency in the code generated that
is wanted.
