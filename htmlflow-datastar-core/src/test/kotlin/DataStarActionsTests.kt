import htmlflow.div
import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import pt.isel.datastar.expressions.DataStarAction
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
                            val peekDataStarAction = DataStarAction.peek("() => $bar")
                            assertEquals("@peek(() => $bar)", peekDataStarAction.toString())
                        }
                    }
                }
            }

        val setAllActions = DataStarAction.setAll(true, "{include: /^foo$/}")
        assertEquals("@setAll(true, {include: /^foo$/})", setAllActions.toString())

        val toggleAllDataStarAction = DataStarAction.toggleAll("{include: /^foo$/}")
        assertEquals("@toggleAll({include: /^foo$/})", toggleAllDataStarAction.toString())

        val getDataStarAction = DataStarAction.get(::getUsers)
        assertEquals("@get('/users')", getDataStarAction.toString())

        val patchDataStarAction = DataStarAction.patch(::patchUsers)
        assertEquals("@patch('/users')", patchDataStarAction.toString())

        val postDataStarAction = DataStarAction.post(::createUser)
        assertEquals("@post('/users')", postDataStarAction.toString())

        val putDataStarAction = DataStarAction.put(::editUser)
        assertEquals("@put('/users')", putDataStarAction.toString())

        val deleteDataStarAction = DataStarAction.delete(::deleteUser)
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
