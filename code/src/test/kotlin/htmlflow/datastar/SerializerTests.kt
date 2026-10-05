package htmlflow.datastar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNull
import org.xmlet.htmlflow.datastar.serialization.NoQuote
import org.xmlet.htmlflow.datastar.serialization.Serializer.serializeComputed
import org.xmlet.htmlflow.datastar.serialization.Serializer.serializeSignals
import org.xmlet.htmlflow.datastar.serialization.Serializer.serializeValue
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializerTests {
    @Test
    fun `simple data class`() {
        val result = serializeValue(Address("Main\nStreet"))
        assertEquals("{street: 'Main\\nStreet'}", result)
    }

    @Test
    fun `nested data class`() {
        val result = serializeValue(Profile("O'Reilly", Address("Main\nStreet")))
        assertEquals("{name: 'O\\'Reilly', address: {street: 'Main\\nStreet'}}", result)
    }

    @Test
    fun `list of primitives`() {
        val result = serializeValue(NumberList(listOf(1, 2, 3)))
        assertEquals("{nums: [1, 2, 3]}", result)
    }

    @Test
    fun `list of mixed types`() {
        val result = serializeValue(listOf(1, 2, 3, "str"))
        assertEquals("[1, 2, 3, 'str']", result)
    }

    @Test
    fun `bare map of mixed types`() {
        val result = serializeValue(mapOf("a" to 1, "b" to "str"))
        assertEquals("{a: 1, b: 'str'}", result)
    }

    @Test
    fun `bare list of primitives`() {
        val result = serializeValue(listOf(1, 2, 3))
        assertEquals("[1, 2, 3]", result)
    }

    @Test
    fun `nested bare collections`() {
        val result = serializeValue(listOf(listOf(1, 2), listOf("a", "b")))
        assertEquals("[[1, 2], ['a', 'b']]", result)
    }

    @Test
    fun `empty list`() {
        val result = serializeValue(emptyList<String>())
        assertEquals("[]", result)
    }

    @Test
    fun `list with null`() {
        val result = serializeValue(listOf(1, null, "a"))
        assertEquals("[1, null, 'a']", result)
    }

    @Test
    fun `list with nll`() {
        val result = serializeValue(listOf(listOf(1, 2), "str"))
        assertEquals("""[[1, 2], 'str']""", result)
    }

    @Test
    fun `top-level IntArray`() {
        assertEquals("[1, 2, 3]", serializeValue(intArrayOf(1, 2, 3)))
    }

    @Test
    fun `top-level LongArray`() {
        assertEquals("[1, 2, 3]", serializeValue(longArrayOf(1L, 2L, 3L)))
    }

    @Test
    fun `top-level DoubleArray`() {
        assertEquals("[1.5, 2.5]", serializeValue(doubleArrayOf(1.5, 2.5)))
    }

    @Test
    fun `top-level FloatArray`() {
        assertEquals("[1.5, 2.5]", serializeValue(floatArrayOf(1.5f, 2.5f)))
    }

    @Test
    fun `top-level ShortArray`() {
        assertEquals("[1, 2]", serializeValue(shortArrayOf(1, 2)))
    }

    @Test
    fun `top-level ByteArray`() {
        assertEquals("[1, 2]", serializeValue(byteArrayOf(1, 2)))
    }

    @Test
    fun `top-level BooleanArray`() {
        assertEquals("[true, false]", serializeValue(booleanArrayOf(true, false)))
    }

    @Test
    fun `top-level CharArray`() {
        assertEquals("['a', 'b']", serializeValue(charArrayOf('a', 'b')))
    }

    @Test
    fun `top-level object array`() {
        assertEquals("[1, 2, 'a']", serializeValue(arrayOf<Any>(1, 2, "a")))
    }

    @Test
    fun `empty IntArray`() {
        assertEquals("[]", serializeValue(intArrayOf()))
    }

    @Test
    fun `nested IntArray in list`() {
        assertEquals("[[1, 2], 'a']", serializeValue(listOf(intArrayOf(1, 2), "a")))
    }

    @Test
    fun `nested object array in list`() {
        assertEquals("[[1, 2], 'a']", serializeValue(listOf(arrayOf(1, 2), "a")))
    }

    @Test
    fun `nested IntArray in map`() {
        assertEquals("{a: [1, 2]}", serializeValue(mapOf("a" to intArrayOf(1, 2))))
    }

    @Test
    fun `nested primitive arrays in data class list`() {
        val result = serializeValue(listOf(NumberList(listOf(1, 2))))
        assertEquals("[{nums: [1, 2]}]", result)
    }

    @Test
    fun `deeply nested`() {
        val result = serializeValue(Outer(Inner("value")))
        assertEquals("{inner: {value: 'value'}}", result)
    }

    @Test
    fun `NoQuote property emits raw JavaScript`() {
        val result = serializeValue(SerializableConfig("John", "{enabled: true}"))
        assertEquals("{name: 'John', options: {enabled: true}}", result)
    }

    @Test
    fun `list of objects`() {
        val result = serializeValue(SerializableConfigGroup(listOf(SerializableRenamedConfig("John", "{enabled: true}"))))
        assertEquals("{items: [{name: 'John', 'raw-options': {enabled: true}}]}", result)
    }

    @Test
    fun `map of objects`() {
        val result = serializeValue(SerializableConfigMap(mapOf("primary" to SerializableRenamedConfig("John", "{enabled: true}"))))
        assertEquals("{items: {primary: {name: 'John', 'raw-options': {enabled: true}}}}", result)
    }

    @Test
    fun `bare list of data classes preserves NoQuote`() {
        val result = serializeValue(listOf(SerializableConfig("John", "{enabled: true}")))
        assertEquals("[{name: 'John', options: {enabled: true}}]", result)
    }

    // ── serializeValue: null and scalar values ──────────────────────────

    @Test
    fun `null value serializes to null`() {
        assertEquals("null", serializeValue(null))
    }

    @Test
    fun `top-level string is quoted`() {
        assertEquals("'hello'", serializeValue("hello"))
    }

    @Test
    fun `string escapes control characters`() {
        assertEquals("""'a\\b\bc\fd\ne\rf\tg'""", serializeValue("a\\b\bc\u000Cd\ne\rf\tg"))
    }

    @Test
    fun `string keeps double quotes unescaped with single quote style`() {
        assertEquals("'He said \"hi\"'", serializeValue("He said \"hi\""))
    }

    @Test
    fun `double quote style keeps single quotes unescaped`() {
        assertEquals("'O\\'Reilly'", serializeValue("O'Reilly"))
    }

    @Test
    fun `top-level numbers are rendered literally`() {
        assertEquals("42", serializeValue(42))
        assertEquals("42", serializeValue(42L))
        assertEquals("1.5", serializeValue(1.5))
        assertEquals("1.5", serializeValue(1.5f))
    }

    @Test
    fun `top-level booleans are rendered literally`() {
        assertEquals("true", serializeValue(true))
        assertEquals("false", serializeValue(false))
    }

    @Test
    fun `char serializes as a quoted single character`() {
        assertEquals("'a'", serializeValue('a'))
    }

// ── serializeValue: maps ────────────────────────────────────────────

    @Test
    fun `map with non-identifier keys quotes them`() {
        assertEquals("{'user-name': 1}", serializeValue(mapOf("user-name" to 1)))
    }

    @Test
    fun `map with numeric keys quotes them`() {
        assertEquals("{'1': 'one'}", serializeValue(mapOf(1 to "one")))
    }

    @Test
    fun `map with null value serializes null`() {
        assertEquals("{a: null}", serializeValue(mapOf<String, Any?>("a" to null)))
    }

    @Test
    fun `empty map serializes to an empty object`() {
        assertEquals("{}", serializeValue(emptyMap<String, Int>()))
    }

    @Test
    fun `nested maps serialize recursively`() {
        assertEquals("{a: {b: 1}}", serializeValue(mapOf("a" to mapOf("b" to 1))))
    }

    @Test
    fun `double quote style applies to map values but not to keys`() {
        assertEquals("{'user-name': 'on'}", serializeValue(mapOf("user-name" to "on"), quote = true))
    }

    // ── serializeValue: collections ─────────────────────────────────────

    @Test
    fun `set serializes in iteration order`() {
        assertEquals("[1, 2, 3]", serializeValue(linkedSetOf(1, 2, 3)))
    }

    @Test
    fun `double quote style applies inside collections`() {
        assertEquals("['a', 'b']", serializeValue(listOf("a", "b"), quote = true))
    }

    // ── serializeValue: kotlinx JSON elements ───────────────────────────

    @Test
    fun `json null serializes to null`() {
        assertEquals("null", serializeValue(JsonNull))
    }

    // ── serializeValue: kotlinx serializer fallback ─────────────────────

    @Test
    fun `enum serializes as a quoted string`() {
        assertEquals("'RED'", serializeValue(Color.RED))
    }

    @Test
    fun `non serializable values are quoted so the output stays valid javascript`() {
        assertEquals("PlainValue(id=7)", serializeValue(PlainValue(7)))
        assertEquals("2026-01-01", serializeValue(java.time.LocalDate.of(2026, 1, 1)))
    }

    @Test
    fun `nullable property with null value is explicit`() {
        assertEquals("{name: 'x', extra: null}", serializeValue(OptionalConfig("x", null)))
    }

    @Test
    fun `default values are encoded`() {
        assertEquals("{name: 'anon'}", serializeValue(DefaultedConfig()))
    }

    @Test
    fun `serial name is used as the object key instead of the property name`() {
        assertEquals("{renamedOptions: 'x'}", serializeValue(IdentifierRenamedConfig("x")))
    }

    @Test
    fun `double quote style applies to nested data classes`() {
        val result = serializeValue(Profile("O'Reilly", Address("Main\nStreet")), quote = true)
        assertEquals("{name: 'O\\'Reilly', address: {street: 'Main\\nStreet'}}", result)
    }

    // ── serializeSignals ────────────────────────────────────────────────

    @Test
    fun `empty signal list produces an empty object`() {
        assertEquals("{}", emptyList<Pair<String, Any?>>().serializeSignals())
    }

    @Test
    fun `signals with mixed scalar values and null`() {
        val result = listOf("age" to 30, "active" to true, "name" to "John", "missing" to null).serializeSignals()
        assertEquals("{age: 30, active: true, name: 'John', missing: null}", result)
    }

    @Test
    fun `signal name that is not an identifier is quoted`() {
        val result = listOf("user-name" to "O'Reilly").serializeSignals()
        assertEquals("{'user-name': 'O\\'Reilly'}", result)
    }

    @Test
    fun `signal value that is a collection`() {
        val result = listOf("tags" to listOf("a", "b")).serializeSignals()
        assertEquals("{tags: ['a', 'b']}", result)
    }

    // ── serializeComputed ───────────────────────────────────────────────

    @Test
    fun `computed signal with a simple name`() {
        assertEquals("{total: () => price * qty}", serializeComputed("total", "price * qty"))
    }

    @Test
    fun `computed signal with a non identifier name is quoted`() {
        assertEquals("{'full-name': () => first + last}", serializeComputed("full-name", "first + last"))
    }

    @Test
    fun `computed expression is passed through untouched`() {
        assertEquals("""{label: () => 'a' + "b"}""", serializeComputed("label", "'a' + \"b\""))
    }

    // ── Test types ──────────────────────────────────────────────────────

    @Serializable
    class Address(
        val street: String,
    )

    @Serializable
    class Profile(
        val name: String,
        val address: Address,
    )

    @Serializable
    data class Inner(
        val value: String,
    )

    @Serializable
    data class Outer(
        val inner: Inner,
    )

    @Serializable
    data class NumberList(
        val nums: List<Int>,
    )

    @Serializable
    data class AnyList(
        val anies: List<Int>,
    )

    @Serializable
    data class SerializableConfig(
        val name: String,
        @NoQuote val options: String,
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

    @Serializable
    data class SerializableConfigMap(
        val items: Map<String, SerializableRenamedConfig>,
    )

    @Serializable
    data class OptionalConfig(
        val name: String,
        val extra: String?,
    )

    @Serializable
    data class DefaultedConfig(
        val name: String = "anon",
    )

    @Serializable
    data class IdentifierRenamedConfig(
        @SerialName("renamedOptions")
        val options: String,
    )

    @Serializable
    enum class Color {
        RED,
        GREEN,
    }

    data class PlainValue(
        val id: Int,
    )
}
