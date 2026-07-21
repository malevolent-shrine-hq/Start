package dev.bimbok.start.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.bimbok.start.data.local.dao.TaskWithSubtasks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    taskWithSubtasks: TaskWithSubtasks,
    onToggleCompletion: (Boolean) -> Unit,
    onDeleteRequest: () -> Unit,
    onEdit: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val task = taskWithSubtasks.task
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDeleteRequest()
                false // Wait for dialog confirmation
            } else false
        }
    )

    // Reset swipe state if dialog is cancelled
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                    else -> Color.Transparent
                }, label = "color"
            )
            val iconAlpha by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1f else 0f,
                label = "iconAlpha"
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp)
                    .background(color, MaterialTheme.shapes.medium)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete, 
                    contentDescription = "Delete", 
                    tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = iconAlpha)
                )
            }
        },
        modifier = modifier
    ) {
        Surface(
            onClick = onClick,
            shape = MaterialTheme.shapes.large,
            // Solid Retro Terminal Surface
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
            ),
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onToggleCompletion,
                    modifier = Modifier.scale(1.2f),
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted) 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) 
                            else MaterialTheme.colorScheme.onSurface
                    )
                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Task",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
