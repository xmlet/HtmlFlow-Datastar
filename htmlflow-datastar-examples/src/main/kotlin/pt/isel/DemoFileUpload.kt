@file:Suppress("ktlint:standard:no-wildcard-imports")

package pt.isel

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.views.htmlflow.hfFileUpload
import java.security.MessageDigest
import kotlin.io.encoding.Base64

private val html = loadResource("public/html/file-upload.html")

fun Route.demoFileUpload() {
    route("/file-upload") {
        get("/html", RoutingContext::getFileUploadHtml)
        get("/htmlflow", RoutingContext::getFileUploadHHtmlFlow)
        post("", RoutingContext::uploadFile)
    }
}

private suspend fun RoutingContext.getFileUploadHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getFileUploadHHtmlFlow() {
    call.respondText(hfFileUpload, ContentType.Text.Html)
}

private const val MAX_FILE_SIZE = 1_000_000 // 1 MB

private suspend fun RoutingContext.uploadFile() {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))

        val callText = call.request.call.receiveText()
        val (files) = Json.decodeFromString<UploadFilesSignals>(callText)

        val invalidFiles = files.filter { (_, contents, _) -> Base64.decode(contents).decodeToString().length > MAX_FILE_SIZE }
        invalidFiles.forEach { file ->
            generator.executeScript("console.error('File $file is not valid!');")
        }

        val validFiles = files.filterNot { it in invalidFiles }

        val tableEntries =
            validFiles.joinToString("") { (name, contents, mime) ->
                val plainText = Base64.decode(contents).decodeToString()
                val textSize = plainText.length
                val md5Hash = contents.md5()

                """
                <tr>
                    <td>$name</td>
                    <td>$textSize B</td>
                    <td>$mime</td>
                    <td>$md5Hash</td>
                </tr>    
                """.trimIndent().replace("\n", "")
            }

        val table =
            """
            <table id="file-upload">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Size</th>
                        <th>Mime</th>
                        <th>MD5 Hash</th>
                    </tr>
                </thead> 
                <tbody>
                    $tableEntries
                </tbody>
            </table>
            """.trimIndent().replace("\n", "")

        generator.patchElements(table)
    }
}

private fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.toHexString()
}

@Serializable
data class UploadFilesSignals(
    val files: List<FileSignal>,
)

@Serializable
data class FileSignal(
    val name: String,
    val contents: String,
    val mime: String,
)
