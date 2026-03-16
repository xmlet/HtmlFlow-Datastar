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
class TodoMvcTest {
    @ParameterizedTest
    @ValueSource(strings = ["Ktor"])
    fun `todo mvc app works as expected, on Html`(serverType: String) {
        `todo mvc app works as expected`("/todo-mvc/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor"])
    fun `todo mvc app works as expected, on HtmlFlow`(serverType: String) {
        `todo mvc app works as expected`("/todo-mvc/htmlflow", serverType)
    }

    /**
     * Test that the to do-Mvc app works as expected, by performing the following steps:
     * 1. Navigate to the To do-Mvc page.
     * 2. Add a new task and verify that it appears in the list.
     * 3. Toggle the task's status and verify that it updates correctly.
     * 4. Edit the task's description and verify that it updates correctly.
     * 5. Delete the task and verify that it is removed from the list.
     * 6. Add multiple tasks, toggle some of them, and verify that the "Toggle All" functionality works as expected.
     * 7. Verify that the filtering options (All, Pending, Done) work correctly by applying each filter and checking the displayed tasks
     */
    private fun `todo mvc app works as expected`(
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

                // ─────────────────────────────────────────────
                // Initial state (4 default tasks)
                // ─────────────────────────────────────────────
                page.waitForSelector("#todo-list li")
                val initialCount = page.locator("#todo-list li").count()
                assertEquals(4, initialCount)

                // ─────────────────────────────────────────────
                // Add a new task
                // ─────────────────────────────────────────────
                page.fill("#new-todo", "Write Playwright test")
                page.press("#new-todo", "Enter")

                page.waitForTimeout(200.0)
                assertEquals(initialCount + 1, page.locator("#todo-list li").count())

                // ─────────────────────────────────────────────
                // Toggle the new task
                // ─────────────────────────────────────────────
                page
                    .locator("#todo-list li")
                    .last()
                    .locator("input[type=checkbox]")
                    .click()

                // ─────────────────────────────────────────────
                // Edit the task
                // ─────────────────────────────────────────────
                page.locator("#todo-list li").last().dblclick()

                val editInput =
                    page
                        .locator("#todo-list li")
                        .last()
                        .locator("input[type=text]")

                editInput.fill("Write AWESOME Playwright test")
                editInput.press("Enter")

                page.waitForTimeout(200.0)
                page.locator("text=Write AWESOME Playwright test").waitFor()

                // ─────────────────────────────────────────────
                // Delete the task
                // ─────────────────────────────────────────────
                page
                    .locator("#todo-list li")
                    .last()
                    .locator("button.error")
                    .click()

                page.waitForTimeout(200.0)
                assertEquals(initialCount, page.locator("#todo-list li").count())

                val pendingCountBeforeToggle =
                    page.locator("#todo-actions span strong").innerText().trim()

                assertEquals("4", pendingCountBeforeToggle)

                // ─────────────────────────────────────────────
                // Toggle all tasks
                // ─────────────────────────────────────────────
                page.locator("#todo-header input[type=checkbox]").click()
                page.waitForTimeout(200.0)

                // All tasks should now be DONE
                page
                    .locator("#todo-list li input[type=checkbox]")
                    .all()
                    .forEach { checkbox ->
                        assertEquals(true, checkbox.isChecked)
                    }

                // ─────────────────────────────────────────────
                // Filters
                // ─────────────────────────────────────────────

                // Pending → none
                page.locator("#todo-actions button:has-text(\"Pending\")").click()
                page.waitForTimeout(200.0)
                assertEquals(0, page.locator("#todo-list li").count())

                val pendingCount =
                    page.locator("#todo-actions span strong").innerText().trim()

                assertEquals("0", pendingCount)

                // Done → all
                page.click("text=Completed")
                page.waitForTimeout(200.0)
                assertEquals(initialCount, page.locator("#todo-list li").count())

                // All → all
                page.click("text=All")
                page.waitForTimeout(200.0)
                assertEquals(initialCount, page.locator("#todo-list li").count())
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}
