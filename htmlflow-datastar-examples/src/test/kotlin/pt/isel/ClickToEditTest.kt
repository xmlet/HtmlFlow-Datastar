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
import kotlin.use

@ExtendWith(SharedTestServersExtension::class)
class ClickToEditTest {
    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `click to edit user details and save changes via signals on HTML`(serverType: String) {
        `click to edit user details and save changes`("/click-to-edit-signals/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `click to edit user details and save changes via signals on HtmlFlow`(serverType: String) {
        `click to edit user details and save changes`("/click-to-edit-signals/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `click to edit user details and save changes on HTML`(serverType: String) {
        `click to edit user details and save changes`("/click-to-edit/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `click to edit user details and save changes on HtmlFlow`(serverType: String) {
        `click to edit user details and save changes`("/click-to-edit/htmlflow", serverType)
    }

    /**
     * Tests that clicking to edit user details allows editing, saving changes updates the details correctly,
     * canceling reverts to original details, and resetting restores default user details.
     */
    private fun `click to edit user details and save changes`(
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
                // Navigate to the click-to-load page
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page to be ready - buttons should be visible
                page.waitForSelector("#edit")
                page.waitForSelector("#reset")

                // Click the Edit button
                page.locator("#edit").click()
                page.waitForSelector("label:has-text('First Name')")

                // Modify user details
                page.getByLabel("First Name").fill("Alice")
                page.getByLabel("Last Name").fill("Smith")
                page.getByLabel("Email").fill("alice.smith@org.com")

                // Click the Save button
                page.locator("#save").click()

                // Verify updated details are displayed
                val firstName = page.textContent("p:has-text('First Name')").substringAfter(":").trim()
                val lastName = page.textContent("p:has-text('Last Name')").substringAfter(":").trim()
                val email = page.textContent("p:has-text('Email')").substringAfter(":").trim()
                assertEquals("Alice", firstName, "First name should be updated to Alice")
                assertEquals("Smith", lastName, "Last name should be updated to Smith")
                assertEquals("alice.smith@org.com", email, "Email should be updated to alice.smith@org.com")

                // Click the Edit button again
                page.locator("#edit").click()
                page.waitForSelector("label:has-text('First Name')")

                // Modify user details again
                page.getByLabel("First Name").fill("Bob")
                page.getByLabel("Last Name").fill("Johnson")
                page.getByLabel("Email").fill("bob.jonhson@email.com")
                // Click the Cancel button
                page.locator("#cancel").click()

                page.waitForSelector("#edit-form", Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN))
                page.waitForSelector("p:has-text('First Name')")
                page.waitForTimeout(300.0)

                // Verify original details are retained
                val firstNameAfterCancel = page.textContent("p:has-text('First Name')").substringAfter(":").trim()
                val lastNameAfterCancel = page.textContent("p:has-text('Last Name')").substringAfter(":").trim()
                val emailAfterCancel = page.textContent("p:has-text('Email')").substringAfter(":").trim()
                assertEquals("Alice", firstNameAfterCancel, "First name should remain Alice after cancel")
                assertEquals("Smith", lastNameAfterCancel, "Last name should remain Smith after cancel")
                assertEquals("alice.smith@org.com", emailAfterCancel, "Email should remain alice.smith@org.com after cancel")

                // Click the Reset button and wait for the default details to be displayed
                page.locator("#reset").click()
                page.waitForSelector("p:has-text('First Name')")
                page.waitForSelector("p:has-text('Last Name')")
                page.waitForSelector("p:has-text('Email')")
				
                // Verify default user details are restored
                val defaultFirstName = page.textContent("p:has-text('First Name')").substringAfter(":").trim()
                val defaultLastName = page.textContent("p:has-text('Last Name')").substringAfter(":").trim()
                val defaultEmail = page.textContent("p:has-text('Email')").substringAfter(":").trim()
                assertEquals("John", defaultFirstName, "First name should be reset to default John")
                assertEquals("Doe", defaultLastName, "Last name should be reset to default Doe")
                assertEquals("joe@blow.com", defaultEmail, "Email should be reset to default joe@blow.com")

                // Verify that the reset operation completed successfully
                page.locator("#edit").click()
                page.waitForSelector("label:has-text('First Name')")

                val firstNameAfterReset = page.getByLabel("First Name").inputValue()
                val lastNameAfterReset = page.getByLabel("Last Name").inputValue()
                val emailAfterReset = page.getByLabel("Email").inputValue()
                assertEquals("John", firstNameAfterReset, "First name input should show default John after reset")
                assertEquals("Doe", lastNameAfterReset, "Last name input should show default Doe after reset")
                assertEquals("joe@blow.com", emailAfterReset, "Email input should show default joe@blow.com after reset")
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}
