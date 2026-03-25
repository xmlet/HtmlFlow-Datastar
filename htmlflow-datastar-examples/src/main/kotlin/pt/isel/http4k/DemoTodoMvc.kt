package pt.isel.http4k

import htmlflow.div
import htmlflow.view
import jakarta.ws.rs.Path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.PolyHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.cookies
import org.http4k.datastar.Element
import org.http4k.routing.bind
import org.http4k.routing.bindSse
import org.http4k.routing.path
import org.http4k.routing.poly
import org.http4k.routing.to
import org.http4k.sse.SseResponse
import org.http4k.sse.sendPatchElements
import pt.isel.ktor.Account
import pt.isel.ktor.Mode
import pt.isel.ktor.Status
import pt.isel.ktor.Task
import pt.isel.ktor.TodoUiState
import pt.isel.utils.EventBus
import pt.isel.utils.loadResource
import pt.isel.views.fragments.hfTodoMvcDescription
import pt.isel.views.htmlflow.buttonsView
import pt.isel.views.htmlflow.hfTodoMvcView
import pt.isel.views.htmlflow.tasksView
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private val html = loadResource("public/html/todo-mvc.html")

private val accounts = ConcurrentHashMap<UUID, EventBus<Account>>()

private val initialTasks =
    listOf(
        Task(0, "Learn any backend language", Status.PENDING, false),
        Task(1, "Learn Datastar", Status.PENDING, false),
        Task(2, "Learn HTML Flow", Status.PENDING, false),
        Task(3, "Profit", Status.PENDING, false),
    )

@Serializable
private data class TodoMvcSignals(
    val input: String,
)

fun demoTodoMvc(): PolyHandler =
    poly(
        "/html" bind Method.GET to ::getTodoMvcHtml,
        "/htmlflow" bind Method.GET to ::getTodoMvcHtmlFlow,
        "/updates" bindSse Method.GET to ::getUpdates,
        "/-1/toggle" bind Method.POST to ::toggleAll,
        "/-1" bind Method.PATCH to ::addTask,
        "/-1" bind Method.DELETE to ::deleteToggledTasks,
        "/cancel" bind Method.PUT to ::cancelEditMode,
        "/mode/0" bind Method.PUT to ::mode0,
        "/mode/1" bind Method.PUT to ::mode1,
        "/mode/2" bind Method.PUT to ::mode2,
        "/reset" bind Method.PUT to ::resetTasks,
        "/description" bindSse Method.GET to ::getTodoMvcDescription,
        "/{id}/toggle" bind Method.POST to ::toggleTask,
        "/{id}" bindSse Method.GET to ::setEditMode,
        "/{id}" bind Method.DELETE to ::deleteTaskById,
        "/{id}" bind Method.PATCH to ::updateTask,
    )

private fun getTodoMvcHtml(req: Request): Response {
    val account = accountFlow(req)
    val current = account.currentValue!!
    return Response(OK)
        .body(html)
        .header("Content-Type", "text/html")
        .withAccountCookie(req, current)
}

private fun getTodoMvcHtmlFlow(req: Request): Response {
    val account = accountFlow(req)
    val current = account.currentValue!!
    val state =
        TodoUiState(
            tasks = current.tasks,
            mode = current.mode,
            pendingCount = current.tasks.count { it.status == Status.PENDING },
        )
    return Response(OK)
        .body(hfTodoMvcView.render(state))
        .header("Content-Type", "text/html")
        .withAccountCookie(req, current)
}

@Path("/todo-mvc/updates")
fun getUpdates(req: Request): SseResponse {
    val account = accountFlow(req)
    val queue = account.subscribe()
    return SseResponse { sse ->
        sse.onClose { account.unsubscribe(queue) }
        while (true) {
            try {
                val event = queue.take()
                val showingTasks =
                    when (event.mode) {
                        Mode.PENDING -> event.tasks.filter { it.status == Status.PENDING }
                        Mode.DONE -> event.tasks.filter { it.status == Status.DONE }
                        Mode.ALL -> event.tasks
                    }
                val todoUiState =
                    TodoUiState(
                        tasks = showingTasks,
                        mode = event.mode,
                        pendingCount = event.tasks.count { it.status == Status.PENDING },
                    )
                val hfTasksView =
                    view<TodoUiState> {
                        div {
                            attrId("todo-app")
                            tasksView()
                            buttonsView()
                        }
                    }.render(todoUiState)

                sse.sendPatchElements(elements = listOf(Element.of(hfTasksView)))
            } catch (_: InterruptedException) {
                account.unsubscribe(queue)
                break
            }
        }
    }
}

@Path("/todo-mvc/-1/toggle")
fun toggleAll(req: Request): Response {
    val account = accountFlow(req)
    val current = account.currentValue!!
    val allDone = current.tasks.all { it.status == Status.DONE }
    account.publish(
        current.copy(
            tasks =
                current.tasks.map {
                    it.copy(status = if (allDone) Status.PENDING else Status.DONE)
                },
        ),
    )
    return Response(NO_CONTENT)
}

private fun addTask(req: Request): Response {
    val body = req.bodyString()
    val (input) = Json.decodeFromString<TodoMvcSignals>(body)
    val account = accountFlow(req)
    val current = account.currentValue!!
    val newTask =
        Task(
            id = current.tasks.maxOfOrNull { it.id }?.inc() ?: 0,
            title = input,
            status = Status.PENDING,
        )
    account.publish(current.copy(tasks = current.tasks + newTask))
    return Response(NO_CONTENT)
}

@Path("/todo-mvc/{id}")
fun setEditMode(req: Request): SseResponse {
    val taskId = req.path("id")?.toIntOrNull() ?: return SseResponse { it.close() }
    val account = accountFlow(req)
    val current = account.currentValue!!
    account.publish(
        current.copy(
            tasks =
                current.tasks.map { task ->
                    task.copy(editing = task.id == taskId)
                },
        ),
    )
    return SseResponse { sse -> sse.close() }
}

private fun toggleTask(req: Request): Response {
    val id = req.path("id")?.toIntOrNull() ?: return Response(BAD_REQUEST)
    val account = accountFlow(req)
    val current = account.currentValue!!
    account.publish(
        current.copy(
            tasks =
                current.tasks.map {
                    if (it.id == id) it.copy(status = it.status.opposite()) else it
                },
        ),
    )
    return Response(NO_CONTENT)
}

private fun deleteTaskById(req: Request): Response {
    val taskId = req.path("id")?.toIntOrNull() ?: return Response(BAD_REQUEST)
    val account = accountFlow(req)
    val current = account.currentValue!!
    account.publish(
        current.copy(tasks = current.tasks.filterNot { it.id == taskId }),
    )
    return Response(NO_CONTENT)
}

private fun updateTask(req: Request): Response {
    val taskId = req.path("id")?.toIntOrNull() ?: return Response(BAD_REQUEST)
    val (input) = Json.decodeFromString<TodoMvcSignals>(req.bodyString())
    val account = accountFlow(req)
    val current = account.currentValue!!
    account.publish(
        current.copy(
            tasks =
                current.tasks.map {
                    if (it.id == taskId) it.copy(title = input, editing = false) else it
                },
        ),
    )
    return Response(NO_CONTENT)
}

@Path("/todo-mvc/cancel")
fun cancelEditMode(req: Request): Response {
    val account = accountFlow(req)
    val current = account.currentValue!!
    account.publish(
        current.copy(
            tasks = current.tasks.map { if (it.editing) it.copy(editing = false) else it },
        ),
    )
    return Response(NO_CONTENT)
}

@Path("/todo-mvc/-1")
fun deleteToggledTasks(req: Request): Response {
    val account = accountFlow(req)
    val current = account.currentValue!!
    account.publish(
        current.copy(tasks = current.tasks.filterNot { it.status == Status.DONE }),
    )
    return Response(NO_CONTENT)
}

private fun setMode(
    req: Request,
    mode: Mode,
): Response {
    val account = accountFlow(req)
    val current = account.currentValue!!
    account.publish(current.copy(mode = mode))
    return Response(NO_CONTENT)
}

@Path("/todo-mvc/mode/0")
fun mode0(req: Request): Response = setMode(req, Mode.ALL)

@Path("/todo-mvc/mode/1")
fun mode1(req: Request): Response = setMode(req, Mode.PENDING)

@Path("/todo-mvc/mode/2")
fun mode2(req: Request): Response = setMode(req, Mode.DONE)

@Path("/todo-mvc/reset")
fun resetTasks(req: Request): Response {
    val account = accountFlow(req)
    val current = account.currentValue!!
    account.publish(current.copy(tasks = initialTasks))
    return Response(NO_CONTENT)
}

@Path("/todo-mvc/description")
fun getTodoMvcDescription(req: Request): SseResponse =
    SseResponse { sse ->
        sse.sendPatchElements(elements = listOf(Element.of(hfTodoMvcDescription)))
        sse.close()
    }

private const val ACCOUNT_COOKIE = "todo-account-id"

private fun accountFlow(req: Request): EventBus<Account> {
    val existingId =
        req
            .cookies()
            .find { it.name == ACCOUNT_COOKIE }
            ?.value
            ?.let { runCatching { UUID.fromString(it) }.getOrNull() }

    val accountId = existingId ?: UUID.randomUUID()
    return accounts.computeIfAbsent(accountId) {
        EventBus(Account(id = accountId, tasks = initialTasks))
    }
}

private fun Response.withAccountCookie(
    req: Request,
    account: Account,
): Response =
    if (req.cookies().none { it.name == ACCOUNT_COOKIE }) {
        this.cookie(
            Cookie(
                name = ACCOUNT_COOKIE,
                value = account.id.toString(),
                path = "/",
                maxAge = 3600,
                httpOnly = true,
            ),
        )
    } else {
        this
    }
