package pt.isel

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import pt.isel.infrastructure.SharedTestServers
import pt.isel.infrastructure.SharedTestServersExtension
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(SharedTestServersExtension::class)
class AllPagesSimpleTest {
    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo counter HTML returns page`(serverType: String) {
        `demo returns page`("/counter/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo counter signals HTML returns page`(serverType: String) {
        `demo returns page`("/counter-signals/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo counter signals HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/counter-signals/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo click to load HTML returns page`(serverType: String) {
        `demo returns page`("/click-to-load/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo click to load HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/click-to-load/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo active search HTML returns page`(serverType: String) {
        `demo returns page`("/active-search/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo active search HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/active-search/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo bulk update HTML returns page`(serverType: String) {
        `demo returns page`("/bulk-update/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo bulk update HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/bulk-update/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo click to edit via signals HTML returns page`(serverType: String) {
        `demo returns page`("/click-to-edit-signals/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo click to edit via signalsHtmlFlow returns page`(serverType: String) {
        `demo returns page`("/click-to-edit-signals/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo click to edit HTML returns page`(serverType: String) {
        `demo returns page`("/click-to-edit/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo click to edit HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/click-to-edit/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo file upload HTML returns page`(serverType: String) {
        `demo returns page`("/file-upload/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo file upload HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/file-upload/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo infinite scroll HTML returns page`(serverType: String) {
        `demo returns page`("/infinite-scroll/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo infinite scroll HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/infinite-scroll/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo inline validation HTML returns page`(serverType: String) {
        `demo returns page`("/inline-validation/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo inline validation HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/inline-validation/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo progressive load HTML returns page`(serverType: String) {
        `demo returns page`("/progressive-load/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo progressive load HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/progressive-load/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo delete row HTML returns page`(serverType: String) {
        `demo returns page`("/delete-row/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo delete row HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/delete-row/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo edit row HTML returns page`(serverType: String) {
        `demo returns page`("/edit-row/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo edit row HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/edit-row/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo lazy load HTML returns page`(serverType: String) {
        `demo returns page`("/lazy-load/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo lazy load HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/lazy-load/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo lazy tabs HTML returns page`(serverType: String) {
        `demo returns page`("/lazy-tabs/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo lazy tabs HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/lazy-tabs/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo todo mvc HTML returns page`(serverType: String) {
        `demo returns page`("/todo-mvc/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo todo mvc HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/todo-mvc/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo progress bar HTML returns page`(serverType: String) {
        `demo returns page`("/progress-bar/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `demo progress bar HtmlFlow returns page`(serverType: String) {
        `demo returns page`("/progress-bar/htmlflow", serverType)
    }

    /**
     * Tests that the router serves the corresponding HTML page for the given path.
     */
    private fun `demo returns page`(
        path: String,
        serverType: String,
    ) {
        val port = SharedTestServers.getPort(serverType)

        val client = HttpClient()
        client.use { client ->
            runBlocking {
                val response = client.get("http://localhost:$port$path")

                // Check that the response status is 200 OK
                assertEquals(HttpStatusCode.OK, response.status)

                // Check that the response contains HTML content
                val body = response.bodyAsText()
                assertTrue(body.isNotEmpty(), "Response body should not be empty")
            }
        }
    }
}
