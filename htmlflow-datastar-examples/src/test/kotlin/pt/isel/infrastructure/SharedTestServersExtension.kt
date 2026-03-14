package pt.isel.infrastructure

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * JUnit 5 Extension that ensures shared test servers are initialized before any tests run.
 * Servers are shut down automatically via a Runtime shutdown hook.
 */
class SharedTestServersExtension : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        // Trigger lazy initialization of servers
        SharedTestServers.ktorPort
        SharedTestServers.http4kPort
    }
}
