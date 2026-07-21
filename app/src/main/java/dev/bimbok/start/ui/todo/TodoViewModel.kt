package dev.bimbok.start.ui.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bimbok.start.data.local.dao.TaskWithSubtasks
import dev.bimbok.start.data.local.entities.Priority
import dev.bimbok.start.data.local.entities.Task
import dev.bimbok.start.data.repository.TodoRepository
import dev.bimbok.start.notifications.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TodoUiState {
    object Loading : TodoUiState
    data class Success(val tasks: List<TaskWithSubtasks>) : TodoUiState
    data class Error(val message: String) : TodoUiState
}

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    val uiState: StateFlow<TodoUiState> = repository.allTasks
        .map { tasks -> TodoUiState.Success(tasks) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TodoUiState.Loading
        )

    fun addTask(title: String, description: String, dueDate: Long? = null) {
        viewModelScope.launch {
            val taskId = repository.insertTask(Task(title = title, description = description, dueDate = dueDate))
            if (dueDate != null) {
                val task = Task(id = taskId, title = title, description = description, dueDate = dueDate)
                notificationHelper.scheduleTaskReminder(task)
            }
        }
    }

    fun toggleTaskCompletion(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = isCompleted, updatedAt = System.currentTimeMillis()))
            if (isCompleted) {
                notificationHelper.cancelTaskReminder(task.id)
            } else {
                notificationHelper.scheduleTaskReminder(task)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            notificationHelper.cancelTaskReminder(task.id)
        }
    }

    fun updateTask(task: Task, title: String, description: String, dueDate: Long? = null) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                title = title,
                description = description,
                dueDate = dueDate,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateTask(updatedTask)
            notificationHelper.cancelTaskReminder(task.id)
            if (dueDate != null && !updatedTask.isCompleted) {
                notificationHelper.scheduleTaskReminder(updatedTask)
            }
        }
    }
}
