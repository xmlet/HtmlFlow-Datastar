package pt.isel.http4k

import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.bind
import org.http4k.routing.poly
import org.http4k.routing.static

val demosHttp4kRouting =
    poly(
        "/" bind static(Classpath("public")),
        *("/counter-signals" bind demoCounterSignals()).toTypedArray(),
        *("/counter" bind demoCounter()).toTypedArray(),
        *("/click-to-edit" bind demoClickToEdit()).toTypedArray(),
        *("/click-to-edit-signals" bind demoClickToEditViaSignals()).toTypedArray(),
        *("/edit-row" bind demoEditRow()).toTypedArray(),
        *("/inline-validation" bind demoInlineValidation()).toTypedArray(),
        *("/file-upload" bind demoFileUpload()).toTypedArray(),
        *("/infinite-scroll" bind demoInfiniteScroll()).toTypedArray(),
        *("/lazy-load" bind demoLazyLoad()).toTypedArray(),
        *("/lazy-tabs" bind demoLazyTabs()).toTypedArray(),
        *("/progress-bar" bind demoProgressBar()).toTypedArray(),
    )
