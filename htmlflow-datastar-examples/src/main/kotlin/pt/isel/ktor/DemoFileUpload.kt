package pt.isel.ktor

import dev.datastar.kotlin.sdk.ElementPatchMode
import dev.datastar.kotlin.sdk.PatchElementsOptions
import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.htmlflow.fileUploadTable
import pt.isel.views.htmlflow.hfFileUpload
import java.security.MessageDigest
import kotlin.io.encoding.Base64

private val description = loadResource("public/html/fragments/file-upload-description.html")
private val html =
    loadResource("public/html/file-upload.html")
        .replace("<!-- DESCRIPTION -->", description)

private const val MAX_FILE_SIZE = 1_000_000 // 1 MB

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

private suspend fun RoutingContext.uploadFile() {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))

        val callText = call.request.call.receiveText()
        val (files) = Json.decodeFromString<UploadFilesSignals>(callText)

        val invalidFiles = files.filter { (_, contents, _) -> Base64.decode(contents).decodeToString().length > MAX_FILE_SIZE }
        invalidFiles.forEach { (name, _, _) ->
            generator.executeScript("console.error('File $name is not valid!');")
        }

        val validFiles = files.filterNot { it in invalidFiles }.map { FileInfo(it) }
        generator.patchElements(
            fileUploadTable.render(validFiles),
            PatchElementsOptions(selector = "#file-upload", mode = ElementPatchMode.Replace),
        )
    }
}

fun String.md5(): String {
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

data class FileInfo(
    val name: String,
    val mime: String,
    val plainTex: String,
    val textSize: Int,
    val md5Hash: String,
) {
    constructor(fileSignal: FileSignal) : this(
        name = fileSignal.name,
        mime = fileSignal.mime,
        plainTex = Base64.decode(fileSignal.contents).decodeToString(),
        textSize = Base64.decode(fileSignal.contents).decodeToString().length,
        md5Hash = fileSignal.contents.md5(),
    )
}
