package htmlflow.datastar

import htmlflow.div
import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignals
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.Click
import kotlin.test.Test
import kotlin.test.assertEquals

class SignalComplexDomainTest {
    @Test
    fun `Signal with complex Domain of the Datastar Frontend Reactivity`() {
        val out = demoDastarRx
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
        StringBuilder()
            .apply {
                doc {
                    html {
                        head {
                            script {
                                attrType(EnumTypeScriptType.MODULE)
                                attrSrc("/js/datastar.js")
                            }
                            link {
                                attrRel(EnumRelType.STYLESHEET)
                                attrHref("/css/styles.css")
                            }
                        }
                        body {
                            div {
                                attrId("person-info")
                                val (person) =
                                    dataSignals(
                                        "person" to Person("John Doe", 30),
                                    )
                                p { dataText { +person.on(Person::name) } }
                                p { dataText { +person.on(Person::age) } }
                                div {
                                    input {
                                        attrType(EnumTypeInputType.TEXT)
                                        dataBind("updatedUserAge")
                                    }
                                    button {
                                        dataOn(Click) { put(::updateUserAge) }
                                        text("Update user age")
                                    }
                                }
                                button {
                                    dataOn(Click) { put(::switchUser) }
                                }
                            }
                        }
                    }
                }
            }

    @Path("/complex-domain/increase-age")
    private fun updateUserAge() {}

    @Path("/complex-domain/switch-user")
    private fun switchUser() {}

    data class Person(
        val name: String,
        val age: Int,
    )

    private val expectedDatastarRx =
        $$"""
            <!DOCTYPE html>
            <html>
                <head>
                    <script type="module" src="/js/datastar.js">
                    </script>
                    <link rel="stylesheet" href="/css/styles.css">
                </head>
                <body>
                    <div id="person-info" data-signals="{person: {age: 30, name: 'John Doe'}}">
                        <p data-text="$person.name">
                        </p>
                        <p data-text="$person.age">
                        </p>
                        <div>
                            <input type="text" data-bind:updatedUserAge="">
                            <button data-on:click="@put('/complex-domain/increase-age')">
                            Update user age
                            </button>
                        </div>
                        <button data-on:click="@put('/complex-domain/switch-user')">
                        </button>
                    </div>
                </body>
            </html>        
        """.trimMargin()
}
