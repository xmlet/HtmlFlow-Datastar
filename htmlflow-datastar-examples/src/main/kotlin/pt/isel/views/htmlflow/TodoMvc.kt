package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
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
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.events.Blur
import org.xmlet.htmlflow.datastar.events.Click
import org.xmlet.htmlflow.datastar.events.DblClick
import org.xmlet.htmlflow.datastar.events.Keydown
import pt.isel.http4k.cancelEditMode
import pt.isel.http4k.deleteToggledTasks
import pt.isel.http4k.getTodoMvcDescription
import pt.isel.http4k.getUpdates
import pt.isel.http4k.mode0
import pt.isel.http4k.mode1
import pt.isel.http4k.mode2
import pt.isel.http4k.resetTasks
import pt.isel.http4k.toggleAllCheck
import pt.isel.ktor.Status
import pt.isel.ktor.TodoUiState

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
                    attrId("description")
                    dataInit { get(::getTodoMvcDescription) }
                }
                div {
                    attrId("app")
                    dataInit { get(::getUpdates) }
                    div {
                        attrId("todo-app")
                        tasksView()
                        buttonsView()
                    }
                }
            }
        }
    }

fun Div<*>.tasksView(): HtmlView<TodoUiState> =
    view<TodoUiState> {
        section {
            attrId("todo-mvc")
            dyn { todoUiState: TodoUiState ->
                header {
                    attrId("todo-header")
                    input {
                        attrType(EnumTypeInputType.CHECKBOX)
                        dataOn(Click) {
                            post(::toggleAllCheck)
                            modifiers { prevent() }
                        }
                        dataInit { +(if (todoUiState.pendingCount == 0) "el.checked = true" else "el.checked = false") }
                    }
                    input {
                        attrId("new-todo")
                        attrType(EnumTypeInputType.TEXT)
                        attrPlaceholder("What needs to be done?")
                        if (todoUiState.tasks.none { it.editing }) {
                            val input = dataBind("input")
                            dataOn(Keydown) {
                                +$$"if (evt.key !== 'Enter' || !$input.trim()) return"
                                patch("/todo-mvc/-1")
                                input.setValue("''")
                            }
                        }
                    }
                }
                ul {
                    attrId("todo-list")
                    todoUiState.tasks.forEach { task ->
                        li {
                            addAttr("role", "button")
                            attrTabIndex(0)
                            dataOn(DblClick) {
                                +"evt.target === el"
                                get("/todo-mvc/${task.id}")
                            }
                            if (!task.editing) {
                                input {
                                    attrId("todo-checkbox-${task.id}")
                                    attrType(EnumTypeInputType.CHECKBOX)
                                    dataInit { +if (task.status == Status.DONE) "el.checked = true" else "el.checked = false" }
                                    dataOn(Click) {
                                        post("/todo-mvc/${task.id}/toggle")
                                        modifiers { prevent() }
                                    }
                                }
                                label {
                                    attrFor("todo-checkbox-${task.id}")
                                    text(task.title)
                                }
                                button {
                                    attrClass("error small")
                                    dataOn(Click) {
                                        delete("/todo-mvc/${task.id}")
                                    }
                                }
                            } else {
                                input {
                                    attrType(EnumTypeInputType.TEXT)
                                    attrValue(task.title)
                                    val input = dataSignal("input", task.title)
                                    dataBind(input)
                                    dataInit { +"el.focus()" }
                                    dataOn(Blur) {
                                        put(::cancelEditMode)
                                    }
                                    dataOn(Keydown) {
                                        val js =
                                            $$"""
                                                if (evt.key === 'Escape') {
                                                    el.blur();
                                                } else if (evt.key === 'Enter' && $input.trim()) {
                                                    @patch('/todo-mvc/$${task.id}');
                                                }
                                            """.trimIndent()
                                        +js
                                    }
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
            dataOn(Click) {
                put(::mode0)
            }
            text("All")
        }
        button {
            attrClass("small")
            dataOn(Click) {
                put(::mode1)
            }
            text("Pending")
        }
        button {
            attrClass("small")
            dataOn(Click) {
                put(::mode2)
            }
            text("Completed")
        }
        button {
            attrClass("error small")
            dataOn(Click) {
                delete(::deleteToggledTasks)
            }
            text("Delete")
        }
        button {
            attrClass("warning small")
            dataOn(Click) {
                put(::resetTasks)
            }
            text("Reset")
        }
    }
