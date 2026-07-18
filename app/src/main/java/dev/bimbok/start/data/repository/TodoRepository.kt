package dev.bimbok.start.data.repository

import dev.bimbok.start.data.local.dao.TodoDao
import dev.bimbok.start.data.local.dao.TaskWithSubtasksAndTags
import dev.bimbok.start.data.local.entities.Task
import dev.bimbok.start.data.local.entities.SubTask
import dev.bimbok.start.data.local.entities.Tag
import dev.bimbok.start.data.local.entities.TaskTagCrossRef
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    val allTasks: Flow<List<TaskWithSubtasksAndTags>> = todoDao.getAllTasks()

    fun getTaskById(taskId: Long): Flow<TaskWithSubtasksAndTags?> = todoDao.getTaskById(taskId)

    suspend fun insertTask(task: Task) = todoDao.insertTask(task)

    suspend fun updateTask(task: Task) = todoDao.updateTask(task)

    suspend fun deleteTask(task: Task) = todoDao.deleteTask(task)

    suspend fun insertSubTask(subTask: SubTask) = todoDao.insertSubTask(subTask)

    suspend fun updateSubTask(subTask: SubTask) = todoDao.updateSubTask(subTask)

    suspend fun deleteSubTask(subTask: SubTask) = todoDao.deleteSubTask(subTask)

    suspend fun insertTag(tag: Tag) = todoDao.insertTag(tag)

    suspend fun deleteTag(tag: Tag) = todoDao.deleteTag(tag)

    val allTags: Flow<List<Tag>> = todoDao.getAllTags()

    suspend fun addTagToTask(taskId: Long, tagId: Long) {
        todoDao.insertTaskTagCrossRef(TaskTagCrossRef(taskId, tagId))
    }

    suspend fun deleteAllData() {
        todoDao.deleteAllTasks()
        todoDao.deleteAllTags()
    }

    suspend fun getAllTasksDirect() = todoDao.getAllTasksDirect()
    suspend fun getAllTagsDirect() = todoDao.getAllTagsDirect()
    suspend fun getAllSubTasksDirect() = todoDao.getAllSubTasksDirect()
    suspend fun getAllCrossRefsDirect() = todoDao.getAllCrossRefsDirect()

    suspend fun restoreData(tasks: List<Task>, tags: List<Tag>, subTasks: List<SubTask>, crossRefs: List<TaskTagCrossRef>) {
        todoDao.deleteAllTasks()
        todoDao.deleteAllTags()
        todoDao.insertTasks(tasks)
        todoDao.insertTags(tags)
        todoDao.insertSubTasks(subTasks)
        todoDao.insertCrossRefs(crossRefs)
    }
}
