package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import pt.isel.ktor.demosKtorRouting
import pt.isel.views.htmlflow.TAB_CONTENTS
import kotlin.test.Test
import kotlin.test.assertEquals

class LazyTabsTest {
    @Test
    fun `click tab and verify content changes on HTML`() {
        `click tab and verify content changes`("/lazy-tabs/html")
    }

    @Test
    fun `click tab and verify content changes on HtmlFlow`() {
        `click tab and verify content changes`("/lazy-tabs/htmlflow")
    }

    fun `click tab and verify content changes`(path: String) {
        val server =
            embeddedServer(Netty, port = 0) {
                demosKtorRouting()
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

                page.waitForSelector("div[role='tablist']")
                page.waitForSelector("div[role='tabpanel']")

                val initialContent = page.textContent("div[role='tabpanel'] p")?.trim()
                assertEquals(TAB_CONTENTS[0], initialContent, "Initial content should be Tab 0 content")

                (1..7).forEach { index ->
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
                server.stop(1000, 2000)
            }
        }
    }
}
