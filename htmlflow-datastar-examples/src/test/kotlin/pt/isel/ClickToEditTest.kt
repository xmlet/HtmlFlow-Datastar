package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.WaitForSelectorState
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import pt.isel.ktor.demoHtmlFlowDatastarRouting
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.use

class ClickToEditTest {
    @Test
    fun `click to edit user details and save changes via signals on HTML`() {
        `click to edit user details and save changes`("/click-to-edit-signals/html")
    }

    @Test
    fun `click to edit user details and save changes via signals on HtmlFlow`() {
        `click to edit user details and save changes`("/click-to-edit-signals/htmlflow")
    }

    /**
     * Tests that clicking to edit user details allows editing, saving changes updates the details correctly,
     * canceling reverts to original details, and resetting restores default user details.
     */
    fun `click to edit user details and save changes`(path: String) {
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
                // Navigate to the click-to-load page
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page to be ready - buttons should be visible
                page.waitForSelector("button:has-text('Edit')")
                page.waitForSelector("button:has-text('Reset')")

                // Click the Edit button
                page.click("button:has-text('Edit')")
                page.waitForSelector("label:has-text('First Name')")
                // Modify user details
                page.getByLabel("First Name").fill("Alice")
                page.getByLabel("Last Name").fill("Smith")
                page.getByLabel("Email").fill("alice.smith@org.com")

                // Click the Save button
                page.click("button:has-text('Save')")

                // Verify updated details are displayed
                val firstName = page.textContent("p:has-text('First Name')").substringAfter(":").trim()
                val lastName = page.textContent("p:has-text('Last Name')").substringAfter(":").trim()
                val email = page.textContent("p:has-text('Email')").substringAfter(":").trim()
                assertEquals("Alice", firstName, "First name should be updated to Alice")
                assertEquals("Smith", lastName, "Last name should be updated to Smith")
                assertEquals("alice.smith@org.com", email, "Email should be updated to alice.smith@org.com")

                // Click the Edit button again
                page.click("button:has-text('Edit')")
                page.waitForSelector("label:has-text('First Name')")

                // Modify user details again
                page.getByLabel("First Name").fill("Bob")
                page.getByLabel("Last Name").fill("Johnson")
                page.getByLabel("Email").fill("bob.jonhson@email.com")
                // Click the Cancel button
                page.click("button:has-text('Cancel')")

                page.waitForSelector("#edit-form", Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN))

                // Verify original details are retained
                val firstNameAfterCancel = page.textContent("p:has-text('First Name')").substringAfter(":").trim()
                val lastNameAfterCancel = page.textContent("p:has-text('Last Name')").substringAfter(":").trim()
                val emailAfterCancel = page.textContent("p:has-text('Email')").substringAfter(":").trim()
                assertEquals("Alice", firstNameAfterCancel, "First name should remain Alice after cancel")
                assertEquals("Smith", lastNameAfterCancel, "Last name should remain Smith after cancel")
                assertEquals("alice.smith@org.com", emailAfterCancel, "Email should remain alice.smith@org.com after cancel")

                // Click the Reset button and wait for the default details to be displayed
                page.click("button:has-text('Reset')")
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
                page.click("button:has-text('Edit')")
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
                server.stop(1000, 2000)
            }
        }
    }
}
