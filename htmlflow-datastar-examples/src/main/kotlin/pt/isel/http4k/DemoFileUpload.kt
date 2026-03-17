package pt.isel.http4k

import kotlinx.serialization.json.Json
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.datastar.Element
import org.http4k.datastar.MorphMode
import org.http4k.datastar.Selector
import org.http4k.routing.bind
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseHandler
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import pt.isel.ktor.UploadFilesSignals
import pt.isel.utils.loadResource
import pt.isel.views.htmlflow.fileUploadTable
import pt.isel.views.htmlflow.hfFileUpload
import kotlin.io.encoding.Base64

private val html = loadResource("public/html/file-upload.html")

fun demoFileUpload(): PolyHandler =
    poly(
        "/html" bind Method.GET to getFileUploadHtml,
        "/htmlflow" bind Method.GET to getFileUploadHtmFlow,
        "" bind Method.POST to uploadFile,
    )

private val getFileUploadHtml: HttpHandler = { _ ->
    Response(OK).body(html).header("Content-Type", "text/html; charset=utf-8")
}

private val getFileUploadHtmFlow: HttpHandler = { _ ->
    Response(OK).body(hfFileUpload).header("Content-Type", "text/html; charset=utf-8")
}

private const val MAX_FILE_SIZE = 1_000_000 // 1 MB

private val uploadFile: SseHandler = { req ->
    SseResponse { sse ->
        val body = req.bodyString()
        val (files) = Json.decodeFromString<UploadFilesSignals>(body)

        val invalidFiles = files.filter { (_, contents, _) -> Base64.decode(contents).decodeToString().length > MAX_FILE_SIZE }
        invalidFiles.forEach { file ->
            sse.sendPatchElements(
                selector = Selector.of("#body"),
                morphMode = MorphMode.append,
                elements =
                    listOf(
                        Element.of(
                            "<script data-effect=\"el.remove()\">console.error('File $file is not valid!');</script>",
                        ),
                    ),
            )
        }

        val validFiles = files.filterNot { it in invalidFiles }

        sse.sendPatchElements(
            Element.of(fileUploadTable().render(validFiles)),
            selector = Selector.of("#file-upload"),
            morphMode = MorphMode.replace,
        )
    }
}
