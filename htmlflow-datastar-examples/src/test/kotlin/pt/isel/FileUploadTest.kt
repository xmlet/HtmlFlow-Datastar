package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import pt.isel.infrastructure.SharedTestServers
import pt.isel.infrastructure.SharedTestServersExtension
import pt.isel.utils.getResourcePath
import kotlin.test.assertEquals

@ExtendWith(SharedTestServersExtension::class)
class FileUploadTest {
    @ParameterizedTest
    @ValueSource(strings = ["Ktor"])
    fun `upload files, shows file info on HTML`(serverType: String) {
        `upload files, shows file info`("/file-upload/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor"])
    fun `upload files, shows file info on HtmlFlow`(serverType: String) {
        `upload files, shows file info`("/file-upload/htmlflow", serverType)
    }

    /**
     * Tests that uploading files shows their info on the page.
     * Files used on test should be small (less than 1 MB) to avoid triggering the file size limit.
     */
    private fun `upload files, shows file info`(
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

                // Wait for the page to be ready - input should be visible
                page.waitForSelector("input[type='file']")

                // Upload files to the file input

                val helloWorldFile = getResourcePath("test-files/Hello-World.txt")
                val jsonFilePath = getResourcePath("test-files/Student.json")

                page.setInputFiles(
                    "input[type=file]",
                    arrayOf(
                        helloWorldFile,
                        jsonFilePath,
                    ),
                )

                val count =
                    page
                        .locator("input[type=file]")
                        .evaluate("el => el.files.length") as Int

                assertEquals(2, count, "File input should have 2 files after setting input files")

                // Click the 'Submit' button to trigger the upload
                val submit = page.locator("button.warning")
                submit.click()

                // Wait for the file info to be displayed in the table
                page.waitForFunction("document.querySelectorAll('#file-upload tbody tr').length === 2")
                val rows = page.querySelectorAll("#file-upload tbody tr")
                assertEquals(2, rows.size, "Table should have 2 rows for the uploaded files")
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}
