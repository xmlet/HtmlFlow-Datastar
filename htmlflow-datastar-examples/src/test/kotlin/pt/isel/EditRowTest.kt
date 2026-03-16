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
class EditRowTest {
    @ParameterizedTest
    @ValueSource(strings = ["Ktor"])
    fun `edit row user details and save changes on HTML`(serverType: String) {
        `edit row user details and save changes`("/edit-row/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor"])
    fun `edit row user details and save changes on HtmlFlow`(serverType: String) {
        `edit row user details and save changes`("/edit-row/htmlflow", serverType)
    }

    /**
     * Tests that clicking to edit a row allows editing, saving changes updates the row correctly,
     * canceling reverts to original details, and resetting restores all default users.
     */
    private fun `edit row user details and save changes`(
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
                // Navigate to the edit-row page
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page to be ready - table should be visible
                page.waitForSelector("table")

                // Verify initial first user details
                val initialName = page.locator("tbody tr:nth-child(1) td:nth-child(1)").innerText()
                val initialEmail = page.locator("tbody tr:nth-child(1) td:nth-child(2)").innerText()

                assertEquals("Joe Smith", initialName, "Initial first user name should be Joe Smith")
                assertEquals("joe@smith.org", initialEmail, "Initial first user email should be joe@smith.org")

                // Click the Edit button for the first user
                page.click("tbody tr:nth-child(1) button:has-text('Edit')")

                // Wait for edit mode - inputs should be visible
                page.waitForSelector("tbody tr:nth-child(1) input")

                val nameInput = page.locator("tbody tr:nth-child(1) input").first()
                val emailInput = page.locator("tbody tr:nth-child(1) input").nth(1)

                // Modify user details
                nameInput.fill("Joseph Smith Jr.")
                emailInput.fill("joseph.smith@example.com")

                // Click the Save button
                page.click("tbody tr:nth-child(1) button:has-text('Save')")

                // Wait for view mode to return - Edit button should reappear
                page.waitForSelector("tbody tr:nth-child(1) button:has-text('Edit')")

                // Verify updated details are displayed
                val updatedName = page.locator("tbody tr:nth-child(1) td:nth-child(1)").innerText()
                val updatedEmail = page.locator("tbody tr:nth-child(1) td:nth-child(2)").innerText()

                assertEquals("Joseph Smith Jr.", updatedName, "Name should be updated to Joseph Smith Jr.")
                assertEquals("joseph.smith@example.com", updatedEmail, "Email should be updated to joseph.smith@example.com")

                // Click the Edit button again for the second user
                page.click("tbody tr:nth-child(2) button:has-text('Edit')")

                // Wait for edit mode
                page.waitForSelector("tbody tr:nth-child(2) input")

                // Modify second user details
                page.locator("tbody tr:nth-child(2) input").first().fill("Angela MacDowell-Smith")
                page.locator("tbody tr:nth-child(2) input").nth(1).fill("angela.macdowell@example.com")

                // Click the Cancel button
                page.click("tbody tr:nth-child(2) button:has-text('Cancel')")

                // Wait for view mode to return - Edit button should reappear
                page.waitForSelector("tbody tr:nth-child(2) button:has-text('Edit')")

                // Verify original details are retained (cancel discarded changes)
                val nameAfterCancel = page.locator("tbody tr:nth-child(2) td:nth-child(1)").innerText()
                val emailAfterCancel = page.locator("tbody tr:nth-child(2) td:nth-child(2)").innerText()
                assertEquals("Angie MacDowell", nameAfterCancel, "Name should remain Angie MacDowell after cancel")
                assertEquals("angie@macdowell.org", emailAfterCancel, "Email should remain angie@macdowell.org after cancel")

                // Verify first user still has the saved changes
                val firstUserName = page.locator("tbody tr:nth-child(1) td:nth-child(1)").innerText()
                assertEquals("Joseph Smith Jr.", firstUserName, "First user changes should still be saved")

                // Click the Reset button
                page.click("button:has-text('Reset')")

                page.waitForFunction(
                    "document.querySelector('tbody tr:nth-child(1) td:nth-child(1)').innerText === 'Joe Smith'",
                )

                val resetNames = page.locator("tbody tr td:nth-child(1)").allInnerTexts()
                val resetEmails = page.locator("tbody tr td:nth-child(2)").allInnerTexts()

                assertEquals(
                    listOf("Joe Smith", "Angie MacDowell", "Fuqua Tarkenton", "Kim Yee"),
                    resetNames,
                    "All user names should be reset to defaults",
                )
                assertEquals(
                    listOf("joe@smith.org", "angie@macdowell.org", "fuqua@tarkenton.org", "kim@yee.org"),
                    resetEmails,
                    "All user emails should be reset to defaults",
                )

                // Verify that the reset operation completed successfully by editing again
                page.click("tbody tr:nth-child(1) button:has-text('Edit')")

                // Wait for edit mode
                page.waitForSelector("tbody tr:nth-child(1) input")

                val nameInputAfterReset = page.locator("tbody tr:nth-child(1) input").first().inputValue()
                val emailInputAfterReset = page.locator("tbody tr:nth-child(1) input").nth(1).inputValue()

                assertEquals("Joe Smith", nameInputAfterReset, "Name input should show default Joe Smith after reset")
                assertEquals("joe@smith.org", emailInputAfterReset, "Email input should show default joe@smith.org after reset")
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}
