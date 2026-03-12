package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import pt.isel.ktor.demosKtorRouting
import pt.isel.utils.getResourcePath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.use

class FileUploadTest {
    @Test
    fun `upload files, shows file info on HTML`() {
        `upload files, shows file info`("/file-upload/html")
    }

    @Test
    fun `upload files, shows file info on HtmlFlow`() {
        `upload files, shows file info`("/file-upload/htmlflow")
    }

    /**
     * Tests that uploading files shows their info on the page.
     * Files used on test should be small (less than 1 MB) to avoid triggering the file size limit.
     */
    fun `upload files, shows file info`(path: String) {
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
                server.stop(1000, 2000)
            }
        }
    }
}
