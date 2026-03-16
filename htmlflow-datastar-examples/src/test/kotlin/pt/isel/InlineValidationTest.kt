package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import pt.isel.infrastructure.SharedTestServers
import pt.isel.infrastructure.SharedTestServersExtension
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@ExtendWith(SharedTestServersExtension::class)
class InlineValidationTest {
    @ParameterizedTest
    @ValueSource(strings = ["Ktor"])
    fun `input fields should be validated inline on HTML`(serverType: String) {
        `input fields should be validated inline`("/inline-validation/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor"])
    fun `input fields should be validated inline on HtmlFlow`(serverType: String) {
        `input fields should be validated inline`("/inline-validation/htmlflow", serverType)
    }

    /**
     * Tests that input fields are validated inline and display correct error messages when
     * the validation fails.
     */
    private fun `input fields should be validated inline`(
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
                // Navigate to the active-search page
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the form to be present
                page.waitForSelector("#demo")

                val emailInput = "input[data-bind\\:email]"
                val firstNameInput = "input[data-bind\\:first-name]"
                val lastNameInput = "input[data-bind\\:last-name]"
                val submitButton = "button.success"

                // Fill inputs with invalid values
                page.fill(emailInput, "invalid-email")
                page.keyboard().press("Tab") // triggers keydown

                page.waitForTimeout(600.0)

                page.fill(firstNameInput, "A")
                page.keyboard().press("Tab") // triggers keydown

                page.waitForTimeout(600.0)

                page.fill(lastNameInput, "B")
                page.keyboard().press("Tab") // triggers keydown

                page.waitForTimeout(600.0)

                // Wait for error <p> elements to be rendered
                page.waitForSelector("#email-error")
                page.waitForSelector("#first-name-error")
                page.waitForSelector("#last-name-error")

                // Assert they are visible
                assertTrue(page.isVisible("#email-error"))
                assertTrue(page.isVisible("#first-name-error"))
                assertTrue(page.isVisible("#last-name-error"))

                // Fix inputs
                page.fill(emailInput, "test@test.com")
                page.keyboard().press("Tab") // triggers keydown
                page.waitForTimeout(600.0)

                page.fill(firstNameInput, "Alice")
                page.keyboard().press("Tab") // triggers keydown
                page.waitForTimeout(600.0)

                page.fill(lastNameInput, "Smith")
                page.keyboard().press("Tab") // triggers keydown
                page.waitForTimeout(600.0)

                // Assert that error messages are not visible
                assertFalse(page.isVisible("#email-error"))
                assertFalse(page.isVisible("#first-name-error"))
                assertFalse(page.isVisible("#last-name-error"))

                // Wait until the button becomes enabled
                page.waitForSelector("button.success")

                // Submit
                page.click(submitButton)
                page.waitForSelector("#demo:has-text('Thank you for signing up!')")

                val demoText = page.textContent("#demo")
                assertTrue(demoText!!.contains("Thank you for signing up!"))
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}
