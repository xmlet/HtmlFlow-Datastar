package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class CounterTest {
    @Test
    fun `demo counter HTML increment button via patch element`() {
        testDemoCounterSignalsIncrementButton("counter/html")
    }

    @Test
    fun `demo counter HTML increment button via patch signals`() {
        testDemoCounterSignalsIncrementButton("counter-signals/html")
    }

    @Test
    fun `demo counter HtmlFlow increment button via patch signals`() {
        testDemoCounterSignalsIncrementButton("counter-signals/htmlflow")
    }

    /**
     * Tests that clicking the increment button updates the counter in the DOM from 0 to 1.
     *
     * This test uses Playwright to:
     * 1. Load the page in a real browser context
     * 2. Wait for JavaScript to initialize (Datastar framework)
     * 3. Verify the initial counter value is 0
     * 4. Click the + button
     * 5. Wait for the fetch request and SSE update
     * 6. Verify the counter DOM element now shows 1
     */
    fun testDemoCounterSignalsIncrementButton(path: String) {
        val server =
            embeddedServer(Netty, port = 0) {
                routing {
                    demoHtmlFlowDatastarRouting()
                }
            }.start()

        val port =
            runBlocking {
                server.engine
                    .resolvedConnectors()
                    .first()
                    .port
            }

        Playwright.create().use { playwright ->
            val browser: Browser =
                playwright.chromium().launch(
                    BrowserType.LaunchOptions().setHeadless(true),
                )
            val context = browser.newContext()
            val page = context.newPage()

            try {
                // Navigate to the counter page
                val url = "http://localhost:$port/$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the counter span to be visible
                page.waitForSelector("span#counter")

                // Get initial counter value
                val initialValue = page.textContent("span#counter")
                assertEquals("0", initialValue?.trim(), "Initial counter value should be 0")

                // Click the increment (+) button
                // The button is the second button (first is decrement -)
                page.click("button:has-text('+')")

                // Wait for the counter to update to 1
                // This waits for the POST request to complete and SSE to update the DOM
                page.waitForFunction("document.querySelector('span#counter').textContent.trim() === '1'")

                // Verify the counter was incremented
                page.textContent("span#counter").also { newValue ->
                    assertEquals("1", newValue?.trim(), "Counter should be incremented to 1 after clicking +")
                }

                // Click the decrement (-) button
                page.click("button:has-text('−')")

                // Wait for the counter to update to 0
                // This waits for the POST request to complete and SSE to update the DOM
                page.waitForFunction("document.querySelector('span#counter').textContent.trim() === '0'")

                // Verify the counter was decremented
                page.textContent("span#counter").also { newValue ->
                    assertEquals("0", newValue?.trim(), "Counter should be decremented to 0 after clicking −")
                }
            } finally {
                page.close()
                context.close()
                browser.close()
                server.stop(1000, 2000)
            }
        }
    }
}
