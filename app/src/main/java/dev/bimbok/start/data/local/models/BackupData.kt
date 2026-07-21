package dev.bimbok.start.data.local.models

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val tasks: List<BackupTask>,
    val notes: List<BackupNote>,
    val subTasks: List<BackupSubTask>
)

@Serializable
data class BackupTask(
    val id: Long,
    val title: String,
    val description: String,
    val priority: String,
    val dueDate: Long?,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class BackupNote(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class BackupSubTask(
    val id: Long,
    val taskId: Long,
    val title: String,
    val isCompleted: Boolean
)
