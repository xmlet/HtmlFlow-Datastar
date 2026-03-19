package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import pt.isel.infrastructure.SharedTestServers
import pt.isel.infrastructure.SharedTestServersExtension
import pt.isel.views.htmlflow.TAB_CONTENTS
import kotlin.test.assertEquals

@ExtendWith(SharedTestServersExtension::class)
class LazyTabsTest {
    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `click tab and verify content changes on HTML`(serverType: String) {
        `click tab and verify content changes`("/lazy-tabs/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `click tab and verify content changes on HtmlFlow`(serverType: String) {
        `click tab and verify content changes`("/lazy-tabs/htmlflow", serverType)
    }

    private fun `click tab and verify content changes`(
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
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                page.waitForSelector("div[role='tablist']")
                page.waitForSelector("div[role='tabpanel']")

                val initialContent = page.textContent("div[role='tabpanel'] p")?.trim()
                assertEquals(TAB_CONTENTS[0], initialContent, "Initial content should be Tab 0 content")

                (1..<TAB_CONTENTS.size).forEach { index ->
                    val expectedContent = TAB_CONTENTS[index]
                    page.click("button[role='tab']:has-text('Tab $index')")
                    page.waitForSelector("#tabpanel p:has-text('${expectedContent.take(30)}')")

                    val content = page.textContent("#tabpanel p")?.trim()
                    assertEquals(expectedContent, content, "Content should match Tab $index content")
                }

                page.click("button[role='tab']:has-text('Tab 0')")
                page.waitForSelector("#tabpanel p:has-text('${TAB_CONTENTS[0].take(30)}')")
                val contentAfterReturn = page.textContent("#tabpanel p")?.trim()
                assertEquals(TAB_CONTENTS[0], contentAfterReturn, "Content should return to Tab 0 content")
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}
