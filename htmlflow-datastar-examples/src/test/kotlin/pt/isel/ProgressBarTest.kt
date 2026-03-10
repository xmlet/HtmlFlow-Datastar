package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.WaitForSelectorState
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import pt.isel.ktor.demoHtmlFlowDatastarRouting
import kotlin.test.Test

class ProgressBarTest {
    @Test
    fun `progress bar updates progressively on Html`() {
        `progress bar updates progressively`("/progress-bar/html")
    }

    @Test
    fun `progress bar updates progressively on HtmlFlow`() {
        `progress bar updates progressively`("/progress-bar/htmlflow")
    }

    fun `progress bar updates progressively`(path: String) {
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
                server.stop(1000, 2000)
            }
        }
    }
}
