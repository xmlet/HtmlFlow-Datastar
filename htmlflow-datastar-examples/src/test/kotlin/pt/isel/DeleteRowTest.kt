package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DeleteRowTest {
    @Test
    fun `delete user row and verify removal on HTML`() {
        `delete user row and verify removal`("/delete-row/html")
    }

    @Test
    fun `delete user row and verify removal on HtmlFlow`() {
        `delete user row and verify removal`("/delete-row/htmlflow")
    }

    @Test
    fun `delete all users and reset on HTML`() {
        `delete all users and reset`("/delete-row/html")
    }

    @Test
    fun `delete all users and reset on HtmlFlow`() {
        `delete all users and reset`("/delete-row/htmlflow")
    }

    /**
     * Tests that clicking the "Delete" button removes a user row from the table.
     */

    fun `delete user row and verify removal`(path: String) {
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

                page.waitForSelector("table")

                // Verifica se o Datastar carregou
                val datastarLoaded = page.evaluate("typeof window.Datastar !== 'undefined'")
                println("Datastar loaded: $datastarLoaded")

                val initialUsersCount = page.querySelectorAll("tbody tr").size
                assertEquals(4, initialUsersCount, "Initial table should have 4 users")

                val firstUserName = page.querySelector("tbody tr:first-child td:first-child")?.innerText()
                assertEquals("Joe Smith", firstUserName, "First user should be Joe Smith")

                // Override window.confirm
                page.evaluate("window.confirm = () => { console.log('Confirm called'); return true; }")

                // Tenta clicar e espera por network idle
                page.click("tbody tr:first-child button.error")

                val usersCountAfterDelete = page.querySelectorAll("tbody tr").size
                println("Users after delete: $usersCountAfterDelete")

                assertEquals(3, usersCountAfterDelete, "Table should have 3 users after deletion")

                val newFirstUserName = page.querySelector("tbody tr:first-child td:first-child")?.innerText()
                assertEquals("Angie MacDowell", newFirstUserName, "First user should now be Angie MacDowell")

                val allNames = page.querySelectorAll("tbody tr td:first-child").map { it.innerText() }
                assertFalse(allNames.contains("Joe Smith"), "Joe Smith should not be in the table anymore")
            } finally {
                page.close()
                context.close()
                browser.close()
                server.stop(1000, 2000)
            }
        }
    }

    /**
     * Tests that deleting all users and clicking "Reset" restores the original users.
     */
    fun `delete all users and reset`(path: String) {
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
                // Navigate to the delete-row page
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the table to be visible
                page.waitForSelector("table")

                val initialUsersCount = page.querySelectorAll("tbody tr").size
                assertEquals(4, initialUsersCount, "Initial table should have 4 users")

                // Override window.confirm to always return true
                page.evaluate("window.confirm = () => true")

                // Delete all users one by one
                repeat(4) { index ->
                    // Click the first delete button
                    page.click("tbody tr:first-child button.error")

                    // Wait for the patch to be applied
                    page.waitForTimeout(200.0)

                    // Verify the count decreased
                    val currentCount = page.querySelectorAll("tbody tr").size
                    println("After deletion ${index + 1}: $currentCount users remaining")
                    assertEquals(4 - (index + 1), currentCount, "Should have ${4 - (index + 1)} users after ${index + 1} deletion(s)")
                }

                // Verify all users are deleted
                val usersCountAfterDeletions = page.querySelectorAll("tbody tr").size
                assertEquals(0, usersCountAfterDeletions, "Table should have 0 users after deleting all")

                // Click the Reset button
                page.click("button.warning")

                // Wait for the patch to be applied
                page.waitForTimeout(200.0)

                // Verify users are restored
                val usersCountAfterReset = page.querySelectorAll("tbody tr").size
                assertEquals(4, usersCountAfterReset, "Table should have 4 users after reset")

                // Verify the original users are back in order
                val userNames = page.querySelectorAll("tbody tr td:first-child").map { it.innerText() }
                assertEquals(
                    listOf("Joe Smith", "Angie MacDowell", "Fuqua Tarkenton", "Kim Yee"),
                    userNames,
                    "Original users should be restored in correct order",
                )

                val userEmails = page.querySelectorAll("tbody tr td:nth-child(2)").map { it.innerText() }
                assertEquals(
                    listOf("joe@smith.org", "angie@macdowell.org", "fuqua@tarkenton.org", "kim@yee.org"),
                    userEmails,
                    "Original user emails should be restored",
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
