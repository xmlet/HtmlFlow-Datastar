package htmlflow.datastar

import htmlflow.div
import htmlflow.doc
import htmlflow.html
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.attributes.dataSignals
import org.xmlet.htmlflow.datastar.attributes.dataStyle
import org.xmlet.htmlflow.datastar.serialization.NoQuote
import org.xmlet.htmlflow.datastar.serialization.Serializer.serializeValue
import kotlin.test.Test
import kotlin.test.assertEquals

class JavaScriptLiteralsTests {
    @Test
    fun `data signal values escape string literals and object keys`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                dataSignal("user-name", "O'Reilly\\Books\n")
                            }
                        }
                    }
                }.toString()

        assertEquals(
            """
            <!DOCTYPE html>
            <html>
            	<div data-signals="{'user-name': 'O\'Reilly\\Books\n'}">
            	</div>
            </html>
            """.trimIndent(),
            html,
        )
    }

    @Test
    fun `class serialization escapes nested object keys and string values`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                dataSignals("profile" to Profile("O'Reilly", Address("Main\nStreet")))
                            }
                        }
                    }
                }.toString()

        assertEquals(
            """
            <!DOCTYPE html>
            <html>
            	<div data-signals="{profile: {name: 'O\'Reilly', address: {street: 'Main\nStreet'}}}">
            	</div>
            </html>
            """.trimIndent(),
            html,
        )
    }

    @Test
    fun `object-style attributes escape keys consistently`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                val signal = dataSignal("is-active", true)
                                dataAttr("aria-expanded" to signal)
                                dataStyle("background-color" to "$signal ? 'red' : 'blue'")
                            }
                        }
                    }
                }.toString()

        assertEquals(
            $$"""
            <!DOCTYPE html>
            <html>
            	<div data-signals="{'is-active': true}" data-attr="{'aria-expanded': $is-active}" data-style="{'background-color': $is-active ? 'red' : 'blue'}">
            	</div>
            </html>
            """.trimIndent(),
            html,
        )
    }

    @Test
    fun `setValue serializes simple value`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                val addressSignal = dataSignal("name", "John")
                                dataInit {
                                    addressSignal.setValue("Doe")
                                }
                            }
                        }
                    }
                }.toString()

        assertEquals(
            $$"""
            <!DOCTYPE html>
            <html>
            	<div data-signals="{name: 'John'}" data-init="$name = 'Doe'">
            	</div>
            </html>
            """.trimIndent(),
            html,
        )
    }

    @Test
    fun `setValue serializes class value`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                val addressSignal = dataSignal("address", Address("Main\nStreet"))
                                dataInit {
                                    addressSignal.setValue(Address("Second\nStreet"))
                                }
                            }
                        }
                    }
                }.toString()

        assertEquals(
            $$"""
            <!DOCTYPE html>
            <html>
            	<div data-signals="{address: {street: 'Main\nStreet'}}" data-init="$address = {street: 'Second\nStreet'}">
            	</div>
            </html>
            """.trimIndent(),
            html,
        )
    }

    @Test
    fun `setValue serializes nested class value`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                val addressSignal = dataSignal("profile", Profile("John", Address("Main\nStreet")))
                                dataInit {
                                    addressSignal.setValue(Profile("Doe", Address("Second\nStreet")))
                                }
                            }
                        }
                    }
                }.toString()

        assertEquals(
            $$"""
            <!DOCTYPE html>
            <html>
            	<div data-signals="{profile: {name: 'John', address: {street: 'Main\nStreet'}}}" data-init="$profile = {name: 'Doe', address: {street: 'Second\nStreet'}}">
            	</div>
            </html>
            """.trimIndent(),
            html,
        )
    }

    @Test
    fun `serializable signal value renders as JavaScript literal and supports raw properties`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                dataSignal("config", SerializableConfig("John", "{enabled: true}"))
                            }
                        }
                    }
                }.toString()

        assertEquals(
            """
            <!DOCTYPE html>
            <html>
            	<div data-signals="{config: {name: 'John', options: {enabled: true}}}">
            	</div>
            </html>
            """.trimIndent(),
            html,
        )
    }

    @Test
    fun `raw property metadata follows serialized names and list element types`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                dataSignal(
                                    "config",
                                    SerializableConfigGroup(
                                        listOf(SerializableRenamedConfig("John", "{enabled: true}")),
                                    ),
                                )
                            }
                        }
                    }
                }.toString()

        assertEquals(
            """
            <!DOCTYPE html>
            <html>
            	<div data-signals="{config: {items: [{name: 'John', 'raw-options': {enabled: true}}]}}">
            	</div>
            </html>
            """.trimIndent(),
            html,
        )
    }

    @Test
    fun `raw property metadata follows map value types`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                dataSignal(
                                    "config",
                                    SerializableConfigMap(
                                        mapOf("primary" to SerializableRenamedConfig("John", "{enabled: true}")),
                                    ),
                                )
                            }
                        }
                    }
                }.toString()

        assertEquals(
            """
            <!DOCTYPE html>
            <html>
            	<div data-signals="{config: {items: {primary: {name: 'John', 'raw-options': {enabled: true}}}}}">
            	</div>
            </html>
            """.trimIndent(),
            html,
        )
    }

    @Test
    fun `list of mixed types`() {
        val result = serializeValue(listOf(1, 2, 3, "str"))
        assertEquals("""[1, 2, 3, 'str']""", result)
    }

    @Serializable
    data class SerializableConfigMap(
        val items: Map<String, SerializableRenamedConfig>,
    )

    @Serializable
    data class Profile(
        val name: String,
        val address: Address,
    )

    @Serializable
    data class Address(
        val street: String,
    )

    @Serializable
    data class SerializableConfig(
        val name: String,
        @NoQuote
        val options: String,
    )

    @Serializable
    data class SerializableConfigGroup(
        val items: List<SerializableRenamedConfig>,
    )

    @Serializable
    data class SerializableRenamedConfig(
        val name: String,
        @SerialName("raw-options")
        @NoQuote
        val options: String,
    )
}
