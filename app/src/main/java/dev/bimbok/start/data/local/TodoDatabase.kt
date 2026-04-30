package dev.bimbok.start.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.bimbok.start.data.local.dao.TodoDao
import dev.bimbok.start.data.local.entities.*

@Database(
    entities = [Task::class, SubTask::class, Tag::class, TaskTagCrossRef::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
