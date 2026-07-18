package dev.bimbok.start.ui.settings.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bimbok.start.data.local.entities.Priority
import dev.bimbok.start.data.local.entities.SubTask
import dev.bimbok.start.data.local.entities.Tag
import dev.bimbok.start.data.local.entities.Task
import dev.bimbok.start.data.local.entities.TaskTagCrossRef
import dev.bimbok.start.data.local.models.AppTheme
import dev.bimbok.start.data.local.models.BackupData
import dev.bimbok.start.data.local.models.BackupSubTask
import dev.bimbok.start.data.local.models.BackupTag
import dev.bimbok.start.data.local.models.BackupTask
import dev.bimbok.start.data.local.models.BackupTaskTagCrossRef
import dev.bimbok.start.data.preferences.SettingsManager
import dev.bimbok.start.data.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: TodoRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    val theme: StateFlow<AppTheme> = settingsManager.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.GRUVBOX
        )

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsManager.setTheme(theme)
        }
    }

    fun backupData(context: Context, uri: Uri, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val tasks = repository.getAllTasksDirect().map { 
                    BackupTask(it.id, it.title, it.description, it.priority.name, it.dueDate, it.isCompleted, it.createdAt, it.updatedAt) 
                }
                val tags = repository.getAllTagsDirect().map { BackupTag(it.id, it.name, it.color) }
                val subTasks = repository.getAllSubTasksDirect().map { BackupSubTask(it.id, it.taskId, it.title, it.isCompleted) }
                val crossRefs = repository.getAllCrossRefsDirect().map { BackupTaskTagCrossRef(it.taskId, it.tagId) }

                val backup = BackupData(tasks, tags, subTasks, crossRefs)
                val json = Json.encodeToString(backup)

                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { 
                        it.write(json.toByteArray())
                    }
                }
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    fun restoreData(context: Context, uri: Uri, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val json = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                } ?: throw Exception("Failed to read file")

                val backup = Json.decodeFromString<BackupData>(json)

                val tasks = backup.tasks.map { 
                    Task(it.id, it.title, it.description, Priority.valueOf(it.priority), it.dueDate, it.isCompleted, it.createdAt, it.updatedAt)
                }
                val tags = backup.tags.map { Tag(it.id, it.name, it.color) }
                val subTasks = backup.subTasks.map { SubTask(it.id, it.taskId, it.title, it.isCompleted) }
                val crossRefs = backup.taskTagCrossRefs.map { TaskTagCrossRef(it.taskId, it.tagId) }

                repository.restoreData(tasks, tags, subTasks, crossRefs)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
}
