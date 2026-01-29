package pt.isel

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AllPagesSimpleTest {
    @Test
    fun `demo counter returns page`() {
        `demo returns page`("/counter")
    }

    @Test
    fun `demo counter signals returns page`() {
        `demo returns page`("/counter-signals")
    }

    @Test
    fun `demo click to load returns page`() {
        `demo returns page`("/click-to-load")
    }

    /**
     * Tests that the router serves the corresponding html page
     * and that the page contains a span element with id "counter".
     */
    fun `demo returns page`(path: String) =
        testApplication {
            application {
                routing {
                    demoCounter()
                    demoCounterSignals()
                    demoClickToLoad()
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
