import htmlflow.div
import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import pt.isel.datastar.expressions.delete
import pt.isel.datastar.expressions.get
import pt.isel.datastar.expressions.patch
import pt.isel.datastar.expressions.peek
import pt.isel.datastar.expressions.post
import pt.isel.datastar.expressions.put
import pt.isel.datastar.expressions.setAll
import pt.isel.datastar.expressions.toggleAll
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
                            val peekDataStarAction = peek("() => $bar")
                            assertEquals("@peek(() => $bar)", peekDataStarAction.toString())
                        }
                    }
                }
            }

        val setAllActions = setAll(true, "{include: /^foo$/}")
        assertEquals("@setAll(true, {include: /^foo$/})", setAllActions.toString())

        val toggleAllDataStarAction = toggleAll("{include: /^foo$/}")
        assertEquals("@toggleAll({include: /^foo$/})", toggleAllDataStarAction.toString())

        val getDataStarAction = get(::getUsers)
        assertEquals("@get('/users')", getDataStarAction.toString())

        val patchDataStarAction = patch(::patchUsers)
        assertEquals("@patch('/users')", patchDataStarAction.toString())

        val postDataStarAction = post(::createUser)
        assertEquals("@post('/users')", postDataStarAction.toString())

        val putDataStarAction = put(::editUser)
        assertEquals("@put('/users')", putDataStarAction.toString())

        val deleteDataStarAction = delete(::deleteUser)
        assertEquals("@delete('/users')", deleteDataStarAction.toString())
    }

    @Path("/users")
    fun getUsers() {}

    @Path("/users")
    fun patchUsers() {}

    @Path("/users")
    fun createUser() { }

    @Path("/users")
    fun editUser() { }

    @Path("/users")
    fun deleteUser() { }
}
