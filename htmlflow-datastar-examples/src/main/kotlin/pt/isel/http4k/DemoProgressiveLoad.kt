package pt.isel.http4k

import htmlflow.div
import htmlflow.doc
import htmlflow.text
import jakarta.ws.rs.Path
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
import pt.isel.ktor.Comment
import pt.isel.utils.loadResource
import pt.isel.views.fragments.hfProgressiveLoadDescription
import pt.isel.views.htmlflow.hfProgressiveLoad
import pt.isel.views.htmlflow.loadDiv

private val html = loadResource("public/html/progressive-load.html")

fun demoProgressiveLoad(): PolyHandler =
    poly(
        "/html" bind Method.GET to ::getProgressiveLoadHtml,
        "/htmlflow" bind Method.GET to ::getProgressiveLoadHtmlFlow,
        "/description" bindSse Method.GET to ::getProgressiveLoadDescription,
        "/updates" bindSse Method.GET to ::getProgressiveLoadUpdates,
    )

fun getProgressiveLoadHtml(req: Request): Response = Response(OK).body(html).header("Content-Type", "text/html; charset=utf-8")

fun getProgressiveLoadHtmlFlow(req: Request): Response =
    Response(OK).body(hfProgressiveLoad).header("Content-Type", "text/html; charset=utf-8")

@Path("/progressive-load/updates")
fun getProgressiveLoadUpdates(req: Request): SseResponse =
    SseResponse { sse ->
        runBlocking {
            val loadDivHtml = StringBuilder().apply { doc { loadDiv() } }.toString()

            delay(1000)
            sse.sendPatchElements(Element.of(loadDivHtml))

            delay(1000)
            sse.sendPatchElements(
                Element.of(
                    "<section id=\"comments\"><h5>Comments</h5><p>This is the comments section. It will also be progressively loaded as you scroll down.</p><ul id=\"comments-list\"></ul></section>",
                ),
            )

            val footerDiv =
                StringBuilder()
                    .apply {
                        doc {
                            div {
                                attrId("footer")
                                text("Hope you like it")
                            }
                        }
                    }.toString()

            delay(1000)
            sse.sendPatchElements(Element.of(footerDiv))

            delay(1000)
            sse.sendPatchElements(Element.of("""<header id="header">Welcome to my blog</header>"""))

            delay(1000)
            sse.sendPatchElements(
                Element.of("""<section id="article"><h4>This is my article</h4><section id="articleBody"></section></section>"""),
            )

            val articleBody =
                """<section id="articleBody">
                |<p>Nam aenean habitant condimentum dui quisque vulputate enim etiam risus torquent porttitor commodo phasellus natoque ex praesent ac nullam ad erat. 
                |Sit lectus tincidunt habitasse volutpat donec facilisi ante primis eros, purus viverra potenti scelerisque mattis sollicitudin porttitor imperdiet montes 
                |class himenaeos magna tempus at tellus facilisis lorem curae parturient. Cubilia facilisis quam malesuada phasellus auctor himenaeos lacinia dictumst ultrices
                |iaculis magna facilisi cras congue sollicitudin sed adipiscing condimentum. Congue tortor pulvinar dictumst habitasse mauris praesent varius adipiscing gravida quis
                |phasellus mi natoque rutrum. Litora auctor in primis phasellus purus hac platea tristique magnis conubia, laoreet lectus sapien consectetur duis gravida dictum natoque neque sodales
                |justo praesent risus aptent fermentum donec aliquam condimentum cursus nam lobortis. Magnis malesuada convallis turpis hendrerit nibh facilisi primis etiam vivamus auctor velit nec metus 
                |litora est congue blandit nullam nam quis vehicula. Imperdiet pulvinar id tincidunt mus lacinia ornare mollis orci turpis tempus penatibus posuere per massa suscipit proin nibh commodo.
                |</p></section>
                """.trimMargin()

            delay(1000)
            sse.sendPatchElements(Element.of(articleBody))

            val comments =
                listOf(
                    Comment(
                        null,
                        "Varius ad consectetur malesuada ligula ante molestie hac bibendum conubia magna iaculis congue nisi eleifend senectus amet posuere etiam imperdiet nulla quis.",
                    ),
                    Comment(
                        null,
                        "Varius ad consectetur malesuada ligula ante molestie hac bibendum conubia magna, iaculis congue nisi eleifend senectus amet posuere etiam imperdiet nulla quis. taciti dictumst torquent nam rutrum sapien auctor sagittis parturient.",
                    ),
                    Comment(
                        null,
                        "Varius ad consectetur malesuada ligula ante molestie hac bibendum conubia magna, iaculis congue nisi eleifend senectus amet posuere etiam imperdiet nulla quis. taciti dictumst torquent nam rutrum sapien.",
                    ),
                    Comment(
                        null,
                        "Varius ad consectetur malesuada ligula ante molestie hac bibendum conubia magna, iaculis congue nisi eleifend senectus amet posuere etiam.",
                    ),
                    Comment(
                        null,
                        "Varius ad consectetur malesuada ligula ante molestie hac bibendum conubia magna, iaculis congue nisi eleifend senectus amet posuere etiam. imperdiet nulla quis..",
                    ),
                )

            comments.forEachIndexed { index, comment ->
                val avatarElem =
                    if (comment.userAvatarUrl != null) {
                        """<img src="${comment.userAvatarUrl}" alt="Avatar" class="avatar">"""
                    } else {
                        ""
                    }
                val commentHtml = """<li id="${index + 1}">$avatarElem${comment.content}</li>"""
                delay(500)
                sse.sendPatchElements(
                    Element.of(commentHtml),
                    morphMode = MorphMode.append,
                    selector = Selector.of("#comments-list"),
                )
            }

            val loadButton =
                $$"""<button id="load-button" data-signals:load-disabled="false" data-on:click="$loadDisabled=true; @get('/progressive-load/updates')" data-attr:disabled="$loadDisabled" data-indicator:progressive-Load>Load</button>"""

            delay(1000)
            sse.sendPatchElements(Element.of(loadButton))
        }
    }

@Path("/progressive-load/description")
fun getProgressiveLoadDescription(req: Request): SseResponse =
    SseResponse { sse -> sse.sendPatchElements(Element.of(hfProgressiveLoadDescription)) }
