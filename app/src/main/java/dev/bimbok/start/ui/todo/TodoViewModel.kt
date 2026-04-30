package dev.bimbok.start.ui.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bimbok.start.data.local.dao.TaskWithSubtasksAndTags
import dev.bimbok.start.data.local.entities.Task
import dev.bimbok.start.data.repository.TodoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TodoUiState {
    object Loading : TodoUiState
    data class Success(val tasks: List<TaskWithSubtasksAndTags>) : TodoUiState
    data class Error(val message: String) : TodoUiState
}

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    val uiState: StateFlow<TodoUiState> = repository.allTasks
        .map { tasks -> TodoUiState.Success(tasks) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TodoUiState.Loading
        )

    fun addTask(title: String, description: String) {
        viewModelScope.launch {
            repository.insertTask(Task(title = title, description = description))
        }
    }

    fun toggleTaskCompletion(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = isCompleted, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updateTask(task: Task, title: String, description: String) {
        viewModelScope.launch {
            repository.updateTask(task.copy(
                title = title,
                description = description,
                updatedAt = System.currentTimeMillis()
            ))
        }
    }
}
