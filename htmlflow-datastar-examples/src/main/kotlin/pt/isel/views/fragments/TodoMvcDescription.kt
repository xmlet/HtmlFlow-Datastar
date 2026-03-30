package pt.isel.views.fragments

import htmlflow.div
import htmlflow.doc
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.ul

val hfTodoMvcDescription =
    StringBuilder()
        .apply {
            doc {
                div {
                    attrId("description")
                    attrClass("demo-description")

                    h2 { text("TodoMVC - Description") }

                    p { text("This page performs the following fetch requests:") }

                    ul {

                        li {
                            text(
                                "GET /todo-mvc/updates — Streams todo application updates. " +
                                    "Triggered on page load. " +
                                    "Response (text/event-stream): Continuously emits server-sent events that patch '#todo-app' with the current state (tasks, filters, counters) whenever the application state changes.",
                            )
                        }

                        li {
                            text(
                                "PATCH /todo-mvc/-1 — Adds a new task. " +
                                    "Triggered by pressing Enter on the input field. " +
                                    "Request body (application/json): Task title. " +
                                    "Response: No content, but changes are propagated through the '/updates' stream.",
                            )
                        }

                        li {
                            text(
                                "PATCH /todo-mvc/{id} — Edits a task title. " +
                                    "Triggered by pressing Enter after editing. " +
                                    "Path parameter: id (task identifier). " +
                                    "Request body (application/json): Updated task title. " +
                                    "Response: Changes are propagated through the '/updates' stream.",
                            )
                        }

                        li {
                            text(
                                "GET /todo-mvc/{id} — Enters edit mode for a task. " +
                                    "Triggered by double-clicking a task. " +
                                    "Path parameter: id (task identifier). " +
                                    "Response: UI updates are delivered via the '/updates' stream.",
                            )
                        }

                        li {
                            text(
                                "POST /todo-mvc/{id}/toggle — Toggles a task's completion status. " +
                                    "Triggered when clicking the task checkbox. " +
                                    "Path parameter: id (task identifier). " +
                                    "Response: Changes are reflected through the '/updates' stream.",
                            )
                        }

                        li {
                            text(
                                "POST /todo-mvc/-1/toggle — Toggles all tasks' completion status. " +
                                    "Triggered when clicking 'Toggle All'. " +
                                    "Response: All task statuses are updated via the '/updates' stream.",
                            )
                        }

                        li {
                            text(
                                "DELETE /todo-mvc/{id} — Deletes a task. " +
                                    "Triggered when clicking the delete button. " +
                                    "Path parameter: id (task identifier). " +
                                    "Response: Task removal is reflected through the '/updates' stream.",
                            )
                        }

                        li {
                            text(
                                "DELETE /todo-mvc/-1 — Clears all completed tasks. " +
                                    "Triggered when clicking 'Clear completed'. " +
                                    "Response: Completed tasks are removed via the '/updates' stream.",
                            )
                        }

                        li {
                            text(
                                "PUT /todo-mvc/mode/{mode} — Sets the filter mode. " +
                                    "Triggered when clicking filter buttons (All, Pending, Completed). " +
                                    "Path parameter: mode (filter mode). " +
                                    "Response: Filtered task list is delivered via the '/updates' stream.",
                            )
                        }

                        li {
                            text(
                                "PUT /todo-mvc/cancel — Cancels task editing. " +
                                    "Triggered when clicking Cancel or pressing Escape during edit mode. " +
                                    "Response: Edit mode is exited and changes are reflected through the '/updates' stream.",
                            )
                        }

                        li {
                            text(
                                "PUT /todo-mvc/reset — Resets the task list. " +
                                    "Triggered when clicking 'Reset'. " +
                                    "Response: Initial task list is restored via the '/updates' stream.",
                            )
                        }
                    }
                }
            }
        }.toString()
