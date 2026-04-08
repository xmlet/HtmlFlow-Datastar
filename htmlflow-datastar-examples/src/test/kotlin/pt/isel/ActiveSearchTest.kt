package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import pt.isel.infrastructure.SharedTestServers
import pt.isel.infrastructure.SharedTestServersExtension
import kotlin.test.assertEquals

@ExtendWith(SharedTestServersExtension::class)
class ActiveSearchTest {
    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `search name, filters table rows`(serverType: String) {
        `search name, filters table rows`("/active-search/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `search name, filters table rows on HtmlFlow`(serverType: String) {
        `search name, filters table rows`("/active-search/htmlflow", serverType)
    }

    /**
     * Tests that searching for a name filters the table to show only matching rows.
     */
    private fun `search name, filters table rows`(
        path: String,
        serverType: String,
    ) {
        val port = SharedTestServers.getPort(serverType)

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
                page.waitForFunction("document.querySelectorAll('tbody tr').length === 1")

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
                page.waitForFunction("document.querySelectorAll('tbody tr').length === 10")
                val resetRowCount = page.querySelectorAll("tbody tr").size
                assertEquals(10, resetRowCount, "Table should have 10 rows after clearing search")
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}
