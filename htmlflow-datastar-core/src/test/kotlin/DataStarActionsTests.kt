import htmlflow.div
import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import pt.isel.datastar.actions.Action
import pt.isel.datastar.extensions.dataSignal
import kotlin.test.Test
import kotlin.test.assertEquals

class DataStarActionsTests {
    @Test
    fun `actions toString() must return correct js string `() {
        StringBuilder()
            .apply {
                doc {
                    html {
                        div {
                            val bar = dataSignal("bar", "initialValue")
                            val peekAction = Action.peek { "() => $bar" }
                            assertEquals("@peek(() => $bar)", peekAction.toString())
                        }
                    }
                }
            }

        val setAllActions = Action.setAll(true, "{include: /^foo$/}")
        assertEquals("@setAll(true, {include: /^foo$/})", setAllActions.toString())

        val toggleAllAction = Action.toggleAll("{include: /^foo$/}")
        assertEquals("@toggleAll({include: /^foo$/})", toggleAllAction.toString())

        val getAction = Action.get(::getUsers)
        assertEquals("@get('/users')", getAction.toString())

        val patchAction = Action.patch(::patchUsers)
        assertEquals("@patch('/users')", patchAction.toString())

        val postAction = Action.post(::createUser)
        assertEquals("@post('/users')", postAction.toString())

        val putAction = Action.put(::editUser)
        assertEquals("@put('/users/{id}')", putAction.toString())

        val deleteAction = Action.delete(::deleteUser)
        assertEquals("@delete('/users/{id}')", deleteAction.toString())
    }

    @Path("/users")
    fun getUsers() {}

    @Path("/users")
    fun patchUsers() {}

    @Path("/users")
    fun createUser() { }

    // TODO: Parameterized paths are not supported by the current implementation of DataStarActions, so the path is defined as a string literal.
    @Path("/users/{id}")
    fun editUser() { }

    @Path("/users/{id}")
    fun deleteUser() { }
}
