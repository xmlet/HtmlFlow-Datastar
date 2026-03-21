package pt.isel.ktor

import dev.datastar.kotlin.sdk.ElementPatchMode
import dev.datastar.kotlin.sdk.PatchElementsOptions
import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import htmlflow.div
import htmlflow.doc
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.coroutines.delay
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.fragments.hfProgressiveLoadDescription
import pt.isel.views.htmlflow.hfProgressiveLoad
import pt.isel.views.htmlflow.loadDiv

private val html = loadResource("public/html/progressive-load.html")

fun Route.demoProgressiveLoad() {
    route("/progressive-load") {
        get("/html", RoutingContext::getProgressiveLoadHtml)
        get("/htmlflow", RoutingContext::getProgressiveLoadHtmlFlow)
        get("/updates", RoutingContext::getUpdates)
        get("/description", RoutingContext::getProgressiveLoadDescription)
    }
}

private suspend fun RoutingContext.getProgressiveLoadHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getProgressiveLoadHtmlFlow() {
    call.respondText(hfProgressiveLoad, ContentType.Text.Html)
}

private suspend fun RoutingContext.getUpdates() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))

        val loadDiv = StringBuilder().apply { doc { loadDiv() } }.toString()

        delay(1000) // Simulate some delay

        generator.patchElements(loadDiv)

        delay(1000) // Simulate some delay

        generator.patchElements(
            "<section id=\"comments\"><h5>Comments</h5><p>This is the comments section. It will also be progressively loaded as you scroll down.</p><ul id=\"comments-list\"></ul></section>",
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

        delay(1000) // Simulate some delay

        generator.patchElements(footerDiv)

        val blogHeader = """<header id="header">Welcome to my blog</header>"""

        delay(1000) // Simulate some delay
        generator.patchElements(blogHeader)

        delay(1000) // Simulate some delay
        val articleHeader = """<section id="article"><h4>This is my article</h4><section id="articleBody"></section></section>"""
        generator.patchElements(articleHeader)

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
        delay(1000) // Simulate some delay
        generator.patchElements(articleBody)

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
                    @Suppress("ktlint:standard:max-line-length")
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
            val commentHtml =
                """<li id="${index + 1}">$avatarElem${comment.content}</li>"""
            delay(500) // Simulate some delay
            generator.patchElements(
                commentHtml,
                PatchElementsOptions(
                    selector = "#comments-list",
                    mode = ElementPatchMode.Append,
                ),
            )
        }

        val loadButton =
            $$"""<button id="load-button" data-signals:load-disabled="false" data-on:click="$loadDisabled=true; @get('/progressive-load/updates')" data-attr:disabled="$loadDisabled" data-indicator:progressive-Load>Load</button>"""

        delay(1000) // Simulate some delay
        generator.patchElements(loadButton)
    }
}

private suspend fun RoutingContext.getProgressiveLoadDescription() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        generator.patchElements(hfProgressiveLoadDescription)
    }
}

data class Comment(
    val userAvatarUrl: String?,
    val content: String,
)
