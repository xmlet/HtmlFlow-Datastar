package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import pt.isel.ktor.demosKtorRouting
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.use

class BulkUpdateTest {
    @Test
    fun `update all users status change status column  on HTML`() {
        `update all users status change status column`("/bulk-update/html")
    }

    @Test
    fun `update all users status change status column on HtmlFlow`() {
        `update all users status change status column`("/bulk-update/htmlflow")
    }

    /**
     * Tests that selecting all users and then clicking "Activate" or "Deactivate"
     * updates the status column for all selected users in the HTML table.
     */
    fun `update all users status change status column`(path: String) {
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
                // Navigate to the active-search page
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page to be ready - users table should be visible
                page.waitForSelector("table")

                val usersCount = page.querySelectorAll("tbody tr").size
                assertEquals(4, usersCount, "Initial table should have 4 users")

                // Select all users by clicking the "Select All" checkbox
                page.check("thead input[type='checkbox']")
                // Click the "Deactivate" button to update status
                page.click("button.error")
                // Wait for the status column to update
                page.waitForTimeout(500.0) // Adjust timeout as needed for the update to complete
                // Verify that all users now have status "INACTIVE"
                val statusesAfterDeactivate =
                    page
                        .querySelectorAll("tbody tr td:nth-child(4)")
                        .map { it.innerText().trim() }
                assertEquals(List(usersCount) { "Inactive" }, statusesAfterDeactivate, "All users should be INACTIVE after deactivation")

                // Select all users again
                page.check("thead input[type='checkbox']")
                // Click the "Activate" button to update status
                page.click("button.success")
                // Wait for the status column to update
                page.waitForTimeout(500.0) // Adjust timeout as needed for the update to
                // Verify that all users now have status "ACTIVE"
                val statusesAfterActivate =
                    page
                        .querySelectorAll("tbody tr td:nth-child(4)")
                        .map { it.innerText().trim() }
                assertEquals(List(usersCount) { "Active" }, statusesAfterActivate, "All users should be ACTIVE after activation")
            } finally {
                page.close()
                context.close()
                browser.close()
                server.stop(1000, 2000)
            }
        }
    }
}
