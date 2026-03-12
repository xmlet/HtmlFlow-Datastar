package pt.isel.http4k

import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.bind
import org.http4k.routing.poly
import org.http4k.routing.static

val demosHttp4kRouting =
    poly(
        "/" bind static(Classpath("public")),
        *("/counter-signals" bind demoCounterSignals()).toTypedArray(),
        *("/counter" bind demoCounter).toTypedArray(),
    )
