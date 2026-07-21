package dev.bimbok.start.data.repository

import dev.bimbok.start.data.local.dao.TodoDao
import dev.bimbok.start.data.local.dao.TaskWithSubtasks
import dev.bimbok.start.data.local.entities.Task
import dev.bimbok.start.data.local.entities.SubTask
import dev.bimbok.start.data.local.entities.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    val allTasks: Flow<List<TaskWithSubtasks>> = todoDao.getAllTasks()

    fun getTaskById(taskId: Long): Flow<TaskWithSubtasks?> = todoDao.getTaskById(taskId)

    suspend fun insertTask(task: Task) = todoDao.insertTask(task)

    suspend fun updateTask(task: Task) = todoDao.updateTask(task)

    suspend fun deleteTask(task: Task) = todoDao.deleteTask(task)

    suspend fun insertSubTask(subTask: SubTask) = todoDao.insertSubTask(subTask)

    suspend fun updateSubTask(subTask: SubTask) = todoDao.updateSubTask(subTask)

    suspend fun deleteSubTask(subTask: SubTask) = todoDao.deleteSubTask(subTask)

    // Notes
    val allNotes: Flow<List<Note>> = todoDao.getAllNotes()
    
    suspend fun insertNote(note: Note) = todoDao.insertNote(note)
    
    suspend fun updateNote(note: Note) = todoDao.updateNote(note)
    
    suspend fun deleteNote(note: Note) = todoDao.deleteNote(note)

    suspend fun deleteAllData() {
        todoDao.deleteAllTasks()
        todoDao.deleteAllNotes()
    }

    suspend fun getAllTasksDirect() = todoDao.getAllTasksDirect()
    suspend fun getAllNotesDirect() = todoDao.getAllNotesDirect()
    suspend fun getAllSubTasksDirect() = todoDao.getAllSubTasksDirect()

    suspend fun restoreData(tasks: List<Task>, notes: List<Note>, subTasks: List<SubTask>) {
        todoDao.deleteAllTasks()
        todoDao.deleteAllNotes()
        todoDao.insertTasks(tasks)
        todoDao.insertNotes(notes)
        todoDao.insertSubTasks(subTasks)
    }
}
