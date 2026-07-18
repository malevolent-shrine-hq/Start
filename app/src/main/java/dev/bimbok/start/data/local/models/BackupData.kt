package dev.bimbok.start.data.local.models

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val tasks: List<BackupTask>,
    val tags: List<BackupTag>,
    val subTasks: List<BackupSubTask>,
    val taskTagCrossRefs: List<BackupTaskTagCrossRef>
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
data class BackupTag(
    val id: Long,
    val name: String,
    val color: Int
)

@Serializable
data class BackupSubTask(
    val id: Long,
    val taskId: Long,
    val title: String,
    val isCompleted: Boolean
)

@Serializable
data class BackupTaskTagCrossRef(
    val taskId: Long,
    val tagId: Long
)
