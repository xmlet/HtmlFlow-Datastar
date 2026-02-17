package htmlflow.datastar

import pt.isel.datastar.extensions.convertToValidSignalName
import pt.isel.datastar.extensions.isValidSignalName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataStarUtilsTests {
    @Test
    fun ` verify if isValidSignalName returns true for valid signal names`() {
        assertTrue(isValidSignalName("validSignal"))
        assertTrue(isValidSignalName("anotherValidSignal"))
    }

    @Test
    fun ` verify if isValidSignalName returns false for invalid signal names`() {
        assertTrue(!isValidSignalName("invalid-signal"))
        assertTrue(!isValidSignalName("another-invalid-signal"))
    }

    @Test
    fun `convertValidSignalName should convert invalid signal names to valid ones`() {
        assertEquals(convertToValidSignalName("invalid-signal"), "invalidSignal")
        assertEquals(convertToValidSignalName("another-invalid-signal"), "anotherInvalidSignal")
    }

    @Test
    fun `convertValidSignalName should return the same name if it's already valid`() {
        assertEquals(convertToValidSignalName("validSignal"), "validSignal")
        assertEquals(convertToValidSignalName("anotherValidSignal"), "anotherValidSignal")
    }
}
