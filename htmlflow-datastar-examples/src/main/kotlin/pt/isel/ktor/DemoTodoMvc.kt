package pt.isel.ktor

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import htmlflow.div
import htmlflow.view
import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.isel.utils.loadResource
import pt.isel.utils.response
import pt.isel.views.htmlflow.buttonsView
import pt.isel.views.htmlflow.hfTodoMvcView
import pt.isel.views.htmlflow.tasksView
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.map

private val accounts =
    ConcurrentHashMap<UUID, MutableStateFlow<Account>>()

private val html = loadResource("public/html/todo-mvc.html")

fun Route.demoTodoMvc() {
    route("/todo-mvc") {
        get("/html", RoutingContext::getTodoMvcHtml)
        get("/htmlflow", RoutingContext::getTodoMcvHtmlFlow)

        get("/updates") { getUpdates(accountFlow()) }
        post("/-1/toggle") { toggleAll(accountFlow()) }
        patch("/-1") { addTask(accountFlow()) }
        get("/{id}") { setEditMode(accountFlow()) }
        post("/{id}/toggle") { toggleTask(accountFlow()) }
        delete("/{id}") { deleteTaskById(accountFlow()) }
        patch("/{id}") { updateTask(accountFlow()) }
        put("/cancel") { cancelEditMode(accountFlow()) }

        put("/mode/0") { setMode(accountFlow(), Mode.ALL) }
        put("/mode/1") { setMode(accountFlow(), Mode.PENDING) }
        put("/mode/2") { setMode(accountFlow(), Mode.DONE) }
        delete("/-1") { deleteToggledTasks(accountFlow()) }
        put("/reset") { resetTasks(accountFlow()) }
    }
}

private val initialTasks =
    listOf(
        Task(0, "Learn any backend language", Status.PENDING, false),
        Task(1, "Learn Datastar", Status.PENDING, false),
        Task(2, "Learn HTML Flow", Status.PENDING, false),
        Task(3, "Profit", Status.PENDING, false),
    )

private suspend fun RoutingContext.getTodoMvcHtml() {
    call.respondText(html, ContentType.Text.Html)
}

private suspend fun RoutingContext.getTodoMcvHtmlFlow() {
    call.respondText(
        hfTodoMvcView.render(
            TodoUiState(
                initialTasks,
                Mode.ALL,
                initialTasks.count {
                    it.status == Status.PENDING
                },
            ),
        ),
        ContentType.Text.Html,
    )
}

data class TodoUiState(
    val tasks: List<Task>,
    val mode: Mode,
    val pendingCount: Int,
)

private suspend fun RoutingContext.getUpdates(account: MutableStateFlow<Account>) {
    call.respondTextWriter(
        status = HttpStatusCode.OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))

        account.collect { event ->
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

            generator.patchElements(hfTasksView)
        }
    }
}

private suspend fun RoutingContext.cancelEditMode(account: MutableStateFlow<Account>) {
    account.emit(
        account.value.copy(
            tasks = account.value.tasks.map { if (it.editing) it.copy(editing = false) else it },
        ),
    )

    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.updateTask(account: MutableStateFlow<Account>) {
    val taskId =
        call.parameters["id"]?.toIntOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

    val (input) = Json.decodeFromString<TodoMvcSignals>(call.receiveText())

    account.emit(
        account.value.copy(
            tasks =
                account.value.tasks.map {
                    if (it.id == taskId) it.copy(title = input, editing = false) else it
                },
        ),
    )

    call.respond(HttpStatusCode.NoContent)
}

@Serializable
private data class TodoMvcSignals(
    val input: String,
)

private suspend fun RoutingContext.toggleAll(account: MutableStateFlow<Account>) {
    val allDone = account.value.tasks.all { it.status == Status.DONE }

    account.emit(
        account.value.copy(
            tasks =
                account.value.tasks.map {
                    it.copy(status = if (allDone) Status.PENDING else Status.DONE)
                },
        ),
    )
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.addTask(account: MutableStateFlow<Account>) {
    val (input) = Json.decodeFromString<TodoMvcSignals>(call.receiveText())
    val newTask =
        Task(
            id =
                account.value.tasks
                    .maxOfOrNull { it.id }
                    ?.inc() ?: 0,
            title = input,
            status = Status.PENDING,
        )
    account.emit(account.value.copy(tasks = account.value.tasks + newTask))
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.setEditMode(account: MutableStateFlow<Account>) {
    val taskId =
        call.parameters["id"]?.toIntOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

    account.emit(
        account.value.copy(
            tasks =
                account.value.tasks.map { task ->
                    task.copy(editing = task.id == taskId)
                },
        ),
    )

    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.toggleTask(account: MutableStateFlow<Account>) {
    val id =
        call.parameters["id"]?.toIntOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

    account.emit(
        account.value.copy(
            tasks =
                account.value.tasks.map {
                    if (it.id == id) it.copy(status = it.status.opposite()) else it
                },
        ),
    )

    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.deleteTaskById(account: MutableStateFlow<Account>) {
    val taskId =
        call.parameters["id"]?.toIntOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

    account.emit(
        account.value.copy(tasks = account.value.tasks.filterNot { it.id == taskId }),
    )

    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.deleteToggledTasks(account: MutableStateFlow<Account>) {
    account.emit(
        account.value.copy(tasks = account.value.tasks.filterNot { it.status == Status.DONE }),
    )
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.setMode(
    account: MutableStateFlow<Account>,
    mode: Mode,
) {
    account.emit(account.value.copy(mode = mode))
    call.respond(HttpStatusCode.NoContent)
}

private suspend fun RoutingContext.resetTasks(account: MutableStateFlow<Account>) {
    account.emit(account.value.copy(tasks = initialTasks))
    call.respond(HttpStatusCode.NoContent)
}

data class Task(
    val id: Int,
    val title: String,
    val status: Status,
    val editing: Boolean = false,
)

enum class Status {
    DONE,
    PENDING,
    ;

    fun opposite(): Status =
        when (this) {
            DONE -> PENDING
            PENDING -> DONE
        }
}

data class Account(
    val id: UUID,
    val tasks: List<Task>,
    val mode: Mode = Mode.ALL,
)

enum class Mode { ALL, PENDING, DONE }

private const val ACCOUNT_COOKIE = "todo-account-id"

private fun RoutingContext.accountFlow(): MutableStateFlow<Account> {
    val call = call

    val accountId =
        call.request.cookies[ACCOUNT_COOKIE]
            ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            ?: UUID.randomUUID()

    val account =
        accounts.computeIfAbsent(accountId) {
            MutableStateFlow(Account(id = accountId, tasks = initialTasks))
        }

    if (call.request.cookies[ACCOUNT_COOKIE] == null) {
        call.response.cookies.append(
            Cookie(
                name = ACCOUNT_COOKIE,
                value = accountId.toString(),
                httpOnly = true,
                path = "/",
                maxAge = 60 * 60, // 1 hour
            ),
        )
    }

    return account
}
