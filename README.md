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
    val count = dataSignal("count", 0)
    div {
        dataInit("@get('/counter-signals/events')")
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




