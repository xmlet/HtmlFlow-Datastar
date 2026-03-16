package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.WaitForSelectorState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import pt.isel.infrastructure.SharedTestServers
import pt.isel.infrastructure.SharedTestServersExtension

@ExtendWith(SharedTestServersExtension::class)
class ProgressBarTest {
    @ParameterizedTest
    @ValueSource(strings = ["Ktor"])
    fun `progress bar updates progressively on Html`(serverType: String) {
        `progress bar updates progressively`("/progress-bar/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor"])
    fun `progress bar updates progressively on HtmlFlow`(serverType: String) {
        `progress bar updates progressively`("/progress-bar/htmlflow", serverType)
    }

    private fun `progress bar updates progressively`(
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
            val page: Page = context.newPage()

            try {
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)

                assertEquals(200, response?.status())

                page.waitForSelector(
                    "#progress-bar",
                    Page
                        .WaitForSelectorOptions()
                        .setState(WaitForSelectorState.ATTACHED),
                )

                page.waitForSelector("text=0%")
                assertEquals(
                    "0%",
                    page.textContent("svg text")?.trim(),
                )

                page.waitForFunction(
                    "document.querySelector('svg text')?.textContent !== '0%'",
                )

                val midValue = page.textContent("svg text")?.filter { it.isDigit() }?.toInt()
                assertTrue(midValue != null && midValue > 0)

                page.waitForSelector("text=100%")

                assertEquals(
                    "100%",
                    page.textContent("svg text")?.trim(),
                )

                page.waitForSelector("text=Completed! Try again?")
                assertTrue(
                    page.isVisible("text=Completed! Try again?"),
                )
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}
