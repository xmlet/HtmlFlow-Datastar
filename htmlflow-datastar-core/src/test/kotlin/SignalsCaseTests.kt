import pt.isel.datastar.Signal
import pt.isel.datastar.modifiers.CaseStyle
import kotlin.test.Test
import kotlin.test.assertEquals

class SignalsCaseTests {
    @Test
    fun `snake_case and kebab-case signal names are converted to camelCase`() {
        val cases =
            listOf(
                "first_name" to $$"$firstName",
                "first-name" to $$"$firstName",
                "user_profile_id" to $$"$userProfileId",
                "user-profile-id" to $$"$userProfileId",
            )

        cases.forEach { (input, expected) ->
            // Act
            val signal = Signal(input, null)

            // Assert
            assertEquals(
                expected,
                signal.toString(),
                "Failed converting '$input' to camelCase",
            )
        }
    }

    @Test
    fun `leading underscores are preserved when converting to camelCase`() {
        val cases =
            listOf(
                "_my_signal" to $$"$_mySignal",
                "_my-signal" to $$"$_mySignal",
                "_private_value" to $$"$_privateValue",
            )

        cases.forEach { (input, expected) ->
            val signal = Signal(input, null)

            assertEquals(
                expected,
                signal.toString(),
                "Leading underscores were not preserved for '$input'",
            )
        }
    }

    @Test
    fun `already camelCase signal names remain unchanged`() {
        val signal = Signal("alreadyCamelCase", null)

        assertEquals(
            $$"$alreadyCamelCase",
            signal.toString(),
        )
    }

    @Test
    fun `caseStyle determines toString output regardless of input name format`() {
        val cases =
            listOf(
                // input name      case style          expected output
                Triple("first_name", CaseStyle.CAMEL, $$"$firstName"),
                Triple("first-name", CaseStyle.CAMEL, $$"$firstName"),
                Triple("firstName", CaseStyle.CAMEL, $$"$firstName"),
                Triple("first_name", CaseStyle.PASCAL, $$"$FirstName"),
                Triple("first-name", CaseStyle.PASCAL, $$"$FirstName"),
                Triple("firstName", CaseStyle.PASCAL, $$"$FirstName"),
                Triple("first_name", CaseStyle.SNAKE, $$"$first_name"),
                Triple("first-name", CaseStyle.SNAKE, $$"$first_name"),
                Triple("firstName", CaseStyle.SNAKE, $$"$first_name"),
                Triple("first_name", CaseStyle.KEBAB, $$"$first-name"),
                Triple("first-name", CaseStyle.KEBAB, $$"$first-name"),
                Triple("firstName", CaseStyle.KEBAB, $$"$first-name"),
            )

        cases.forEach { (input, style, expected) ->
            // Act
            val signal = Signal(input, null, style)

            // Assert
            assertEquals(
                expected,
                signal.toString(),
                "caseStyle=$style did not dominate formatting for input '$input'",
            )
        }
    }
}
