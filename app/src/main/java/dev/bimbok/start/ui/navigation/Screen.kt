package dev.bimbok.start.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Tasks : Screen("tasks", "Tasks", Icons.Default.Checklist)
    object Notes : Screen("notes", "Notes", Icons.Default.Description)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val navItems = listOf(
    Screen.Tasks,
    Screen.Notes,
    Screen.Settings
)
