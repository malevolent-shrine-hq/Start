package dev.bimbok.start.data.local.dao

import androidx.room.*
import dev.bimbok.start.data.local.entities.*
import kotlinx.coroutines.flow.Flow

data class TaskWithSubtasks(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val subTasks: List<SubTask>
)

@Dao
interface TodoDao {
    @Transaction
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskWithSubtasks>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<TaskWithSubtasks?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: SubTask)

    @Update
    suspend fun updateSubTask(subTask: SubTask)

    @Delete
    suspend fun deleteSubTask(subTask: SubTask)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksDirect(): List<Task>

    @Query("SELECT * FROM notes")
    suspend fun getAllNotesDirect(): List<Note>

    @Query("SELECT * FROM subtasks")
    suspend fun getAllSubTasksDirect(): List<SubTask>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<Note>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTasks(subTasks: List<SubTask>)
}
