package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.use

class InfiniteScrollTest {
    @Test
    fun `should load more agents on scroll on HTML`() {
        `should load more agents on scroll`("/infinite-scroll/html")
    }

    @Test
    fun `should load more agents on scroll on HtmlFlow`() {
        `should load more agents on scroll`("/infinite-scroll/htmlflow")
    }

    /**
     * Tests that more agents are loaded when the user scrolls to the bottom of the page.
     */
    fun `should load more agents on scroll`(path: String) {
        val server =
            embeddedServer(Netty, port = 0) {
                demoHtmlFlowDatastarRouting()
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
                // Navigate to the active-search page
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the table body to be present
                page.waitForSelector("tbody#agents")

                // Capture initial number of agents (should be 10)
                val initialCount = page.locator("#agents tr").count()
                assertEquals(10, initialCount)

                // Ensure the intersection trigger is in view
                page.waitForSelector("[data-on-intersect]")
                page.evaluate(
                    """
                document
                  .querySelector('[data-on-intersect]')
                  .scrollIntoView()
                """,
                )

                // Wait until more agents are appended
                page.waitForFunction(" document.querySelectorAll('#agents tr').length > 10")

                // Verify new agents were added
                val finalCount = page.locator("#agents tr").count()
                assert(finalCount > initialCount) {
                    "Expected more agents after scrolling, but got $finalCount"
                }
                val lastAgentName = page.textContent("#agents tr:last-child td:first-child").trim()

                assert(lastAgentName!!.startsWith("Agent Smith")) { "Unexpected agent name: $lastAgentName" }
            } finally {
                page.close()
                context.close()
                browser.close()
                server.stop(1000, 2000)
            }
        }
    }
}
