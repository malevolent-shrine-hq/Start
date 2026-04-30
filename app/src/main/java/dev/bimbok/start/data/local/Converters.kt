package dev.bimbok.start.data.local

import androidx.room.TypeConverter
import dev.bimbok.start.data.local.entities.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }
}
