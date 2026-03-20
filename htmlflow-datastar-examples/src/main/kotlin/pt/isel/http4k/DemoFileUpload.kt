package pt.isel.http4k

import jakarta.ws.rs.Path
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.datastar.MorphMode
import org.http4k.datastar.Selector
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import pt.isel.ktor.FileInfo
import pt.isel.ktor.UploadFilesSignals
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.fileUploadTable
import pt.isel.views.htmlflow.hfFileUpload
import kotlin.io.encoding.Base64

private val description = loadResource("pt/isel/views/fragments/file-upload-description.html")
private val html = loadResource("public/html/file-upload.html")

fun demoFileUpload(): PolyHandler =
    poly(
        "/html" bind Method.GET to ::getFileUploadHtml,
        "/htmlflow" bind Method.GET to ::getFileUploadHtmFlow,
        "" bind Method.POST to ::uploadFile,
        "/description" bindSse Method.GET to ::getFileUploadDescription,
    )

fun getFileUploadHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html; charset=utf-8")

fun getFileUploadHtmFlow(req: Request): Response = Response(OK).body(hfFileUpload).header("Content-Type", "text/html; charset=utf-8")

private const val MAX_FILE_SIZE = 1_000_000 // 1 MB

@Path("/file-upload")
fun uploadFile(req: Request): SseResponse =
    SseResponse { sse ->
        val body = req.bodyString()
        val (files) = Json.decodeFromString<UploadFilesSignals>(body)

        val invalidFiles = files.filter { (_, contents, _) -> Base64.decode(contents).decodeToString().length > MAX_FILE_SIZE }
        invalidFiles.forEach { (name, _, _) ->
            sse.sendPatchElements(
                selector = Selector.of("#body"),
                morphMode = MorphMode.append,
                elements =
                    listOf(
                        Element.of(
                            "<script data-effect=\"el.remove()\">console.error('File $name is not valid!');</script>",
                        ),
                    ),
            )
        }

        val validFiles = files.filterNot { it in invalidFiles }.map { FileInfo(it) }

        sse.sendPatchElements(
            Element.of(fileUploadTable.render(validFiles)),
            selector = Selector.of("#file-upload"),
            morphMode = MorphMode.replace,
        )
    }

@Path("/file-upload/description")
fun getFileUploadDescription(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(
            elements = listOf(Element.of(description)),
        )
    }
