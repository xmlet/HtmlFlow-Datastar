import htmlflow.div
import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.builders.ExpressionBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class DataStarActionsTests {
    @Test
    fun `actions toString() must return correct js string `() {
        val handler = ExpressionBuilder()
        StringBuilder()
            .apply {
                doc {
                    html {
                        div {
                            val bar = dataSignal("bar", "initialValue")
                            val peekDataStarAction = handler.peek("() => $bar")
                            assertEquals("@peek(() => $bar)", peekDataStarAction.syntax)
                        }
                    }
                }
            }

        val setAllActions = handler.setAll(true, "{include: /^foo$/}")
        assertEquals("@setAll(true, {include: /^foo$/})", setAllActions.syntax)

        val toggleAllDataStarAction = handler.toggleAll("{include: /^foo$/}")
        assertEquals("@toggleAll({include: /^foo$/})", toggleAllDataStarAction.syntax)

        val getDataStarAction = handler.get(::getUsers)
        assertEquals("@get('/users')", getDataStarAction.syntax)

        val patchDataStarAction = handler.patch(::patchUsers)
        assertEquals("@patch('/users')", patchDataStarAction.syntax)

        val postDataStarAction = handler.post(::createUser)
        assertEquals("@post('/users')", postDataStarAction.syntax)

        val putDataStarAction = handler.put(::editUser)
        assertEquals("@put('/users')", putDataStarAction.syntax)

        val deleteDataStarAction = handler.delete(::deleteUser)
        assertEquals("@delete('/users')", deleteDataStarAction.syntax)

        val postPathDataStarAction = handler.post("/users")
        assertEquals("@post('/users')", postPathDataStarAction.syntax)

        val putPathDataStarAction = handler.put("/users")
        assertEquals("@put('/users')", putPathDataStarAction.syntax)

        val patchPathDataStarAction = handler.patch("/users")
        assertEquals("@patch('/users')", patchPathDataStarAction.syntax)
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
