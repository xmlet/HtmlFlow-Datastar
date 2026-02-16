package pt.isel

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import pt.isel.ktor.demoActiveSearch
import pt.isel.ktor.demoBulkUpdate
import pt.isel.ktor.demoClickToEdit
import pt.isel.ktor.demoClickToLoad
import pt.isel.ktor.demoCounter
import pt.isel.ktor.demoCounterSignals
import pt.isel.ktor.demoDeleteRow
import pt.isel.ktor.demoEditRow
import pt.isel.ktor.demoFileUpload
import pt.isel.ktor.demoInfiniteScroll
import pt.isel.ktor.demoInlineValidation
import pt.isel.ktor.demoLazyLoad
import pt.isel.ktor.demoLazyTabs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AllPagesSimpleTest {
    @Test
    fun `demo counter HTML returns page`() {
        `demo returns page`("/counter/html")
    }

    @Test
    fun `demo counter signals HTML returns page`() {
        `demo returns page`("/counter-signals/html")
    }

    @Test
    fun `demo counter signals HtmlFlow returns page`() {
        `demo returns page`("/counter-signals/htmlflow")
    }

    @Test
    fun `demo click to load HTML returns page`() {
        `demo returns page`("/click-to-load/html")
    }

    @Test
    fun `demo click to load HtmlFlow returns page`() {
        `demo returns page`("/click-to-load/htmlflow")
    }

    @Test
    fun `demo active search HTML returns page`() {
        `demo returns page`("/active-search/html")
    }

    @Test
    fun `demo active search HtmlFlow returns page`() {
        `demo returns page`("/active-search/htmlflow")
    }

    @Test
    fun `demo bulk update HTML returns page`() {
        `demo returns page`("/bulk-update/html")
    }

    @Test
    fun `demo bulk update HtmlFlow returns page`() {
        `demo returns page`("/bulk-update/htmlflow")
    }

    @Test
    fun `demo click to edit HTML returns page`() {
        `demo returns page`("/click-to-edit/html")
    }

    @Test
    fun `demo click to edit HtmlFlow returns page`() {
        `demo returns page`("/click-to-edit/htmlflow")
    }

    @Test
    fun `demo file upload HTML returns page`() {
        `demo returns page`("/file-upload/html")
    }

    @Test
    fun `demo file upload HtmlFlow returns page`() {
        `demo returns page`("/file-upload/htmlflow")
    }

    @Test
    fun `demo infinite scroll HTML returns page`() {
        `demo returns page`("/infinite-scroll/html")
    }

    @Test
    fun `demo infinite scroll HtmlFlow returns page`() {
        `demo returns page`("/infinite-scroll/htmlflow")
    }

    @Test
    fun `demo inline validation HTML returns page`() {
        `demo returns page`("/inline-validation/html")
    }

    @Test
    fun `demo inline validation HtmlFlow returns page`() {
        `demo returns page`("/inline-validation/htmlflow")
    }

    @Test
    fun `demo progressive load HTML returns page`() {
        `demo returns page`("/progressive-load/html")
    }

    @Test
    fun `demo progressive load HtmlFlow returns page`() {
        `demo returns page`("/progressive-load/htmlflow")
    }

    @Test
    fun `demo delete row HTML returns page`() {
        `demo returns page`("/delete-row/html")
    }

    @Test
    fun `demo delete row HtmlFlow returns page`() {
        `demo returns page`("/delete-row/htmlflow")
    }

    @Test
    fun `demo edit row HTML returns page`() {
        `demo returns page`("/edit-row/html")
    }

    @Test
    fun `demo edit row HtmlFlow returns page`() {
        `demo returns page`("/edit-row/htmlflow")
    }

    @Test
    fun `demo lazy load HTML returns page`() {
        `demo returns page`("/lazy-load/html")
    }

    @Test
    fun `demo lazy load HtmlFlow returns page`() {
        `demo returns page`("/lazy-load/htmlflow")
    }

    @Test
    fun `demo lazy tabs HTML returns page`() {
        `demo returns page`("/lazy-tabs/html")
    }

    @Test
    fun `demo lazy tabs HtmlFlow returns page`() {
        `demo returns page`("/lazy-tabs/htmlflow")
    }

    /**
     * Tests that the router serves the corresponding HTML page for the given path.
     */
    fun `demo returns page`(path: String) =
        testApplication {
            application {
                routing {
                    demoCounter()
                    demoCounterSignals()
                    demoClickToLoad()
                    demoActiveSearch()
                    demoBulkUpdate()
                    demoClickToEdit()
                    demoFileUpload()
                    demoInfiniteScroll()
                    demoInlineValidation()
                    demoDeleteRow()
                    demoEditRow()
                    demoLazyLoad()
                    demoLazyTabs()
                    // demoProgressiveLoad()
                }
            }

            // Make a GET request to /counter-signals
            val response = client.get(path)

            // Check that the response status is 200 OK
            assertEquals(HttpStatusCode.OK, response.status)

            // Check that the response contains HTML content
            val body = response.bodyAsText()
            assertTrue(body.isNotEmpty(), "Response body should not be empty")
        }
}
