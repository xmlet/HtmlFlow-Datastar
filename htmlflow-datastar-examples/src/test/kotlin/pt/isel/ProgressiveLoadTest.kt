package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.WaitForSelectorState
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import pt.isel.infrastructure.SharedTestServers
import pt.isel.infrastructure.SharedTestServersExtension
import kotlin.test.assertEquals

@ExtendWith(SharedTestServersExtension::class)
class ProgressiveLoadTest {
    @ParameterizedTest
    @ValueSource(strings = ["Ktor"])
    fun `click to loads, progressively displays the article and the comments, on Html`(serverType: String) {
        `click to loads, progressively displays the article and the comments`("/progressive-load/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor"])
    fun `click to loads, progressively displays the article and the comments, on HtmlFlow`(serverType: String) {
        `click to loads, progressively displays the article and the comments`("/progressive-load/htmlflow", serverType)
    }

    /**
     * Tests that clicking the "Load" button progressively loads the article and comments sections.
     * Initially, only the header should be loaded. After the first update, the article section should be loaded.
     * Finally, after the second update, the comments section should be loaded.
     */
    private fun `click to loads, progressively displays the article and the comments`(
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
                // Navigate to the lazy-load page
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Verify that div load exists and contains the section with id header, article, comments and footer, but they are not visible yet
                page.waitForSelector(
                    "#Load",
                    Page
                        .WaitForSelectorOptions()
                        .setState(WaitForSelectorState.ATTACHED),
                )
                assertEquals(false, page.isVisible("#load"))
                assertEquals(false, page.isVisible("#header"))
                assertEquals(false, page.isVisible("#article"))
                assertEquals(false, page.isVisible("#comments"))
                assertEquals(false, page.isVisible("#footer"))

                // Verify that they are empty
                assertEquals("", page.textContent("#header")?.trim(), "Header should be empty initially")
                assertEquals("", page.textContent("#article")?.trim(), "Article should be empty initially")
                assertEquals("", page.textContent("#comments")?.trim(), "Comments should be empty initially")
                assertEquals("", page.textContent("#footer")?.trim(), "Footer should be empty initially")

                // Click the "Load" button
                page.click("#load-button")

                // Verify that the now the header, article and comments sections contains content
                page.waitForSelector("#header")
                assertEquals(
                    page.textContent("#header")?.trim()?.isNotEmpty(),
                    true,
                    "Header should not be empty after loading",
                )
                page.waitForSelector("#article h4")
                assertEquals(
                    page.textContent("#article h4")?.trim()?.isNotEmpty(),
                    true,
                    "Article should not be empty after loading",
                )

                page.waitForSelector("#comments h5")
                assertEquals(
                    page.textContent("#comments h5")?.trim()?.isNotEmpty(),
                    true,
                    "Comments should not be empty after loading",
                )

                page.waitForSelector("#footer")
                assertEquals(
                    page.textContent("#footer")?.trim()?.isNotEmpty(),
                    true,
                    "Footer should not be empty after loading",
                )
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}
