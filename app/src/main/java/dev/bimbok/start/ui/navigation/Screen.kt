package dev.bimbok.start.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tag
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Tasks : Screen("tasks", "Tasks", Icons.Default.Checklist)
    object Tags : Screen("tags", "Tags", Icons.Default.Tag)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val navItems = listOf(
    Screen.Tasks,
    Screen.Tags,
    Screen.Settings
)
