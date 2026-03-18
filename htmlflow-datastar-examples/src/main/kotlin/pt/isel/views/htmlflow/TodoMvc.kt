package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.header
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.label
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.section
import org.xmlet.htmlapifaster.span
import org.xmlet.htmlapifaster.strong
import org.xmlet.htmlapifaster.ul
import pt.isel.datastar.expressions.delete
import pt.isel.datastar.expressions.get
import pt.isel.datastar.expressions.post
import pt.isel.datastar.expressions.put
import pt.isel.datastar.expressions.setValue
import pt.isel.datastar.extensions.dataBind
import pt.isel.datastar.extensions.dataInit
import pt.isel.datastar.extensions.dataOn
import pt.isel.datastar.extensions.dataSignal
import pt.isel.ktor.Status
import pt.isel.ktor.TodoUiState
import pt.isel.utils.loadResource

private val description = loadResource("public/html/fragments/todo-mvc-description.html")

val hfTodoMvcView: HtmlView<TodoUiState> =
    view<TodoUiState> {
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
                    attrId("todo-app")
                    tasksView()
                    buttonsView()
                }
                raw(description)
            }
        }
    }

fun Div<*>.tasksView(): HtmlView<TodoUiState> =
    view<TodoUiState> {
        section {
            attrId("todo-mvc")
            dataInit(get(::updates))
            dyn { todoUiState: TodoUiState ->
                header {
                    attrId("todo-header")
                    input {
                        attrType(EnumTypeInputType.CHECKBOX)
                        dataOn("click", post(::minus1Toggle)) {
                            mods { prevent() }
                        }
                        dataInit(if (todoUiState.pendingCount == 0) "el.checked = true" else "el.checked = false")
                    }
                    input {
                        attrId("new-todo")
                        attrType(EnumTypeInputType.TEXT)
                        attrPlaceholder("What needs to be done?")
                        if (todoUiState.tasks.none { it.editing }) {
                            val input = dataBind("input")
                            dataOn(
                                "keydown",
                                "evt.key === 'Enter' && $input.trim() && @patch('/todo-mvc/-1') && ($input = '');",
                            )
                        }
                    }
                }
                ul {
                    attrId("todo-list")
                    todoUiState.tasks.forEach { task ->
                        li {
                            addAttr("role", "button")
                            attrTabIndex(0)
                            dataOn("dblclick", "evt.target === el && @get('/todo-mvc/${task.id}')")
                            if (!task.editing) {
                                input {
                                    attrId("todo-checkbox-${task.id}")
                                    attrType(EnumTypeInputType.CHECKBOX)
                                    dataInit(if (task.status == Status.DONE) "el.checked = true" else "el.checked = false")
                                    dataOn("click", post("/todo-mvc/${task.id}/toggle")) {
                                        mods { prevent() }
                                    }
                                }
                                label {
                                    attrFor("todo-checkbox-${task.id}")
                                    text(task.title)
                                }
                                button {
                                    attrClass("error small")
                                    dataOn("click", delete("/todo-mvc/${task.id}"))
                                }
                            } else {
                                input {
                                    attrType(EnumTypeInputType.TEXT)
                                    attrValue(task.title)
                                    val input = dataSignal("input", task.title)
                                    dataBind(input)
                                    dataInit("el.focus()")
                                    dataOn("blur", put(::cancel))
                                    dataOn(
                                        "keydown",
                                        """
                                        if (evt.key === 'Escape') {
                                        	el.blur();
                                        } else if (evt.key === 'Enter' && $input.trim()) {
                                        	@patch('/todo-mvc/${task.id}');
                                        }
                                        """.trimIndent(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun Div<*>.buttonsView() =
    div {
        attrId("todo-actions")
        span {
            dyn { todoUiState: TodoUiState -> strong { text(todoUiState.pendingCount) } }
            text(" items pending")
        }
        button {
            attrClass("small info")
            dataOn("click", put(::mode0))
            text("All")
        }
        button {
            attrClass("small")
            dataOn("click", put(::mode1))
            text("Pending")
        }
        button {
            attrClass("small")
            dataOn("click", put(::mode2))
            text("Completed")
        }
        button {
            attrClass("error small")
            dataOn("click", delete(::minus1))
            text("Delete")
        }
        button {
            attrClass("warning small")
            dataOn("click", put(::reset))
            text("Reset")
        }
    }

@Path("/todo-mvc/updates")
private fun updates() {}

@Path("/todo-mvc/-1/toggle")
private fun minus1Toggle() {}

@Path("/todo-mvc/-1")
private fun minus1() {}

@Path("/todo-mvc/reset")
private fun reset() {}

@Path("/todo-mvc/cancel")
private fun cancel() {}

@Path("/todo-mvc/mode/0")
private fun mode0() {}

@Path("/todo-mvc/mode/1")
private fun mode1() {}

@Path("/todo-mvc/mode/2")
private fun mode2() {}
