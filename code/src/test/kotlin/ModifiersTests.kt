import htmlflow.div
import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlflow.datastar.attributes.dataIgnore
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataOnIntersect
import org.xmlet.htmlflow.datastar.attributes.dataOnInterval
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.Click
import org.xmlet.htmlflow.datastar.modifiers.CaseStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class ModifiersTests {
    @Test
    fun `CaseModifiers should add correct case modifier`() {
        val expected = expectedHtmlCaseModifier.trimIndent().lines().iterator()
        expectedCaseModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `DelayModifier applied to dataOn click event`() {
        val expected = expectedHtmlDelayModifier.trimIndent().lines().iterator()
        expectedDelayModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `DurationModifiers should add correct duration modifier`() {
        val expected = expectedHtmlDurationModifier.trimIndent().lines().iterator()
        expectedDurationModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `ViewTransitionModifiers should add correct viewtransition modifier`() {
        val expected = expectedHtmlViewTransitionModifier.trimIndent().lines().iterator()
        expectedViewTransitionModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `ThrottleModifiers should add correct throttle modifier`() {
        val expected = expectedHtmlThrottleModifier.trimIndent().lines().iterator()
        expectedThrottleModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `TimingEdgeModifiers should add leading and notrailing to debounce`() {
        val expected = expectedHtmlTimingEdgeModifier.trimIndent().lines().iterator()
        expectedTimingEdgeModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `IntersectModifiers should add correct intersect modifiers`() {
        val expected = expectedHtmlIntersectModifier.trimIndent().lines().iterator()
        expectedIntersectModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `IgnoreModifiers should add data-ignore attribute`() {
        val expected = expectedHtmlIgnoreModifier.trimIndent().lines().iterator()
        expectedIgnoreModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }
}

private val expectedCaseModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("/js/datastar.js")
                        }
                        link {
                            attrRel(EnumRelType.STYLESHEET)
                            attrHref("/css/styles.css")
                        }
                    }
                    body {
                        div {
                            attrId("demoCaseModifier")
                            val userName =
                                dataSignal("Name", "John Doe") {
                                    modifiers { case(CaseStyle.CAMEL) }
                                }
                            p { dataText { +userName } }
                        }
                    }
                }
            }
        }

private val expectedHtmlCaseModifier =
    $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="/js/datastar.js">
                </script>
                <link rel="stylesheet" href="/css/styles.css">
            </head>
            <body>
                <div id="demoCaseModifier" data-signals:Name__case.camel="'John Doe'">
                    <p data-text="$name">
                    </p>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedDelayModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("/js/datastar.js")
                        }
                        link {
                            attrRel(EnumRelType.STYLESHEET)
                            attrHref("/css/styles.css")
                        }
                    }
                    body {
                        div {
                            attrId("demoDelayModifier")
                            button {
                                dataOn(Click) {
                                    get(::fetchData)
                                    modifiers { delay(500.milliseconds) }
                                }
                                text("Fetch")
                            }
                        }
                    }
                }
            }
        }

@Path("/delay-modifier/fetch")
private fun fetchData() {}

private val expectedHtmlDelayModifier =
    $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="/js/datastar.js">
                </script>
                <link rel="stylesheet" href="/css/styles.css">
            </head>
            <body>
                <div id="demoDelayModifier">
                    <button data-on:click__delay.500ms="@get('/delay-modifier/fetch')">
                        Fetch
                    </button>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedDurationModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("/js/datastar.js")
                        }
                        link {
                            attrRel(EnumRelType.STYLESHEET)
                            attrHref("/css/styles.css")
                        }
                    }
                    body {
                        div {
                            attrId("demoDurationModifier")
                            button {
                                dataOnInterval {
                                    get(::fetchData)
                                    modifiers { duration(500.milliseconds, leading = true) }
                                }
                                text("Fetch")
                            }
                        }
                    }
                }
            }
        }

private val expectedHtmlDurationModifier =
    $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="/js/datastar.js">
                </script>
                <link rel="stylesheet" href="/css/styles.css">
            </head>
            <body>
                <div id="demoDurationModifier">
                    <button data-on-interval__duration.500ms.leading="@get('/delay-modifier/fetch')">
                        Fetch
                    </button>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedViewTransitionModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("/js/datastar.js")
                        }
                        link {
                            attrRel(EnumRelType.STYLESHEET)
                            attrHref("/css/styles.css")
                        }
                    }
                    body {
                        div {
                            attrId("demoViewTransitionModifier")
                            button {
                                dataOn(Click) {
                                    get(::navigatePage)
                                    modifiers { viewTransition() }
                                }
                                text("Navigate")
                            }
                        }
                    }
                }
            }
        }

@Path("/view-transition/navigate")
private fun navigatePage() {}

private val expectedHtmlViewTransitionModifier =
    $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="/js/datastar.js">
                </script>
                <link rel="stylesheet" href="/css/styles.css">
            </head>
            <body>
                <div id="demoViewTransitionModifier">
                    <button data-on:click__viewtransition="@get('/view-transition/navigate')">
                        Navigate
                    </button>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedThrottleModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("/js/datastar.js")
                        }
                        link {
                            attrRel(EnumRelType.STYLESHEET)
                            attrHref("/css/styles.css")
                        }
                    }
                    body {
                        div {
                            attrId("demoThrottleModifier")
                            button {
                                dataOn(Click) {
                                    get(::fetchData)
                                    modifiers {
                                        throttle(500.milliseconds)
                                    }
                                }
                                text("Fetch")
                            }
                        }
                    }
                }
            }
        }

private val expectedHtmlThrottleModifier =
    $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="/js/datastar.js">
                </script>
                <link rel="stylesheet" href="/css/styles.css">
            </head>
            <body>
                <div id="demoThrottleModifier">
                    <button data-on:click__throttle.500ms="@get('/delay-modifier/fetch')">
                        Fetch
                    </button>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedTimingEdgeModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("/js/datastar.js")
                        }
                        link {
                            attrRel(EnumRelType.STYLESHEET)
                            attrHref("/css/styles.css")
                        }
                    }
                    body {
                        div {
                            attrId("demoTimingEdgeModifier")
                            button {
                                dataOn(Click) {
                                    get(::fetchLeading)
                                    modifiers { debounce(500.milliseconds) { leading() } }
                                }
                                text("Leading")
                            }
                            button {
                                dataOn(Click) {
                                    get(::fetchNoTrailing)
                                    modifiers { debounce(500.milliseconds) { noTrailing() } }
                                }
                                text("No Trailing")
                            }
                        }
                    }
                }
            }
        }

@Path("/timing-edge/leading")
private fun fetchLeading() {}

@Path("/timing-edge/no-trailing")
private fun fetchNoTrailing() {}

private val expectedHtmlTimingEdgeModifier =
    $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="/js/datastar.js">
                </script>
                <link rel="stylesheet" href="/css/styles.css">
            </head>
            <body>
                <div id="demoTimingEdgeModifier">
                    <button data-on:click__debounce.500ms.leading="@get('/timing-edge/leading')">
                        Leading
                    </button>
                    <button data-on:click__debounce.500ms.notrailing="@get('/timing-edge/no-trailing')">
                        No Trailing
                    </button>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedIntersectModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("/js/datastar.js")
                        }
                        link {
                            attrRel(EnumRelType.STYLESHEET)
                            attrHref("/css/styles.css")
                        }
                    }
                    body {
                        div {
                            attrId("demoIntersectModifier")
                            div {
                                dataOnIntersect {
                                    get(::fetchHalf)
                                    modifiers { half() }
                                }
                            }
                            div {
                                dataOnIntersect {
                                    get(::fetchFull)
                                    modifiers { full() }
                                }
                            }
                        }
                    }
                }
            }
        }

@Path("/intersect/half")
private fun fetchHalf() {}

@Path("/intersect/full")
private fun fetchFull() {}

private val expectedHtmlIntersectModifier =
    $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="/js/datastar.js">
                </script>
                <link rel="stylesheet" href="/css/styles.css">
            </head>
            <body>
                <div id="demoIntersectModifier">
                    <div data-on-intersect__half="@get('/intersect/half')">
                    </div>
                    <div data-on-intersect__full="@get('/intersect/full')">
                    </div>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedIgnoreModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("/js/datastar.js")
                        }
                        link {
                            attrRel(EnumRelType.STYLESHEET)
                            attrHref("/css/styles.css")
                        }
                    }
                    body {
                        div {
                            attrId("demoIgnoreModifier")
                            div {
                                dataIgnore {
                                    modifiers { self() }
                                }
                                p { text("This content is ignored by Datastar") }
                            }
                            div {
                                p { text("This content is not ignored by Datastar") }
                            }
                        }
                    }
                }
            }
        }

private val expectedHtmlIgnoreModifier =
    $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="/js/datastar.js">
                </script>
                <link rel="stylesheet" href="/css/styles.css">
            </head>
            <body>
                <div id="demoIgnoreModifier">
                    <div data-ignore__self="">
                        <p>
                            This content is ignored by Datastar
                        </p>
                    </div>
                    <div>
                        <p>
                            This content is not ignored by Datastar
                        </p>
                    </div>
                </div>
            </body>
        </html>
    """.trimMargin()
