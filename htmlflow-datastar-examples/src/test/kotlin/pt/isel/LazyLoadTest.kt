package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import pt.isel.ktor.demoHtmlFlowDatastarRouting
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LazyLoadTest {
    @Test
    fun `lazy load shows loading then graph on HTML`() {
        `lazy load shows loading then graph`("/lazy-load/html")
    }

    @Test
    fun `lazy load shows loading then graph on HtmlFlow`() {
        `lazy load shows loading then graph`("/lazy-load/htmlflow")
    }

    /**
     * Tests that the lazy load initially shows "Loading..."
     * and then replaces it with the graph image after the SSE response.
     */
    fun `lazy load shows loading then graph`(path: String) {
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
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for body to ensure page loaded
                page.waitForSelector("body")

                // Wait for the graph div to be visible
                page.waitForSelector("#graph")

                // Verify initial state shows "Loading..."
                val graphDiv = page.querySelector("#graph")
                assertNotNull(graphDiv, "Graph div should exist")

                val initialText = graphDiv.textContent()
                assertTrue(
                    initialText.contains("Loading"),
                    "Initial content should contain 'Loading...', but was: '$initialText'",
                )

                // Verify no image is present initially
                val initialImages = graphDiv.querySelectorAll("img")
                assertEquals(0, initialImages.size, "No image should be present initially")

                // Wait for the graph to load (SSE response + processing time)
                // Adding extra time to account for the 2-second delay in the server
                page.waitForSelector(
                    "#graph img",
                    com.microsoft.playwright.Page
                        .WaitForSelectorOptions()
                        .setTimeout(8000.0)
                        .setState(com.microsoft.playwright.options.WaitForSelectorState.ATTACHED),
                )

                // Verify the image is now present
                val loadedImages = page.querySelectorAll("#graph img")
                assertEquals(1, loadedImages.size, "Exactly one image should be present after loading")

                // Verify image attributes
                val img = loadedImages.first()
                val imgSrc = img.getAttribute("src")
                assertNotNull(imgSrc, "Image should have a src attribute")
                assertTrue(
                    imgSrc.contains("tokyo") || imgSrc.contains("graph"),
                    "Image src should reference the graph/tokyo image, but was: '$imgSrc'",
                )

                val imgAlt = img.getAttribute("alt")
                assertEquals("Tokyo", imgAlt, "Image should have alt text 'Tokyo'")

                // Verify "Loading..." text is gone
                val finalText = graphDiv.textContent()
                assertTrue(
                    !finalText.contains("Loading"),
                    "Loading text should be replaced after graph loads, but text was: '$finalText'",
                )
            } finally {
                page.close()
                context.close()
                browser.close()
                server.stop(1000, 2000)
            }
        }
    }
}
