package org.xmlet.htmlflow.datastar.expressions

import kotlin.time.Duration

/**
 * Holds optional configuration values for a DataStar action.
 *
 * These options are serialized into the action argument object when present.
 */
class ActionOptions {
    var contentType: ContentType? = null
    var filterSignals: SignalPatchFilter.() -> Unit = {}
    var selector: String? = null
    var headers: String? = null
    var openWhenHidden: Boolean? = null
    var payload: String? = null
    var retry: RetryMode? = null
    var retryInterval: Duration? = null
    var retryScaler: Int? = null
    var retryMaxCount: Int? = null
    var requestCancellation: RequestCancellationMode? = null

    override fun toString(): String {
        val parts =
            buildList {
                contentType?.let { add("contentType: '${it.name.lowercase()}'") }

                val filter = SignalPatchFilter().apply(filterSignals).toString()
                if (filter != "{}") {
                    add("filterSignals: $filter")
                }

                selector?.let { add("selector: '$it'") }
                headers?.let { add("headers: \"$it\"") }
                openWhenHidden?.let { add("openWhenHidden: $it") }
                payload?.let { add("payload: \"$it\"") }
                retry?.let { add("retry: '${it.name.lowercase()}'") }
                retryInterval?.let { add("retryInterval: ${it.inWholeMilliseconds}") }
                retryScaler?.let { add("retryScaler: $it") }
                retryMaxCount?.let { add("retryMaxCount: $it") }
                requestCancellation?.let { add("requestCancellation: '${it.name.lowercase()}'") }
            }

        return if (parts.isEmpty()) "" else parts.joinToString(prefix = "{", postfix = "}")
    }
}

enum class ContentType {
    JSON,
    FORM,
}

enum class RetryMode {
    AUTO,
    ERROR,
    ALWAYS,
    NEVER,
}

enum class RequestCancellationMode {
    AUTO,
    CLEANUP,
    DISABLED,
}
