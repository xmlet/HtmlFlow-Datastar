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

class ActiveSearchTest {
    @Test
    fun `search name, filters table rows on HTML`() {
        `search name, filters table rows`("/active-search/html")
    }

    @Test
    fun `search name, filters table rows on HtmlFlow`() {
        `search name, filters table rows`("/active-search/htmlflow")
    }

    /**
     * Tests that searching for a name filters the table to show only matching rows.
     */
    fun `search name, filters table rows`(path: String) {
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

                // Wait for the page to be ready - input and table should be visible
                page.waitForSelector("input")
                page.waitForSelector("table")

                val rowCount = page.querySelectorAll("tbody tr").size
                assertEquals(10, rowCount, "Initial table should have 10 rows")

                // Type "Ann" into the search input
                val searchInput = page.querySelector("input")
                searchInput.fill("Ann")

                // Wait for the table to update
                page.waitForTimeout(500.0) // Wait for debounce and SSE processing

                val filteredRowCount = page.querySelectorAll("tbody tr").size
                assertEquals(1, filteredRowCount, "Table should have 1 row after searching")

                // Verify the content of the filtered row
                val row = page.querySelectorAll("tbody tr").first()

                val firstName = row.querySelectorAll("td")[0].textContent().trim()
                val lastName = row.querySelectorAll("td")[1].textContent().trim()
                assertEquals("Annamarie", firstName, "Filtered row should have first name 'Annamarie'")
                assertEquals("Rippin", lastName, "Filtered row should have last name 'Rippin'")

                // Clear the search input
                searchInput.fill("")
                page.waitForTimeout(500.0) // Wait for debounce and SSE processing
                val resetRowCount = page.querySelectorAll("tbody tr").size
                assertEquals(10, resetRowCount, "Table should have 10 rows after clearing search")
            } finally {
                page.close()
                context.close()
                browser.close()
                server.stop(1000, 2000)
            }
        }
    }
}
