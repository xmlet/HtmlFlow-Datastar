package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClickToLoadTest {
    @Test
    fun `click load more button fetches and appends 5 rows to table on HTML`() {
        `click load more button fetches and appends 5 rows to table`("/click-to-load/html")
    }

    @Test
    fun `click load more button fetches and appends 5 rows to table on HtmlFlow`() {
        `click load more button fetches and appends 5 rows to table`("/click-to-load/htmlflow")
    }

    /**
     * Tests that clicking the "Load More" button fetches 5 table rows via SSE
     * and appends them to the table.
     */
    fun `click load more button fetches and appends 5 rows to table`(path: String) {
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
                // Navigate to the click-to-load page
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page to be ready - table should be visible
                page.waitForSelector("table")

                // Verify initial state - table body should be empty
                val initialRowCount = page.querySelectorAll("tbody#agents tr").size
                assertEquals(0, initialRowCount, "Table should initially be empty")

                // Click the "Load More" button
                page.click("button:has-text(\"Load More\")")

                // Wait for the rows to be added
                // We expect 5 rows to be added
                page.waitForFunction("document.querySelectorAll('tbody#agents tr').length === 5")

                // Verify 5 rows were added
                val rowCount = page.querySelectorAll("tbody#agents tr").size
                assertEquals(5, rowCount, "Table should have 5 rows after clicking Load More")

                // Verify the structure of the rows
                val rows = page.querySelectorAll("tbody#agents tr")
                for (i in 0 until 5) {
                    val row = rows[i]
                    val cells = row.querySelectorAll("td")

                    assertEquals(3, cells.size, "Each row should have 3 cells")

                    // Verify the name column
                    val name = cells[0].textContent()
                    assertEquals("Agent Smith $i", name, "Row $i should have correct name")

                    // Verify the email column
                    val email = cells[1].textContent()
                    assertEquals("void$i@null.org", email, "Row $i should have correct email")

                    // Verify the ID column exists and is non-empty
                    val id = cells[2].textContent()
                    assertTrue(id?.isNotEmpty() == true, "Row $i should have a non-empty ID")
                    assertTrue(id?.length == 16, "ID should be 16 characters (8 hex bytes)")
                }

                // Verify the HTML structure matches the expected format
                val tableHTML = page.querySelector("tbody#agents")?.innerHTML() ?: ""
                assertTrue(
                    tableHTML.contains("<tr><td>Agent Smith 0</td><td>void0@null.org</td>"),
                    "Table HTML should contain Agent Smith 0 row",
                )
                assertTrue(
                    tableHTML.contains("<tr><td>Agent Smith 4</td><td>void4@null.org</td>"),
                    "Table HTML should contain Agent Smith 4 row",
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
