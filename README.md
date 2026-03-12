# HtmlFlow-Datastar

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=xmlet_HtmlFlow-Datastar&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=xmlet_HtmlFlow-Datastar)

Type-Safe Hypermedia-First DSL for Reactive Backend-Driven Web Applications

## Examples

This project includes a demo web application featuring examples from 
[Data-Star](https://data-star.dev/examples), running on **Ktor** and using the **HtmlFlow Kotlin DSL** to generate HTML.

HtmlFlow DSL provides a type-safe way to define Datastar attributes in Kotlin. Next is a sample of the Counter example
with a strongly typed signal and another one from Active Search using events and modifiers:

<table class="table">
<tr>
<td>


```kotlin
div {
  val count: Signal = dataSignal("count", 0)
  div {
    dataInit("@get('/counter/events')")
    span {
      attrId("counter")
      dataText(count)
    }
  }
}
```

</td>
<td>


```kotlin
div {
  attrId("demo")
  input {
    attrType(EnumTypeInputType.TEXT)
    attrPlaceholder("Search...")
    dataBind("search")
    dataOn("input", "@get('/active-search/search')") {
      debounce(200.milliseconds)
    }
  }
}
```

</td>
</tr>
<tr>
    <td>
        Note that the <code>count</code> variable is of type <code>Signal</code>
        and simply binds to the data-text in a type-safe way,
        regardless of the name passed to the <code>Signal</code> constructor.
    </td>
    <td>
        Modifiers such as <code>debounce</code> are added inside a lambda using 
        builders, following an idiomatic Kotlin style.
    </td>
</tr>
</table>

HtmlFlow DSL provides type-safe backend handlers for DataStar actions.
In the next sample, taken from the “Click to Load” DataStar example, 
note how the action `get` is attached to the handler given by the function reference `::clickToLoadMore`.

Also, HtmlFlow DSL supports strongly typed DataStar expressions,
allowing their composition with the infix operator `and`, 
such as in the expression `!fetching and get(::clickToLoadMore)`.
Note how the JavaScript expression for the `onclick` event handler (right side)
is expressed in Kotlin through HtmlFlow in a type-safe way:


<table>
<tr>
<td>
    
```kotlin
button {
  val fetching = dataIndicator("_fetching")
  dataAttr("disabled", fetching)
  dataOn("click", !fetching and get(::clickToLoadMore))
  text("Load More")
}
```

</td>
<td>

```html
<button
  data-indicator:_fetching
  data-attr:aria-disabled="`${$_fetching}`"
  data-on:click="!$_fetching && @get('/examples/click_to_load/more')"
>
    Load More
</button>
```

</td>
</tr>
</table>

Run with: `./gradlew run` and open `http://localhost:8080` in your browser.

Check all examples from the index page and corresponding HtmlFlow view definitions:
* Active Search - [ActiveSearch.kt](htmlflow-datastar-examples/src/main/kotlin/pt/isel/views/htmlflow/ActiveSearch.kt)
* Bulk Update - [BulkUpdate.kt](htmlflow-datastar-examples/src/main/kotlin/pt/isel/views/htmlflow/BulkUpdate.kt)
* Click To Edit - [ClickToEdit.kt](htmlflow-datastar-examples/src/main/kotlin/pt/isel/views/htmlflow/ClickToEdit.kt)
* Click To Load - [BulkUpdate.kt](htmlflow-datastar-examples/src/main/kotlin/pt/isel/views/htmlflow/BulkUpdate.kt)
* Counter Via Signals - [CounterViaSignals.kt](htmlflow-datastar-examples/src/main/kotlin/pt/isel/views/htmlflow/CounterViaSignals.kt)
* DBmon - [DBmon.kt](htmlflow-datastar-examples/src/main/kotlin/pt/isel/views/htmlflow/DBmon.kt)
* Delete Row - [DeleteRow.kt](htmlflow-datastar-examples/src/main/kotlin/pt/isel/views/htmlflow/DeleteRow.kt)
* File Upload - [FileUpload.kt](htmlflow-datastar-examples/src/main/kotlin/pt/isel/views/htmlflow/FileUpload.kt)
* Infinite Scroll - [InfiniteScroll.kt](htmlflow-datastar-examples/src/main/kotlin/pt/isel/views/htmlflow/InfiniteScroll.kt)
* Inline Validation - [InlineValidation.kt](htmlflow-datastar-examples/src/main/kotlin/pt/isel/views/htmlflow/InlineValidation.kt)




