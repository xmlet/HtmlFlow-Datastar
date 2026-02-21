package htmlflow.datastar

import pt.isel.datastar.Signal
import kotlin.test.Test
import kotlin.test.assertEquals

class DataStarUtilsTests {
    @Test
    fun ` verify if signal name in snake case toString() returns camel case name `() {
        val firstName = Signal("first_name")
        val expected = $$"$firstName"
        assertEquals(firstName.toString(), expected)
    }

    @Test
    fun ` verify if signal name is in kebab case toString() returns camel case name`() {
        val firstName = Signal("first-name")
        val expected = $$"$firstName"
        assertEquals(firstName.toString(), expected)
    }

    @Test
    fun `verify leading '_' don't get removed after being converted to camel case`() {
        val signal = Signal("_my_signal")
        val expected = $$"$_mySignal"
        assertEquals(signal.toString(), expected)

        val signal2 = Signal("_my-signal")
        val expected2 = $$"$_mySignal"
        assertEquals(signal2.toString(), expected2)
    }
}
